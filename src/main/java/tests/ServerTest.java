package tests;

import com.alibaba.fastjson.JSON;
import common.JSONSerializable.JSONCommand;
import common.JSONSerializable.auth.JSONAuth;
import common.JSONSerializable.file_dialog.JSONGiveFile;

import java.net.Socket;

public class ServerTest {
    public static void main(String[] args)  throws  Exception {
        Socket socket = new Socket("localhost", 8189);
        PrimitiveClient primitiveClient = new PrimitiveClient(socket);

        //get testing
        primitiveClient.sendString(JSON.toJSONString(new JSONAuth("default_client", "password")));
        primitiveClient.sendString(JSON.toJSONString(new JSONCommand(1, "get", "6.txt")));
        primitiveClient.sendString(JSON.toJSONString(new JSONGiveFile(1)));
    }

    // create_dir, delete, move, getfilelist

    private static JSONCommand create_dir() {
        return new JSONCommand(1, "create_dir", "newDir");
    }

    private static JSONCommand delete() {
        return new JSONCommand(1, "delete", "dir1");
    }

    private static JSONCommand move() {
        return new JSONCommand(1, "move", "5.txt", "6.txt");
    }

    private static JSONCommand getFileList() {
        return new JSONCommand(1, "getfilelist");
    }
}
