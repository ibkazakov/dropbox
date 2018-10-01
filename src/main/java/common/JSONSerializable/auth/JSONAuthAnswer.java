package common.JSONSerializable.auth;

import com.alibaba.fastjson.annotation.JSONField;

public class JSONAuthAnswer {
    @JSONField
    private boolean success = false;

    @JSONField
    private final String obj_class = "JSONAuthAnswer";



    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getObj_class() {
        return obj_class;
    }

}
