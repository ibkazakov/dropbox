package client.gui;

import client.gui.ActionsGUI;
import com.alibaba.fastjson.JSON;
import common.JSONSerializable.JSONAnswer;
import common.JSONSerializable.JSONFileList;
import common.JSONSerializable.file_dialog.JSONEndTransmission;
import client.accepters.AnswerAccepter;

// ClientThread's opinion of server files
public class FileListOpinion {

    private AnswerAccepter uplink;

    public FileListOpinion(AnswerAccepter uplink) {
        this.uplink = uplink;
    }

    public void update(JSONFileList fileList) {
        ActionsGUI.updateCloudTable(fileList.getRootNode());
        System.out.println("successfully update");
    }

    public void create_dir(JSONAnswer answer) {
        System.out.println("success create_dir");
        System.out.println(JSON.toJSONString(answer));
    }

    public void delete(JSONAnswer answer) {
        System.out.println("successfully delete");
       // ActionsGUI.getClientSender().getfilelist();
        System.out.println(JSON.toJSONString(answer));
    }

    public void move(JSONAnswer answer) {
        System.out.println("success move");
        System.out.println(JSON.toJSONString(answer));
    }

    public void post(JSONEndTransmission endTransmission) {
        System.out.println("success post");
        System.out.println(endTransmission.getFileName());
    }
}
