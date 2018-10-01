package server;

import com.alibaba.fastjson.JSON;
import common.JSONSerializable.auth.JSONAuth;
import common.JSONSerializable.auth.JSONAuthAnswer;

import java.util.HashMap;
import java.util.Map;

// formal. needs database connection
public class AuthAccepter {

    private ServerHandlerAccepter uplink;

    private Map<String, String> authMap = new HashMap<String, String>();

    public AuthAccepter(ServerHandlerAccepter uplink) {
        this.uplink = uplink;
        authMap.put("default_client", "password");
    }

    public String accept(String jsonString) {
        // checking class
        String obj_class = JSON.parseObject(jsonString).getObject("obj_class", String.class);
        if (!obj_class.equals("JSONAuth")) return JSON.toJSONString(new JSONAuthAnswer());

        JSONAuth auth = JSON.parseObject(jsonString, JSONAuth.class);
        JSONAuthAnswer authAnswer = new JSONAuthAnswer();

        if (checkPassword(auth)) {
            authAnswer.setSuccess(true);
            uplink.successAuth(auth.getClientName());
        }

        return JSON.toJSONString(authAnswer);

    }


    private boolean checkPassword(JSONAuth auth) {
        String truePassword = authMap.get(auth.getClientName());
        if (!auth.getPassword().equals(truePassword)) return false;
        else return true;
    }

}
