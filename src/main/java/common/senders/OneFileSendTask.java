package common.senders;

import com.alibaba.fastjson.JSON;
import common.JSONSerializable.JSONFilePart;

import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class OneFileSendTask implements Runnable {

    private Path filePath;
    private FileSender uplink;
    private int fileID;

    public OneFileSendTask(Path filePath, int fileID, FileSender uplink) {
        this.filePath = filePath;
        this.uplink = uplink;
        this.fileID = fileID;
    }

    @Override
    public void run() {
        try {
            int blocks = (int) Math.ceil((double) Files.size(filePath) / (double)FileSender.BLOCK_SIZE);

            // empty file
            if (blocks == 0) blocks = 1;

            SeekableByteChannel channel = Files.newByteChannel(filePath);
            ByteBuffer buffer = ByteBuffer.allocate(FileSender.BLOCK_SIZE);

            for (int i = 0; i < blocks; i++) {
                buffer.clear();
                channel.read(buffer);
                byte[] currentBlock = new byte[buffer.position()];
                buffer.flip();
                buffer.get(currentBlock);

                JSONFilePart filePart = new JSONFilePart(fileID, (int) blocks, i + 1, currentBlock);

                uplink.sendToClient(JSON.toJSONString(filePart));

                // System.out.println(JSON.toJSONString(filePart));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
