package common.JSONSerializable;

import com.alibaba.fastjson.annotation.JSONField;

public class JSONAnswer {
    @JSONField
    private JSONCommand command;

    @JSONField
    private boolean success;

    @JSONField
    private String error;

    @JSONField
    private int fileID;

    @JSONField
    private final String obj_class = "JSONAnswer";

    // default answer
    public JSONAnswer(JSONCommand command) {
        this.command = command;
        success = false;
    }

    public JSONAnswer(JSONCommand command, boolean success, String error, int fileID) {
        this.command = command;
        this.success = success;
        this.error = error;
        this.fileID = fileID;
    }

    public JSONCommand getCommand() {
        return command;
    }

    public void setCommand(JSONCommand command) {
        this.command = command;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getObj_class() {
        return obj_class;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

}
