package common.accepters;

import client.ClientAccepter;
import com.alibaba.fastjson.JSON;
import common.JSONSerializable.JSONAnswer;

import java.nio.file.Files;
import java.nio.file.Path;

// formal
public class AnswerAccepter {
    private Path clientPath;
    private ClientAccepter uplink;

    public AnswerAccepter(Path clientPath, ClientAccepter uplink) {
        this.clientPath = clientPath;
        this.uplink = uplink;
    }

    public void accept(JSONAnswer answer) {
        System.out.println(JSON.toJSONString(answer));
        if (answer.isSuccess()) {
            switch (answer.getCommand().getType()) {
                case "get":
                    get(answer);
                    break;
                case "post":
                    post(answer);
                    break;
                case "create_dir":
                    create_dir(answer);
                    break;
                case "delete":
                    delete(answer);
                    break;
                case "move":
                    move(answer);
                    break;
            }
        }
        else {
            error(answer);
        }
    }

    // commands: get, post, create_dir, delete, move

    // difference with server: fileID already known
    private void get(JSONAnswer answer) {
        // to FilePartAccepter
        int fileID = answer.getFileID();
        String fileName = answer.getCommand().getParams()[0]; // already checked on server side
        Path filePath = clientPath.resolve(fileName);

        try {
            // do nothing if file already exists
            if (Files.notExists(filePath)) {
                // formal creating empty file
                Files.createFile(filePath);
                uplink.newAcceptingFile(filePath, fileID);
                // ??? regetting/reposting problem
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void post(JSONAnswer answer) {
        // to FileSender
        int fileID = answer.getFileID();
        String fileName = answer.getCommand().getParams()[0];
        Path filePath = clientPath.resolve(fileName);
        // we know that empty file already created on server side

        uplink.newSendingFile(filePath, fileID);

    }

    // it changes client's opinion about server files
    private void create_dir(JSONAnswer answer) {

    }

    private void delete(JSONAnswer answer) {

    }

    private void move(JSONAnswer answer) {

    }

    // unsuccessful

    private void error(JSONAnswer answer) {

    }
}
