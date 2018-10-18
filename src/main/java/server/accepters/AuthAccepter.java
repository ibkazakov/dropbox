package server.accepters;

import com.alibaba.fastjson.JSON;
import common.JSONSerializable.auth.JSONAuth;
import common.JSONSerializable.auth.JSONAuthAnswer;
import server.netty.handlers.ServerHandler;
import server.gui.ActionsGUI;


public class AuthAccepter {

    private ServerHandler uplink;

    public AuthAccepter(ServerHandler uplink) {
        this.uplink = uplink;
    }

    public String accept(String jsonString) throws Exception {
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


    private boolean checkPassword(JSONAuth auth) throws Exception {
        boolean isPass = ActionsGUI.getDataBase().checkPassword(auth.getClientName(), auth.getPassword());
        boolean isNotAlreadyConnected = !uplink.getClientsList().contains(auth.getClientName());
        return (isPass && isNotAlreadyConnected);
    }

}
