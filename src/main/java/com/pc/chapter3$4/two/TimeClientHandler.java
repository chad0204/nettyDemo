package com.pc.chapter3$4.two;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import java.util.logging.Logger;

/**
 *
 * @author pengchao
 * @since 10:40 2019-09-16
 */
public class TimeClientHandler extends ChannelHandlerAdapter {

    private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());


    private int counter;

    private byte[] req;


    public TimeClientHandler() {
        req = ("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message = null;
        for(int i = 0; i < 100 ;i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            //发送请求到服务端
            ctx.writeAndFlush(message);
        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //释放资源
        logger.warning("Unexpected exception from downstream :" + cause.getMessage());
        ctx.close();
    }

    /*
     * 当服务端返回应答消息时，线程调用，打印返回消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//       ByteBuf buf = (ByteBuf) msg;
//       byte[] req = new byte[buf.readableBytes()];
//       buf.readBytes(req);
//       String body = new String(req, StandardCharsets.UTF_8);
        String body = (String) msg;
       System.out.println("Now is:" +body+" ; the counter is :" + ++counter);
    }
}


