package server;

import com.alibaba.fastjson.JSON;
import common.JSONSerializable.JSONCommand;
import common.JSONSerializable.JSONFilePart;
import common.JSONSerializable.file_dialog.JSONEndTransmission;
import common.JSONSerializable.file_dialog.JSONGiveFile;
import common.accepters.CommandAccepter;
import common.accepters.FilePartAccepter;
import common.accepters.RootAccepter;
import common.senders.FileSender;


import java.nio.file.Path;
import io.netty.channel.Channel;

public class ServerAccepter implements RootAccepter {
    private Path serverPath;

    private CommandAccepter commandAccepter;
    private FilePartAccepter filePartAccepter;
    private FileSender fileSender;

    private String clientName;
    private Channel clientChannel;


    public ServerAccepter(Path serverPath) {
        this.serverPath = serverPath;
        this.commandAccepter = new CommandAccepter(serverPath, this);
        this.fileSender = new FileSender(this);
        this.filePartAccepter = new FilePartAccepter(this);
    }

    public void accept(String jsonString)  {

        // if answerString == null, there are no answer message
        String answerString = null;

        // how to know class of json-serialized object
        String obj_class = JSON.parseObject(jsonString).getObject("obj_class", String.class);

        switch (obj_class) {
            case "JSONCommand":
                answerString = commandAccepter.accept(JSON.parseObject(jsonString, JSONCommand.class), clientName);
                break;
            case "JSONFilePart":
                // no answer by this
                filePartAccepter.accept(JSON.parseObject(jsonString, JSONFilePart.class), clientName);
                break;
            case "JSONGiveFile":
                fileSender.accept(JSON.parseObject(jsonString, JSONGiveFile.class));
                break;
            case "JSONEndTransmission":
                fileSender.endSending(JSON.parseObject(jsonString, JSONEndTransmission.class).getFileID());
                break;
        }

        // sending
        if (answerString != null) {
            clientChannel.writeAndFlush(answerString);
        }

    }


    public int newAcceptingFile(Path filePath) {
        return filePartAccepter.newFile(filePath);
    }

    public int newSendingFile(Path filePath) {
        return fileSender.newFile(filePath);
    }


    public void setClientName(String clientName) {
        filePartAccepter.setRootPath(serverPath.resolve(clientName));
        this.clientName = clientName;
    }

    public void setClientChannel(Channel clientChannel) {
        this.clientChannel = clientChannel;
    }


    public void clear() throws Exception {
        fileSender.clear();
        filePartAccepter.clear();
    }

    public void sendString(String jsonString) {
        clientChannel.writeAndFlush(jsonString);
    }
}
