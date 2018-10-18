package client.netty;

import client.accepters.ClientAccepter;
import client.senders.ClientSender;
import client.gui.ActionsGUI;
import com.alibaba.fastjson.JSON;
import common.JSONSerializable.auth.JSONAuth;
import common.JSONSerializable.auth.JSONAuthAnswer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private boolean authMode = true;
    private final CountDownLatch authBlocker = new CountDownLatch(1);


    private ClientAccepter clientAccepter;
    private ClientSender clientSender;


    public ClientHandler(Path clientPath) {
        clientAccepter = new ClientAccepter(clientPath);
        clientSender = new ClientSender();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String jsonString = (String) msg;
        if (authMode) {
            JSONAuthAnswer authAnswer = JSON.parseObject(jsonString, JSONAuthAnswer.class);
            if (authAnswer.isSuccess()) {
                authMode = false;
                authBlocker.countDown();
                System.out.println("AUTH SUCCESSFUL");
                ActionsGUI.successAuth();
            }
        } else {
            try {
                clientAccepter.accept(jsonString);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void tryAuth(String clientName, String password) {
        String authString = JSON.toJSONString(new JSONAuth(clientName, password));
        clientAccepter.sendString(authString);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // giving channel
        Channel channel = ctx.channel();
        clientAccepter.setServerChannel(channel);
        clientSender.setServerChannel(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        clientAccepter.clear();
        ActionsGUI.channelDisconnected();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clientAccepter.clear();
        ActionsGUI.channelDisconnected();
        ctx.close();
    }



    public ClientSender getClientSender() throws Exception {
        authBlocker.await();
        return clientSender;
    }
}
