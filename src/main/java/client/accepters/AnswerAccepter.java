package client.accepters;

import client.gui.FileListOpinion;
import client.gui.ActionsGUI;
import com.alibaba.fastjson.JSON;
import common.JSONSerializable.JSONAnswer;
import common.JSONSerializable.JSONFileList;
import common.JSONSerializable.file_dialog.JSONEndTransmission;
import common.JSONSerializable.file_dialog.JSONGiveFile;

import java.nio.file.Files;
import java.nio.file.Path;

// formal
public class AnswerAccepter {
    private Path clientPath;
    private ClientAccepter uplink;
    private FileListOpinion fileListOpinion;



    public AnswerAccepter(Path clientPath, ClientAccepter uplink) {
        this.clientPath = clientPath;
        this.uplink = uplink;
        this.fileListOpinion = new FileListOpinion(this);
    }

    // we can to accept: JSONFileList, JSONEndTransmission, JSONAnswer
    public void accept(String jsonString, String obj_class) throws Exception {
        switch (obj_class) {
            case "JSONFileList":
                JSONFileList fileList = JSON.parseObject(jsonString, JSONFileList.class);
                fileListOpinion.update(fileList);
                break;
            case "JSONEndTransmission":
                JSONEndTransmission endTransmission = JSON.parseObject(jsonString, JSONEndTransmission.class);
                uplink.getFileSender().endSending(endTransmission.getFileID());
                fileListOpinion.post(endTransmission);
                break;
            case "JSONAnswer":
                JSONAnswer answer = JSON.parseObject(jsonString, JSONAnswer.class);
                acceptAnswer(answer);
                break;
        }
    }


    private void acceptAnswer(JSONAnswer answer) throws Exception {
        if (!answer.isSuccess()) {
            error(answer);
        } else {
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
    }


    // commands: get, post, create_dir, delete, move

    // difference with server: fileID already known
    private void get(JSONAnswer answer) {
        // to FilePartAccepter
        int fileID = answer.getFileID();
        String fileName = answer.getCommand().getParams()[0]; // already checked on server side
        Path filePath = clientPath.resolve(fileName);

        try {
            // formal creating empty file. create also all updirs. delete if exists
            Files.deleteIfExists(filePath);
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
            // tell to GUI that file created
            ActionsGUI.getWorkspaceController().localUpdate();
            uplink.newAcceptingFile(filePath, fileID);
            // give file
            uplink.sendString(JSON.toJSONString(new JSONGiveFile(fileID)));

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void post(JSONAnswer answer) throws Exception {
        // to FileSender
        int fileID = answer.getFileID();
        String fileName = answer.getCommand().getParams()[0];
        Path filePath = clientPath.resolve(fileName);
        // we know that empty file already created on server side

        uplink.newSendingFile(filePath, fileID);
        // begin transmittion
        uplink.getFileSender().send(fileID);

    }



    // it changes client's opinion about server files
    private void create_dir(JSONAnswer answer) {
        fileListOpinion.create_dir(answer);
    }

    private void delete(JSONAnswer answer) {
        fileListOpinion.delete(answer);
    }

    private void move(JSONAnswer answer) {
        fileListOpinion.move(answer);
    }

    // unsuccessful

    private void error(JSONAnswer answer) {
        System.out.println("error!:");
        System.out.println(answer.getError());
    }
}
