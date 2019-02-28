package onl.fdt.java.cs.network.Client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

public class UDPClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger LOGGER = Logger.getLogger(UDPClient.class);

    private final ChannelHandlerContext ctx;
    private final InetSocketAddress sender;

    public UDPClientHandler(ChannelHandlerContext ctx, InetSocketAddress sender) {
        this.ctx = ctx;
        this.sender = sender;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        ByteBuf in = datagramPacket.content();
        StringBuilder sb = new StringBuilder("Received Message: ");
        for (int i = 0; i < in.readableBytes(); ++i) {
            sb.append(String.format("%02x", in.getByte(i)));
        }
        LOGGER.debug(sb.toString());

        this.ctx.writeAndFlush(new DatagramPacket(in.copy(), sender));

        channelHandlerContext.close();

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(), cause);
        ctx.close();
    }


}
