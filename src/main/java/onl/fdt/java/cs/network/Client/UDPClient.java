package onl.fdt.java.cs.network.Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import onl.fdt.java.cs.network.Config;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

public class UDPClient {

    private static final Logger LOGGER = Logger.getLogger(UDPClient.class);

    private Channel channel;

    final private ChannelHandlerContext ctx;
    final private InetSocketAddress sender;
    final private ByteBuf sendPacketBuf;

    public UDPClient(ChannelHandlerContext ctx, InetSocketAddress sender, ByteBuf sendPacketBuf) {
        this.ctx = ctx;
        this.sender = sender;
        this.sendPacketBuf = sendPacketBuf;
    }

    public ChannelFuture channelWriteAndFlush(ByteBuf byteBuf, final String host, final int port) {
        return this.channel.writeAndFlush(new DatagramPacket(byteBuf, new InetSocketAddress(host, port)));
    }

    public void run() throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new UDPClientHandler(this.ctx, this.sender));
            Channel ch = b.bind(0).sync().channel();

            this.channel = ch;

            this.channelWriteAndFlush(this.sendPacketBuf, Config.TARGET_DNS_SERVER_ADDRESS, Config.TARGET_DNS_SERVER_PORT);
            ch.closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }

}
