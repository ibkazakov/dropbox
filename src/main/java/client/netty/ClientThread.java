package client.netty;

import common.netty_coders.MyStringDecoder;
import common.netty_coders.MyStringEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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
    private boolean connected = false;

    private String host;
    private int port;
    private Path clientPath;

    private Channel clientChannel;

    private ClientHandler handler;

    public ClientThread(String host, int port, Path clientPath) {
        this.host = host;
        this.port = port;
        this.clientPath = clientPath;
    }

    public boolean isConnected() {
        return connected;
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
            clientBootstrap.remoteAddress(new InetSocketAddress(host, port));
            handler = new ClientHandler(clientPath);
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new MyStringEncoder(), new MyStringDecoder(), handler);
                }
            });
            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            clientChannel = channelFuture.channel();
            // actions unblock
            connected = true;
            initBlocker.countDown();
            channelFuture.channel().closeFuture().sync();
        }
        catch (Exception e) {
            System.out.println("Connection failed!");
        }
        finally {
            try {
                initBlocker.countDown();
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("ConsoleTestClient shutdown!");
        }
    }

    public void shutdown() {
        if (clientChannel.isActive()) {
            clientChannel.close();
        }
    }

    public Path getClientPath() {
        return clientPath;
    }
}
