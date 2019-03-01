package onl.fdt.java.cs.network.Listener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import onl.fdt.java.cs.network.Client.UDPClient;
import org.apache.log4j.Logger;

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

        UDPClient udpClient = new UDPClient(channelHandlerContext, datagramPacket.sender(), in.copy());
        udpClient.run();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(), cause);
        ctx.close();
    }
}
