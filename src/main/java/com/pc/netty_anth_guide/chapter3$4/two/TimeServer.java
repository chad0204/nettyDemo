package com.pc.netty_anth_guide.chapter3$4.two;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author pengchao
 * @since 16:50 2019-09-12
 */
public class TimeServer {

    public void bind(int port) throws Exception {
        //配置服务端的NIO线程组，一个用于服务端接收客户端的连接，一个用于进行SocketChannel的读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();//包含一组NIO线程，专门用于处理网络事件，实际上它们就是Reactor线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //辅助启动类
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childHandler(new ChildChannelHandler());

            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            //优雅退出,释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /*
     *  作用类似于Reactor模式中的handler类，主要用于处理网络IO事件（如日志记录，对消息编解码等）
     */
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            //添加解码器
            /* LineBasedFrameDecoder：它是以换行符为结束标志的解码器，工作原理是依次遍历ByteBuf中的可读字节，
               判断看是否有"\n"，"\r\n"，如果有，就以此位置为结束位置。支持配置最大长度，如果连续读取到最大长度后仍然没有发现换行符
               就会抛出异常。
            */
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            /*
                StringDecoder就是将接收到的对象转换成字符串
             */
            socketChannel.pipeline().addLast(new StringDecoder());
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new TimeServer().bind(port);
    }
}
