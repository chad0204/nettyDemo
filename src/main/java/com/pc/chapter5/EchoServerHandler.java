package com.pc.chapter5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author pengchao
 * @since 15:09 2019-09-16
 */
public class EchoServerHandler extends ChannelHandlerAdapter {

    int counter = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        String body = (String) msg;
//        System.out.println("This is "+ ++counter + "times receive client: ["+body+"]");
//        //由于DelimiterBasedFrameDecoder解码器过滤了分隔符，发送客户端前需要加上
//        body += "$_";
//        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
//        ctx.writeAndFlush(echo);

        //测试FixedLengthFrameDecoder，直接打印消息即可,不使用客户端程序，使用telnet来测试。服务端按照20个字符串来截取消息
        System.out.println("Receive client: ["+msg+"]");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
