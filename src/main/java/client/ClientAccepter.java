package client;

import com.alibaba.fastjson.JSON;
import common.JSONSerializable.JSONFilePart;
import common.accepters.AnswerAccepter;
import common.accepters.FilePartAccepter;
import common.accepters.RootAccepter;
import common.senders.FileSender;
import io.netty.channel.Channel;

import java.nio.file.Path;

public class ClientAccepter implements RootAccepter {
    private Path clientPath;

    private AnswerAccepter answerAccepter;
    private FilePartAccepter filePartAccepter;

    private FileSender fileSender;

    private Channel serverChannel;

    public ClientAccepter(Path clientPath) {
        this.answerAccepter = new AnswerAccepter(clientPath, this);
        this.filePartAccepter = new FilePartAccepter(this);
        this.fileSender = new FileSender(this);
        filePartAccepter.setRootPath(clientPath);
    }

    public void accept(String jsonString) throws Exception {
        // how to know class of json-serialized object
        String obj_class = JSON.parseObject(jsonString).getObject("obj_class", String.class);
        switch (obj_class) {
            case "JSONFileList":
            case "JSONEndTransmission":
            case "JSONAnswer":
                answerAccepter.accept(jsonString, obj_class);
                break;
            case "JSONFilePart":
                filePartAccepter.accept(JSON.parseObject(jsonString, JSONFilePart.class), "server");
                break;
        }

    }

    public void setServerChannel(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    public void newAcceptingFile(Path filePath, int fileID) {
        filePartAccepter.clientNewFile(filePath, fileID);
    }

    public void newSendingFile(Path filePath, int fileID) {
        fileSender.clientNewFile(filePath, fileID);
    }

    public void sendString(String jsonString) {
        serverChannel.writeAndFlush(jsonString);
    }

    public FileSender getFileSender() {
        return fileSender;
    }

    public void clear() throws Exception {
        filePartAccepter.clear();
        fileSender.clear();
    }
}
