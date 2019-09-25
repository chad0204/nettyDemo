package com.pc.netty_anth_guide.chapter12;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author pengchao
 * @since 09:56 2019-09-19
 */
public class ChineseProverbServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final String[] DICTIONARY = {"旧时王谢堂前燕","一片冰心在玉壶","指点江山激扬文字粪土当年万户侯","落霞与孤鹜起飞"};

    private String nextQuote() {
        //由于ChineseProverbServerHandler存在并发操作的可能，使用线程安全随机类
        int quoteId = ThreadLocalRandom.current().nextInt(DICTIONARY.length);
        return DICTIONARY[quoteId];
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        //netty对udp进行了封装，因此收到的是Netty封装后的DatagramPacket对象，转换成字符串
        String req = packet.content().toString(StandardCharsets.UTF_8);
        System.out.println(req);

        if("谚语字典查询".equals(req)) {
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(
                    "谚语查询结果： "+nextQuote(), CharsetUtil.UTF_8), packet.sender()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
