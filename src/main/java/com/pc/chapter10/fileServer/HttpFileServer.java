package com.pc.chapter10.fileServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 *
 * @author pengchao
 * @since 14:26 2019-09-17
 */
public class HttpFileServer {

    //http://localhost:8080/src/file/
    private static final String DEFAULT_URL = "/src/";

    public void run(final int port, final String url) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //请求消息解码器
                            socketChannel.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            //作用是将多个消息转换成单一的FullHttpRequest或者FullHttpResponse,原因是http解码器在每个http消息中会生成多个消息对象
                            socketChannel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            //应答消息编码器
                            socketChannel.pipeline().addLast("http-encoder",new HttpResponseEncoder());
                            //作用是支持异步发送大的码流，如文件，但不占用过多的内存，防止Java内存溢出
                            socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            //业务处理
                            socketChannel.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));
                        }
                    });

            ChannelFuture future = bootstrap.bind("127.0.0.1",port).sync();
            System.out.println("HTTP 文件目录服务器启动， 网址是： http://127.0.0.1:"+port+url);

            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        new HttpFileServer().run(port, DEFAULT_URL);
    }
}
