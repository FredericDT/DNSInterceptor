package onl.fdt.java.cs.network.Listener;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import onl.fdt.java.cs.network.Config;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

public class DNSRelayServer {

    private static final Logger LOGGER = Logger.getLogger(DNSRelayServer.class);

    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(bossGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(
                            new ChannelInitializer<NioDatagramChannel>() {
                                @Override
                                public void initChannel(NioDatagramChannel ch) throws Exception {
                                    ch.pipeline().addLast(new DNSPacketHandler());
                                }
                            }
                    );

            ChannelFuture f = b.bind(new InetSocketAddress(Config.getListenAddress(), Config.getListenPort())).sync(); // (7)
            LOGGER.info("port " + Config.getListenPort() + " bind success");

            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
        }
    }
}
