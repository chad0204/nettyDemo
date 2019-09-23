package com.pc.chapter7;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author pengchao
 * @since 19:20 2019-09-16
 */
public class SubReqClientHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i = 0; i < 10; i++) {
            ctx.write(subReq(i));
        }
        ctx.flush();
    }

    private SubscribeReq subReq(int i) {
        SubscribeReq subscribeReq = new SubscribeReq();
        subscribeReq.setAddress("杭州市滨江区");
        subscribeReq.setPhoneNumber("187****1234");
        subscribeReq.setProductName("Netty权威指南");
        subscribeReq.setSubReqId(i);
        subscribeReq.setUserName("chao");
        return subscribeReq;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Receive server response: [" + msg + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
