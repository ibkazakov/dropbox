package client.senders;

import com.alibaba.fastjson.JSON;
import common.JSONSerializable.JSONCommand;
import io.netty.channel.Channel;

import java.nio.file.Path;

// command generator. FORMAL.
public class ClientSender {

    private Channel serverChannel;
    private int nextfreeID = 1;


    // get, post, create_dir, delete, move, getfileList
    // ??? checking correctness(later)
    public void get(String fileName) {
        JSONCommand command = new JSONCommand(nextfreeID, "get", fileName);
        nextfreeID++;
        execute(command);
    }

    public void post(String fileName) {
        JSONCommand command = new JSONCommand(nextfreeID, "post", fileName);
        nextfreeID++;
        execute(command);
    }

    public void create_dir(String directoryName) {
        JSONCommand command = new JSONCommand(nextfreeID, "create_dir", directoryName);
        nextfreeID++;
        execute(command);
    }

    public void delete(String fileName) {
        JSONCommand command = new JSONCommand(nextfreeID, "delete", fileName);
        nextfreeID++;
        execute(command);
    }

    public void move(String from, String to) {
        JSONCommand command = new JSONCommand(nextfreeID, "move", from, to);
        nextfreeID++;
        execute(command);
    }

    public void getfilelist() {
        JSONCommand command = new JSONCommand(nextfreeID, "getfilelist");
        nextfreeID++;
        execute(command);
    }

    // derivative commands


    private void execute(JSONCommand command) {
        String sendString = JSON.toJSONString(command);
        // send to the net point:
        serverChannel.writeAndFlush(sendString);
    }


    public void setServerChannel(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }
}
