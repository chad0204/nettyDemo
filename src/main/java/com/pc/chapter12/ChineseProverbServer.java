package com.pc.chapter12;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 *
 * @author pengchao
 * @since 09:53 2019-09-19
 */
public class ChineseProverbServer {

    public void run(int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            //由于是udp协议所以创建channel时需要通过NioDatagramChannel来创建
            //相比于TCP，UDP不存在客户端与服务端的实际连接，因此不需要为连接（ChannelPipeline）设置handler
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new ChineseProverbServerHandler());

            bootstrap.bind(port).sync().channel().closeFuture().await();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ChineseProverbServer().run(8080);
    }
}
