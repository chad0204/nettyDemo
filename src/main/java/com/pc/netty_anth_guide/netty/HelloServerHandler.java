package com.pc.netty_anth_guide.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;


/**
 * 处理服务器接收到的信息
 * 输出接收到的信息
 */
public class HelloServerHandler extends ChannelInboundHandlerAdapter {


    /**
     * 收到数据时间调用
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf in = (ByteBuf) msg;
            System.out.println(in.toString(CharsetUtil.UTF_8));
        } finally {
            //抛弃收到的数据
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
