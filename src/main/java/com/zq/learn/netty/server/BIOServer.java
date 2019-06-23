package com.zq.learn.netty.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author qun.zheng
 * @description: TODO
 * @date 2019-06-2208:25
 */
public class BIOServer implements Server {

    private int port;

    public BIOServer(int port) {
        this.port = port;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleClientSocket(clientSocket);
        }
    }

    private void handleClientSocket(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

        String request,response;
        while ((request = in.readLine()) != null){
            if (request.equalsIgnoreCase("done")) {
                break;
            }

            response = handleRequest(request);
            out.println(response);
            out.flush();
        }
    }

    private String handleRequest(String request) {
        return "processing " + request;
    }

    public static void main(String[] args) throws Exception {
        new BIOServer(1111).start();
    }
}
