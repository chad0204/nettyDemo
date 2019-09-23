package com.pc.chapter11;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 *
 * @author pengchao
 * @since 17:05 2019-09-18
 */
public class WebSocketServer {

    public void run(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //将请求和应答消息解码或解码成http消息
                            pipeline.addLast("http-codec", new HttpServerCodec());
                            //将一条http消息的几个部分组合成一条完整的http消息
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                            //支持大码流消息，如向客户端发送h5文件，用于支持浏览器和服务器之间的websocket通信
                            pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                            //业务
                            pipeline.addLast("handler", new WebSocketServerHandler());
                        }
                    });
            Channel channel = bootstrap.bind(port).sync().channel();
            System.out.println("Web socket server started at port "+ port +".");
            System.out.println("Open your browser and navigate to http://localhost:"+port+'/');

            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new WebSocketServer().run(8080);
    }


}
