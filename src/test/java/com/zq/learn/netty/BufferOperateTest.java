package com.zq.learn.netty;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class BufferOperateTest {

    @Test
    public void testReadFile() throws Exception{
        URL resource = BufferOperateTest.class.getClassLoader().getResource("niodata.txt");
        File file = new File(resource.toURI());

        RandomAccessFile raf = new RandomAccessFile(file, "rw");

        FileChannel channel = raf.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int read = channel.read(buffer);
        while (read != -1) {
            buffer.flip();

            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            System.out.print(new String(bytes));

            buffer.clear();
            read = channel.read(buffer);
        }

        channel.close();
        raf.close();
    }

    public void testScatterGather() throws Exception {
        URL resource = BufferOperateTest.class.getClassLoader().getResource("niodata.txt");
        File file = new File(resource.toURI());

        RandomAccessFile raf = new RandomAccessFile(file, "rw");

        FileChannel channel = raf.getChannel();

        ByteBuffer head = ByteBuffer.allocate(512);
        ByteBuffer body = ByteBuffer.allocate(1024);

        channel.read(new ByteBuffer[]{head, body});
    }

    @Test
    public void testTransferFrom() throws Exception {
        File file = newClasspathFile("niodata.txt");
        RandomAccessFile fromFile = new RandomAccessFile(file, "rw");
        FileChannel fromChannel = fromFile.getChannel();

        RandomAccessFile toFile = new RandomAccessFile(newClasspathFile("toFile.txt"), "rw");
        FileChannel toChannel = toFile.getChannel();


        toChannel.transferFrom(fromChannel, 0, fromChannel.size());

        fromFile.close();
        toFile.close();
    }

    private File newClasspathFile(String classpathFile) throws URISyntaxException {
        URL resource = BufferOperateTest.class.getClassLoader().getResource(classpathFile);
        return new File(resource.toURI());
    }

    public void testSelector() throws IOException {
        Selector selector = Selector.open();

        ServerSocket serverSocket = new ServerSocket(88888);
        ServerSocketChannel channel = serverSocket.getChannel();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int readyChannel = selector.select();
            if (readyChannel == 0) {
                continue;
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    //a connection was accept by serverSocketChannel
                }

                iterator.remove();
            }
        }
    }
}
