package common.senders;

import common.JSONSerializable.file_dialog.JSONGiveFile;
import common.accepters.SendableAccepter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// FORMAL!
public class FileSender {

    public static final int BLOCK_SIZE = 2048;

    private ExecutorService executor = Executors.newFixedThreadPool(8);

    // map of ids
    private Map<Integer, Path> filesMap = new HashMap<Integer, Path>();

    private SendableAccepter uplink;

    private int nextfreeID = 1;

    public int newFile(Path filePath) {
        int fileID = nextfreeID;
        nextfreeID++;

        // register
        filesMap.put(fileID, filePath);

        return fileID;
    }

    public FileSender(SendableAccepter uplink) {
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
        Runnable oneFileSend = new OneFileSendTask(filePath, fileID, this);
        executor.submit(oneFileSend);
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
        executor.shutdown();
    }

}
