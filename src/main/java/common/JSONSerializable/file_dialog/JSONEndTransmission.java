package common.JSONSerializable.file_dialog;

import com.alibaba.fastjson.annotation.JSONField;

public class JSONEndTransmission {
    @JSONField
    private int fileID;

    @JSONField
    private String fileName;

    @JSONField
    private String obj_class = "JSONEndTransmission";

    public JSONEndTransmission(int fileID, String fileName) {
        this.fileName = fileName;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
