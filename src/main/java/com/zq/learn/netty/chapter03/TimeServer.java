package com.zq.learn.netty.chapter03;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.Date;

public class TimeServer {

    public static void main(String[] args) throws Exception {
        new TimeServer().bind(8080);
    }

    private void bind(int port) throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());

            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline()
                    .addLast(new LineBasedFrameDecoder(1024))
                    .addLast(new StringDecoder())
                    .addLast(new TimeServerHandler());

        }

    }
    private class TimeServerHandler extends ChannelInboundHandlerAdapter {
        private volatile int count = 0;

        private final String lineSeparator = System.lineSeparator();

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String req = (String) msg;
            System.out.println("The Time Server receive order:" + req + ";" +
                    "the counter is " + ++count);

            String currentTime = "query Time".equalsIgnoreCase(req) ? new Date().toString()
                    : "BAD ORDER";
            currentTime += lineSeparator;

            ByteBuf byteBuf = Unpooled.copiedBuffer(currentTime.getBytes());
            ctx.write(byteBuf);

        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }
}
