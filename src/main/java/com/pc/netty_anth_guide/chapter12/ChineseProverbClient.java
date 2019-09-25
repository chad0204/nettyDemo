package com.pc.netty_anth_guide.chapter12;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import java.net.InetSocketAddress;

/**
 * @author pengchao
 * @since 10:22 2019-09-19
 */
public class ChineseProverbClient {

    public void run(int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new ChineseProverbClientHandler());

            Channel channel = bootstrap.bind(0).sync().channel();//不能和服务端8080相同
            //向网段内所有的机器广播消息
            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("谚语字典查询", CharsetUtil.UTF_8),
                    new InetSocketAddress("255.255.255.255",port
            ))).sync();

            //客户端等待15s用于接收服务端的应答消息
            if(! channel.closeFuture().await(15000)) {
                System.out.println("查询超时!");
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ChineseProverbClient().run(8080);
    }
}
