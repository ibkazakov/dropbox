package client;

import common.handler_coders.MyStringDecoder;
import common.handler_coders.MyStringEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

public class ClientThread extends Thread {
    // wait util init ends before any actions
    public final CountDownLatch initBlocker = new CountDownLatch(1);
    private Path clientPath;

    private ClientHandler handler;

    public ClientThread(Path clientPath) {
        this.clientPath = clientPath;
    }

    // actions throw this
    public ClientHandler getHandler() throws Exception {
        initBlocker.await();
        return handler;
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.remoteAddress(new InetSocketAddress("localhost", 8189));
            handler = new ClientHandler(clientPath);
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new MyStringEncoder(), new MyStringDecoder(), handler);
                }
            });
            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            // actions unblock
            initBlocker.countDown();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
