package common.senders;

import com.alibaba.fastjson.JSON;
import common.JSONSerializable.JSONFilePart;
import common.JSONSerializable.file_dialog.JSONGiveFile;
import common.accepters.RootAccepter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// FORMAL!
public class FileSender {

    // map of ids
    private Map<Integer, Path> filesMap = new HashMap<Integer, Path>();

    private RootAccepter uplink;

    private int nextfreeID = 1;

    public int newFile(Path filePath) {
        int fileID = nextfreeID;
        nextfreeID++;

        // register
        filesMap.put(fileID, filePath);

        return fileID;
    }

    public FileSender(RootAccepter uplink) {
        this.uplink = uplink;
    }


    //clientMode: fileId already determined by server
    public void clientNewFile(Path filePath, int fileID) {
        // register
        filesMap.put(fileID, filePath);

    }

    // FORMAL!
    public void send(int fileID) throws IOException {
        Path filePath = filesMap.get(fileID);
        System.out.println(filePath.toString() + " sending!");
        Thread oneFileSender = new OneFileSenderThread(filePath, fileID, this);
        oneFileSender.start();

        // endSending(fileID);
    }

    public void endSending(int fileID) {
        filesMap.remove(fileID);
    }

    public void sendToClient(String jsonString) {
        uplink.sendString(jsonString);
    }

    public void accept(JSONGiveFile jsonGiveFile) {
        try {
            int fileID = jsonGiveFile.getFileID();
            if (filesMap.containsKey(fileID)) {
                send(jsonGiveFile.getFileID());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        filesMap.clear();
    }

}
