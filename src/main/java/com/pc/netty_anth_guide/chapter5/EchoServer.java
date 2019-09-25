package com.pc.netty_anth_guide.chapter5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 *
 * @author pengchao
 * @since 15:04 2019-09-16
 */
public class EchoServer {

    public void bind(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //定义分隔符
                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                            //单条消息最大长度，超过该长度还没有读到分隔符，就抛出异常，防止内存溢出
                            //第一个解码后就是完整的消息包，这里解码后会过滤分隔符
//                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));

                            //使用FixedLengthFrameDecoder定长解码器替换上面的分隔符解码器
                            socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(20));


                            //第二个解码后就是字符串对象
                            socketChannel.pipeline().addLast(new StringDecoder());
                            //第三个解码器接收到字符串对象，进行处理
                            socketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            //绑定端口，同步等待成功
            ChannelFuture f = bootstrap.bind(port).sync();
            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            //优雅关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        new EchoServer().bind(port);
    }
}
