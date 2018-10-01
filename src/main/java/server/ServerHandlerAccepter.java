package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.file.Path;


public class ServerHandlerAccepter extends ChannelInboundHandlerAdapter {

    private boolean authMode = true;
    private ServerAccepter serverAccepter;
    private AuthAccepter authAccepter;
    private String clientName;



    public ServerHandlerAccepter(Path serverPath) {
        this.authAccepter = new AuthAccepter(this);
        this.serverAccepter = new ServerAccepter(serverPath);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            String jsonString = (String) msg;

            // point to give it to inner clientsMap api:
            if (authMode) {
               String authAnswer = authAccepter.accept(jsonString);
                if (!authMode) {
                    // successful auth and register client
                    Channel newClientChannel = ctx.channel();
                    serverAccepter.setClientName(clientName);
                    serverAccepter.setClientChannel(newClientChannel);
                }

                ctx.writeAndFlush(authAnswer);
            } else {
                serverAccepter.accept(jsonString);
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        serverAccepter.clear();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        serverAccepter.clear();
        ctx.close();
    }


    public void successAuth(String clientName) {
        authMode = false;
        this.clientName = clientName;
    }
}
