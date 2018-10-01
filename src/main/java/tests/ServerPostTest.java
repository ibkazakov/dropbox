package tests;

import com.alibaba.fastjson.JSON;
import common.JSONSerializable.JSONCommand;
import common.JSONSerializable.JSONFilePart;
import common.JSONSerializable.auth.JSONAuth;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerPostTest {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8189);
        PrimitiveClient primitiveClient = new PrimitiveClient(socket);
        primitiveClient.sendString(JSON.toJSONString(new JSONAuth("default_client", "password")));
        primitiveClient.sendString(JSON.toJSONString(new JSONCommand(1, "post", "hegel.pdf")));
        send(1, Paths.get("someFiles/hegel.pdf"), primitiveClient);
    }


    private static void send(int fileID, Path filePath, PrimitiveClient client) throws Exception {
        int blocks = (int)Math.ceil((double)Files.size(filePath) / 1024.0);

        // empty file
        if (blocks == 0) blocks = 1;

        SeekableByteChannel channel = Files.newByteChannel(filePath);
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        for(int i = 0; i < blocks; i++) {
            buffer.clear();
            channel.read(buffer);
            byte[] currentBlock = new byte[buffer.position()];
            buffer.flip();
            buffer.get(currentBlock);

            JSONFilePart filePart = new JSONFilePart(fileID, (int)blocks, i + 1, currentBlock);

            // sendToClient(JSON.toJSONString(filePart));
            client.sendString(JSON.toJSONString(filePart));
        }
    }
}
