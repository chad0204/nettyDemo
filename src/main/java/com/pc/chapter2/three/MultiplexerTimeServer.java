package com.pc.chapter2.three;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

    private Selector selector;
    private ServerSocketChannel servChannel;
    private volatile boolean stop;


    /**
     * 初始化多路复用器，绑定监听端口
     */
    public MultiplexerTimeServer(int port) {
        try {

            //3.创建Reactor线程，创建多路复用器并启动线程
            selector = Selector.open();

            // 1.打开ServerSocketChannel，用于监听客户端连接，它是所有客户端连接的父管道
            servChannel = ServerSocketChannel.open();
            //2. 绑定监听端口，设置连接为非阻塞模式
            servChannel.configureBlocking(false);
            servChannel.socket().bind(new InetSocketAddress(port), 1024);

            //4.将ServerSocketChannel注册到Reactor线程的多路复用器Selector上，监听ACCEPT事件
            servChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("The time server is start in port:" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                //5.多路复用器在线程run方法的无限循环体内轮询准备就绪的Key
                selector.select(1000);//休眠时间，无论是否有读写事件发生，selector每隔1s都被唤醒一次
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null)
                                key.channel().close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //多路复用器关闭后，所有注册在上面的Channel和pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            //处理新接入的请求消息
            if (key.isAcceptable()) {//OP_ACCEPT
                //Accept the new connection
                //6. 多路复用器监听到有新的客户端接入，处理新的接入请求，完成TCP三次握手，建立物理链路
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                //7.设置客户端链路为非阻塞模式
                sc.configureBlocking(false);
                //8.将新接入的客户端连接注册到Reactor线程的多路复用器上，监听读操作
                sc.register(selector, SelectionKey.OP_READ);
            }

            if (key.isReadable()) {//OP_READ
                SocketChannel sc = (SocketChannel) key.channel();
                //异步读取客户端请求消息到缓冲区
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);//开辟一个1MB的缓冲区
                int readBytes = sc.read(readBuffer);//在建立连接时已经将channel设置为非阻塞，因此read是非阻塞的

                /**
                 * >0:读到字节码，进行编解码
                 * =0:没有读到字节，属与正常场景，忽略
                 * =-1:链路已经关闭，需要关闭SocketChannel，释放资源
                 */
                if (readBytes > 0) {
                    //对ByteBuffer进行操作
                    readBuffer.flip();//将缓存区的limit设置为position，position设置为0，用于后续对缓存区的读取操作
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("The time server receiver order:" + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                            new Date(System.currentTimeMillis()).toString() + "\n" :
                            "BAD ORDER\n";
                    doWrite(sc, currentTime);//将应答消息异步发送给客户端
                } else if (readBytes < 0) {//
                    //对端链路关闭
                    key.cancel();
                    sc.close();
                } else {

                }
            }
        }
    }

    private void doWrite(SocketChannel sc, String response) throws IOException {
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            ByteBuffer writerBuffer = ByteBuffer.allocate(bytes.length);
            writerBuffer.put(bytes);
            writerBuffer.flip();
            sc.write(writerBuffer);//异步非阻塞操作不保证一次能够把需要发送的字节码发送完，这称为"写半包"，暂不处理。
        }
    }
}


