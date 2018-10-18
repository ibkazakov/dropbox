package server.netty.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import server.accepters.AuthAccepter;
import server.accepters.ServerAccepter;
import server.gui.ActionsGUI;

import java.nio.file.Path;
import java.util.List;


public class ServerHandler extends ChannelInboundHandlerAdapter {

    private boolean authMode = true;
    private ServerAccepter serverAccepter;
    private AuthAccepter authAccepter;
    private String clientName;

    private List<String> clientsList;



    public ServerHandler(Path serverPath, List<String> clientsList) {
        this.authAccepter = new AuthAccepter(this);
        this.serverAccepter = new ServerAccepter(serverPath);
        this.clientsList = clientsList;

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
                    clientsList.add(clientName);
                    ActionsGUI.authChannel(ctx.channel().remoteAddress().toString(), clientName);
                    ActionsGUI.logMessage(ctx.channel().remoteAddress() + " successfully auth as " + clientName);
                    serverAccepter.setClientChannel(newClientChannel);
                }

                ctx.writeAndFlush(authAnswer);
            } else {
                serverAccepter.accept(jsonString);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            ReferenceCountUtil.release(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        endConnection(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ActionsGUI.newConnected(ctx.channel());
        ActionsGUI.logMessage("Unauth " + ctx.channel().remoteAddress() + " client connected...");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        endConnection(ctx);
    }


    public void successAuth(String clientName) {
        authMode = false;
        this.clientName = clientName;
    }

    public List<String> getClientsList() {
        return clientsList;
    }

    private void endConnection(ChannelHandlerContext ctx) throws Exception {
        if (authMode) {
            ActionsGUI.logMessage("Unauth " + ctx.channel().remoteAddress() + " disconnected");
        }
        else {
            clientsList.remove(clientName);
            ActionsGUI.logMessage("client " + clientName +
                    " ( " + ctx.channel().remoteAddress() + " ) " + "disconnected");
        }
        ActionsGUI.channelDisconnected(ctx.channel());
        serverAccepter.clear();
        ctx.close();
    }
}
