package com.zq.learn.netty.io;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TimeClient {
    public static void main(String[] args) {
        Socket socket = null;
        PrintWriter printWriter = null;
        BufferedReader reader = null;
        try {
            socket = new Socket("localhost", 8080);

            printWriter = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("please enter order");

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String order = scanner.nextLine();
                if (StringUtils.isEmpty(order)) {
                    System.out.println("order can not empty");
                    continue;
                } else if (order.equalsIgnoreCase("q")) {
                    System.out.println("client quit");
                    break;
                }

                printWriter.println(order);

                String response = reader.readLine();
                System.out.println("response from server: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(socket);

            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(printWriter);
        }
    }
}
