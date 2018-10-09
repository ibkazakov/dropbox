package y_netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import y_netty.handler_coders.MyStringDecoder;
import y_netty.handler_coders.MyStringEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public class Network extends Thread {

    private Channel currentChannel = null;
    public final CountDownLatch sendBlocker = new CountDownLatch(1);


    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.remoteAddress(new InetSocketAddress("localhost", 8189));
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new MyStringEncoder(), new MyStringDecoder(), new NetworkHandler());
                    currentChannel = socketChannel;
                }
            });
            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            sendBlocker.countDown();
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

    public void sendString(String message) throws Exception {
        // we can get NullPointerException without it, in channel still hasn't prepared
        sendBlocker.await();
        currentChannel.writeAndFlush(message);
    }



    public static void main(String[] args) throws Exception {
        Network network = new Network();
        network.start();
        network.sendString("first string");
        network.sendString("second string");
    }

}