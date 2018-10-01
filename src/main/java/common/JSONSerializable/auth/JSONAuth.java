package common.JSONSerializable.auth;

import com.alibaba.fastjson.annotation.JSONField;

public class JSONAuth {
    @JSONField
    private String clientName;

    @JSONField
    private String password;

    @JSONField
    private final String obj_class = "JSONAuth";




    public JSONAuth(String clientName, String password) {
        this.clientName = clientName;
        this.password = password;
    }


    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getObj_class() {
        return obj_class;
    }
}
