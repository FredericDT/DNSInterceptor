package onl.fdt.java.cs.network.Listener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import onl.fdt.java.cs.network.Client.UDPClient;
import onl.fdt.java.cs.network.Config;
import onl.fdt.java.cs.network.DNSPacket;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class DNSPacketHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger LOGGER = Logger.getLogger(DNSPacketHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {

        ByteBuf in = datagramPacket.content();
        StringBuilder sb = new StringBuilder("Received Message: ");
        for (int i = 0; i < in.readableBytes(); ++i) {
            sb.append(String.format("%02x", in.getByte(i)));
        }
        LOGGER.debug(sb.toString());

        LOGGER.debug(in.getShort(2) + " " + in.getShort(4));

        // intercept
        DNSPacket packet = new DNSPacket(in);
        if (packet.getQDCOUNT() != 0) {
            DNSPacket.QuestionSection qs = packet.getQuestionSectionList().get(0);
            String domain = qs.domainName;
            LOGGER.debug("querying domain: " + domain);
            if (Config.getInterceptDomainIPMap().containsKey(domain)) {
                LOGGER.debug(domain + " found in interceptDomainIPMap");
                byte[] address = Config.getInterceptDomainIPMap().get(domain);
                DNSPacket rPacket = new DNSPacket(in);
//                rPacket.setQRType(DNSPacket.QR.RESPONSE);
                rPacket.getRawByteBuf().writerIndex(12 + qs.fullByteLength);
                rPacket.getRawByteBuf().setByte(2, 0x81);
                if (Arrays.equals(address, Config.BLOCK_DOMAIN_ADDRESS)) {
                    // DO NOT ASK ME WHY
                    rPacket.getRawByteBuf().setByte(3, 0xa3);
                    rPacket.setARCOUNT((short) 1);
                    rPacket.getRawByteBuf().writeBytes(new DNSPacket.ResourceRecord(0, (short) 0, DNSPacket.TYPE.SOA, DNSPacket.CLASS.IN, 0, (short) 0x40, DNSPacket.ResourceRecord.RDATA_SOA.DOMAIN_NOT_FOUND.getFullRDATABytes()).fullBytes);
                } else {
                    // DO NOT ASK ME WHY
//                    rPacket.getRawByteBuf().setByte(2, 0x81);
                    rPacket.getRawByteBuf().setByte(3, 0x80);
                    rPacket.setANCOUNT((short) 1);
                    rPacket.getRawByteBuf().writeBytes(new DNSPacket.ResourceRecord(0, (short) 0xc00c, DNSPacket.TYPE.A, DNSPacket.CLASS.IN, 0, (short) 4, address).fullBytes);
                }
                channelHandlerContext.writeAndFlush(new DatagramPacket(rPacket.getRawByteBuf(), datagramPacket.sender()));
            } else {
                relay(channelHandlerContext, datagramPacket, in);
            }
        } else {
            relay(channelHandlerContext, datagramPacket, in);
        }
    }

    private void relay(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket, ByteBuf in) throws Exception {
        UDPClient udpClient = new UDPClient(channelHandlerContext, datagramPacket.sender(), in.copy());
        udpClient.run();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(), cause);
        ctx.close();
    }
}
