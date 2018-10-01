package client;

import com.alibaba.fastjson.JSON;
import common.JSONSerializable.JSONAnswer;
import common.JSONSerializable.JSONFileList;
import common.JSONSerializable.JSONFilePart;
import common.accepters.AnswerAccepter;
import common.accepters.FileListAccepter;
import common.accepters.FilePartAccepter;
import common.senders.FileSender;

import java.nio.file.Path;

public class ClientAccepter {
    private Path clientPath;

    private AnswerAccepter answerAccepter;
    private FileListAccepter fileListAccepter;
    private FilePartAccepter filePartAccepter;

    private FileSender fileSender;

    public ClientAccepter(Path clientPath) {
        this.answerAccepter = new AnswerAccepter(clientPath, this);
    }

    public void accept(String jsonString) {
        // how to know class of json-serialized object
        String obj_class = JSON.parseObject(jsonString).getObject("obj_class", String.class);

        switch (obj_class) {
            case "JSONAnswer":
                answerAccepter.accept(JSON.parseObject(jsonString, JSONAnswer.class));
                break;
            case "JSONFileList":
                fileListAccepter.accept(JSON.parseObject(jsonString, JSONFileList.class));
                break;
            case "JSONFilePart":
                filePartAccepter.accept(JSON.parseObject(jsonString, JSONFilePart.class), "server");
                break;
        }

    }

    public void newAcceptingFile(Path filePath, int fileID) {
        filePartAccepter.clientNewFile(filePath, fileID);
    }

    public void newSendingFile(Path filePath, int fileID) {
        fileSender.clientNewFile(filePath, fileID);
    }

}
