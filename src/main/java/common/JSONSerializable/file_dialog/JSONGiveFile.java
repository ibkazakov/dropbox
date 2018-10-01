package common.JSONSerializable.file_dialog;

import com.alibaba.fastjson.annotation.JSONField;

public class JSONGiveFile {
    @JSONField
    private int fileID;

    private String obj_class = "JSONGiveFile";

    public JSONGiveFile(int fileID) {
        this.fileID = fileID;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public String getObj_class() {
        return obj_class;
    }

    public void setObj_class(String obj_class) {
        this.obj_class = obj_class;
    }
}
