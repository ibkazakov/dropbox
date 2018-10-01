package common.JSONSerializable;

import com.alibaba.fastjson.annotation.JSONField;

public class JSONFilePart {

    @JSONField
    private int fileId;

    @JSONField
    private int parts_quality;

    @JSONField
    private int part_number;

    @JSONField
    private byte[] data;

    @JSONField
    private final String obj_class = "JSONFilePart";

    public JSONFilePart(int fileId, int parts_quality, int part_number, byte[] data) {
        this.fileId = fileId;
        this.parts_quality = parts_quality;
        this.part_number = part_number;
        this.data = data;
    }



    public int getParts_quality() {
        return parts_quality;
    }

    public void setParts_quality(int parts_quality) {
        this.parts_quality = parts_quality;
    }

    public int getPart_number() {
        return part_number;
    }

    public void setPart_number(int part_number) {
        this.part_number = part_number;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


    public String getObj_class() {
        return obj_class;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
}
