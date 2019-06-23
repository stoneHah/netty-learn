package com.zq.learn.netty.nio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class NioTimeServer {

    public static void main(String[] args) {
        new Thread(new ReactorTask()).start();
    }

    private static class ReactorTask implements Runnable{

        private Selector selector;

        private volatile boolean stop = false;


        public ReactorTask() {
            try {
                this.selector = Selector.open();
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), 8888),1024);

                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

                System.out.println("the time server started in port:" + 8888);
            } catch (IOException e) {
                e.printStackTrace();

                System.exit(1);
            }
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }

        @Override
        public void run() {
            while (!stop) {
                try {
                    int readyChannels = selector.select(1000);
                    if (readyChannels > 0) {
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = selectionKeys.iterator();
                        while (iterator.hasNext()) {
                            SelectionKey selectionKey = iterator.next();

                            try {
                                handleInput(selectionKey);
                            } catch (Exception e) {
                                if (selectionKey != null) {
                                    selectionKey.cancel();
                                    if (selectionKey.channel() != null) {
                                        selectionKey.channel().close();
                                    }
                                }
                                e.printStackTrace();
                            }
                            iterator.remove();
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        private void handleInput(SelectionKey selectionKey) throws Exception{
            if (selectionKey.isValid()) {
                if (selectionKey.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);

                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    SocketChannel sc = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                    int read = sc.read(byteBuffer);
                    if (read > 0) {
                        byteBuffer.flip();
                        byte[] bytes = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bytes);
                        String body = new String(bytes, "utf-8");
                        System.out.println("the time server receive order:" + body);

                        String response = body.equalsIgnoreCase("query time") ?
                                new Date().toString() : "bad order";

                        doWrite(sc,response);
                    } else if (read < 0) {
                        //链路关闭
                        selectionKey.cancel();
                        sc.close();

                    }else{}

                }
            }
        }

        private void doWrite(SocketChannel sc, String response) throws Exception {
            ByteBuffer buffer = ByteBuffer.wrap(response.getBytes("utf-8"));
            sc.write(buffer);
        }
    }
}
