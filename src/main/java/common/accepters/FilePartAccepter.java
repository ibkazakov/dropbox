package common.accepters;

import com.alibaba.fastjson.JSON;
import common.JSONSerializable.JSONFilePart;
import common.JSONSerializable.file_dialog.JSONEndTransmission;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;


// no proper thread for each file
public class FilePartAccepter {

    private Map<Integer, Path> fileIDMap = new HashMap<Integer, Path>();

    private RootAccepter uplink;

    private Path rootPath;

    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }

    public FilePartAccepter(RootAccepter uplink) {
        this.uplink = uplink;
    }

    // ID namespace is common for all clients
    private int nextfreeID = 1;

    public void accept(JSONFilePart filePart, String clientName)  {
        try {
                // exception if there are no such fileID
                Path currentPath = fileIDMap.get(filePart.getFileId());

                // write
                Files.write(currentPath, filePart.getData(), StandardOpenOption.APPEND);

                // close and unregister
                if (filePart.getParts_quality() == filePart.getPart_number()) {
                    fileIDMap.remove(filePart.getFileId());
                    uplink.sendString(JSON.toJSONString(new JSONEndTransmission(filePart.getFileId(),
                            rootPath.relativize(currentPath).toString())));
                }

        }
        catch (Exception e) {
            e.printStackTrace();
            // do nothing
        }
    }

    // server mode: return new fileID of accepting file process
    public int newFile(Path filePath) {
        // take free id
        int fileID = nextfreeID;
        nextfreeID++;

        fileIDMap.put(fileID, filePath);
        return fileID;
    }


    // client mode: fileID already determined by server
    public void clientNewFile(Path filePath, int fileID) {
        fileIDMap.put(fileID, filePath);
    }

    // deleting all unfinished downloads
    public void clear() throws Exception {
        for(Path path : fileIDMap.values()) {
            Files.deleteIfExists(path);
        }
        fileIDMap.clear();
    }


}
