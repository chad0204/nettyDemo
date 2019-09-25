package com.pc.netty_anth_guide.chapter13;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.File;
import java.io.RandomAccessFile;

/**
 *
 * @author pengchao
 * @since 14:48 2019-09-19
 */
public class FileServerHandler extends SimpleChannelInboundHandler<String> {

    private static final String CR = System.getProperty("line.separator");

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        File file = new File(msg);
        if(file.exists()) {
            if(!file.isFile()) {
                ctx.writeAndFlush("Not a file : "+ file +CR);
                return;
            }
            ctx.write(file + " " + file.length() + CR);
            RandomAccessFile randomAccessFile = new RandomAccessFile(msg,"r");
            //randomAccessFile.getChannel():FileChannel文件通道，用于对文件进行读写操作。position：文件操作指针位置，对文件进行读写的起始点
            FileRegion region = new DefaultFileRegion(randomAccessFile.getChannel(), 0, randomAccessFile.length());
            ctx.write(region);
            ctx.writeAndFlush(CR);
            System.out.println("get file succeed : "+file.getName()+"("+file.length()+")");
            randomAccessFile.close();
        } else {
            ctx.writeAndFlush("File not found: "+file+CR);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
