package com.zq.learn.netty.io;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeServer {
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1, 16, 0, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new ThreadFactoryBuilder()
                    .setNameFormat("TimeServer-%s")
                    .setDaemon(true)
                    .build());

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);
            System.out.println("time server started,bind on port 8080");

            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.execute(new TimeServerHandler(socket));
            }
        } finally {
            System.out.println("time server closed...");
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    static class TimeServerHandler implements Runnable{
        private Socket socket;

        public TimeServerHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            BufferedReader in = null;
            PrintWriter out = null;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    String order = in.readLine();
                    if(StringUtils.isEmpty(order) || order.equalsIgnoreCase("q")){
                        break;
                    }

                    if (order.equalsIgnoreCase("query time")) {
                        out.print("current time is:" + new Date());
                    }
                }
                System.out.println("a client come in " + new Date());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(socket);
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }
    }
}
