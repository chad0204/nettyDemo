package com.pc.chapter7;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerInvoker;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 *
 * @author pengchao
 * @since 17:53 2019-09-16
 */
public class SubReqServerHandler extends ChannelHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReq req = (SubscribeReq) msg;
        if("chao".equalsIgnoreCase(req.getUserName())) {
            System.out.println("Server accept client subscribe req : [" + req.toString()+ "]");
            ctx.writeAndFlush(resp(req.getSubReqId()));
        }
    }

    private SubscribeResp resp(int subReqId) {
        SubscribeResp resp = new SubscribeResp();
        resp.setSubReqId(subReqId);
        resp.setRespCode(0);
        resp.setDesc("Netty book succeed, 3 days later, sent to the designated address");
        return resp;
    }
}
