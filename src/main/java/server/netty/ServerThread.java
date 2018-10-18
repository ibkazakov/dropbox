package server.netty;

import common.netty_coders.MyStringDecoder;
import common.netty_coders.MyStringEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import server.gui.ActionsGUI;
import server.netty.handlers.ServerHandler;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {

    private int serverPort;
    private Path serverPath;

    private Channel serverChannel;

    private List<String> clientsList = new ArrayList<String>();



    public ServerThread(int serverPort, Path serverPath) {
        this.serverPath = serverPath;
        this.serverPort = serverPort;
    }

    public void shutdown() {
        serverChannel.close();
    }

    public void run()  {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MyStringEncoder(), new MyStringDecoder(),
                                    new ServerHandler(serverPath, clientsList));
                        }
                    })
//                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(serverPort).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            serverChannel = f.channel();

            f.channel().closeFuture().sync();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("server shutdown!");
            try {
                ActionsGUI.getDataBase().disconnect();
                ActionsGUI.clearClientsTable();
                ActionsGUI.setDisableClientsTableButtons(true);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
