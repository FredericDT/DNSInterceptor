package onl.fdt.java.cs.network.Listener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import onl.fdt.java.cs.network.Client.UDPClient;
import onl.fdt.java.cs.network.Config;
import onl.fdt.java.cs.network.DNSPacket.DNSPacket;
import onl.fdt.java.cs.network.DNSPacket.DNSPacketImpl;
import onl.fdt.java.cs.network.DNSPacket.enums.CLASS;
import onl.fdt.java.cs.network.DNSPacket.enums.TYPE;
import onl.fdt.java.cs.network.DNSPacket.sections.QuestionSection;
import onl.fdt.java.cs.network.DNSPacket.sections.ResourceRecord;
import onl.fdt.java.cs.network.DNSPacket.sections.ResourceRecordImpl;
import onl.fdt.java.cs.network.DNSPacket.sections.resourcerecord.RDATA.RDATA_SOA;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class DNSPacketHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger LOGGER = Logger.getLogger(DNSPacketHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {

        ByteBuf in = datagramPacket.content();

        // Print debug packet info
        StringBuilder sb = new StringBuilder("Received Message: ");
        for (int i = 0; i < in.readableBytes(); ++i) {
            sb.append(String.format("%02x", in.getByte(i)));
        }
        LOGGER.debug(sb.toString());

        LOGGER.debug(in.getShort(2) + " " + in.getShort(4));

        try {
            // intercept
            DNSPacket packet = new DNSPacketImpl(in);
            if (packet.getQDCOUNT() != 0) {
                QuestionSection qs = packet.getQuestionSectionList().get(0);
                // Process the question section like BIND, refer https://www.isc.org/downloads/bind/
                // which means, support only one query per packet
                String domain = qs.getDomainName();
                LOGGER.debug("querying domain: " + domain);
                if (Config.getInterceptDomainIPMap().containsKey(domain)) {
                    LOGGER.debug(domain + " found in interceptDomainIPMap");
                    byte[] address = Config.getInterceptDomainIPMap().get(domain);
                    DNSPacket rPacket = new DNSPacketImpl(in);
                    rPacket.getRawByteBuf().writerIndex(12 + qs.getFullByteLength());
                    rPacket.getRawByteBuf().setByte(2, 0x81);
                    if (Arrays.equals(address, Config.BLOCK_DOMAIN_ADDRESS)) {
                        // Set domain status not found
                        // DO NOT ASK ME WHY
                        rPacket.getRawByteBuf().setByte(3, 0xa3);
                        rPacket.setARCOUNT((short) 1);
                        rPacket.getRawByteBuf().writeBytes(new ResourceRecordImpl(0, (short) 0, TYPE.SOA, CLASS.IN, 0, (short) 0x40, RDATA_SOA.DOMAIN_NOT_FOUND.getFullRDATABytes()).getFullBytes());
                    } else {
                        // Set pre-set ip address
                        // DO NOT ASK ME WHY
                        rPacket.getRawByteBuf().setByte(3, 0x80);
                        rPacket.setANCOUNT((short) 1);
                        rPacket.getRawByteBuf().writeBytes(new ResourceRecordImpl(0, (short) 0xc00c, TYPE.A, CLASS.IN, 0, (short) 4, address).getFullBytes());
                    }
                    channelHandlerContext.writeAndFlush(new DatagramPacket(rPacket.getRawByteBuf(), datagramPacket.sender()));
                } else {
                    LOGGER.debug(domain + " not in interceptDoaminIPMap relay it");
                    relay(channelHandlerContext, datagramPacket, in);
                }
            } else {
                // QDCOUNT == 0, cannot parse, must relay
                relay(channelHandlerContext, datagramPacket, in);
            }
        } catch (Exception e) {
            LOGGER.info("cannot parse this packet, relay it");
            relay(channelHandlerContext, datagramPacket, in);
        }

    }

    /**
     *
     * @param channelHandlerContext
     * @param datagramPacket raw data packet to get sender
     * @param in raw packet data
     * @throws Exception
     *
     * Forward the data packet to upstream DNS server
     * then feed the response to the client
     */
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
