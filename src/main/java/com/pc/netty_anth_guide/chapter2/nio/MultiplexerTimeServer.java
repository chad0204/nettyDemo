package com.pc.netty_anth_guide.chapter2.nio;

import jdk.nashorn.internal.ir.LexicalContextNode;
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
import java.util.Optional;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

    private Selector selector;
    private ServerSocketChannel servChannel;
    private volatile boolean stop;


    /**
     * 构造函数，初始化多路复用器，绑定监听端口，注册事件监听（初始化只监听接收事件）
     */
    public MultiplexerTimeServer(int port) {
        try {
            // 1.打开ServerSocketChannel，用于监听客户端连接，它是所有客户端连接的父管道
            servChannel = ServerSocketChannel.open();

            //2. 绑定监听端口，设置连接为非阻塞模式
            servChannel.configureBlocking(false);
            servChannel.socket().bind(new InetSocketAddress(port), 1024);

            //3.创建Reactor线程，创建多路复用器并启动线程
            selector = Selector.open();

            //4.将ServerSocketChannel注册到Reactor线程的多路复用器Selector上，监听ACCEPT事件
            SelectionKey accept_selectionKey = servChannel.register(selector, SelectionKey.OP_ACCEPT);


            accept_selectionKey.attach(new Runnable() {//添加附加对象处理
                @Override
                public void run() {
                    System.out.println("处理accept事件");
                }
            });

            System.out.println("The time server is start in port:" + port);
        } catch (IOException e) {
            e.printStackTrace();
            //资源初始化失败，比如端口被占用
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        //5.多路复用器在线程run方法的无限循环体内轮询准备就绪的Key
        while (!stop) {
            try {
                //✨轮询当前selector中的socket是否产生感兴趣的事件，这里的轮询是底层操作系统函数做的，select就是遍历，epoll是轮询。
                //第一次进入循环体，当前selector只有一个ServerSocket监听accept事件（构造器中设置），没有则一直循环，有accept事件发生，会进入handleInput方法，创建新的通信socket，并注册到selector中，监听读事件。
                //再次进入循环体，此时selector中包含一个ServerSocket和一个Socket,一个用于监听新的客户端连接，一个用于监听客户端读写事件
                selector.select();//这里是阻塞的，但可以设置阻塞时间，就是当没有事件的时候等待timeout，有事件发生则立即放行不会等待。如果不传时间，那么将一直阻塞到事件发生

                //只要新selector持有的socket有事件发生，select()就会解阻塞，并将socketChannel加入到SelectionKey。阻塞和给SelectionKey设值都是
                //操作系统完成
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                //取出的SelectionKey集合进行遍历，注意处理之后要删除SelectionKey
                while (it.hasNext()) {
                    key = it.next();//取出
                    try {
                        //处理SelectionKey
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null)
                                key.channel().close();
                        }
                    }
                    it.remove();//删除
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


//        Runnable runnable = key.attachment()!=null ? (Runnable)key.attachment(): null;
//        Optional.ofNullable(runnable).ifPresent(x->x.run());


        //分发事件
        if (key.isValid()) {
            //处理新接入的请求消息
            if (key.isAcceptable()) {//OP_ACCEPT
                //Accept the new connection
                //6. 多路复用器监听到有新的客户端接入，处理新的接入请求，完成TCP三次握手，建立物理链路
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                //对通道进行设置
                SocketChannel sc = ssc.accept();//这里是非阻塞的（第二步设置）
                //7.设置客户端链路为非阻塞模式
                sc.configureBlocking(false);
                //8.将新接入的客户端连接注册到Reactor线程的多路复用器上，监听读操作
                SelectionKey read_selectionKey = sc.register(selector, SelectionKey.OP_READ);
                read_selectionKey.attach(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("处理read事件");
                    }
                });

            }

            if (key.isReadable()) {//OP_READ
                SocketChannel sc = (SocketChannel) key.channel();
                //9.异步读取客户端请求消息到缓冲区
                //开辟一个1MB的缓冲区
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                //在建立连接时已经将channel设置为非阻塞（第七步设置），因此read是非阻塞的
                int readBytes = sc.read(readBuffer);

                /**
                 * >0:读到字节码，进行编解码
                 * =0:没有读到字节，属与正常场景，忽略
                 * =-1:链路已经关闭，需要关闭SocketChannel，释放资源
                 */
                if (readBytes > 0) {
                    //10.对ByteBuffer进行编解码操作
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


