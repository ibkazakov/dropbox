package common.JSONSerializable;

import com.alibaba.fastjson.annotation.JSONField;
import common.JSONSerializable.filelist.FileNode;


import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class JSONFileList {

   @JSONField
   private FileNode rootNode;

   @JSONField
   private boolean success = false;

   @JSONField
   private final String obj_class = "JSONFileList";


    public String getObj_class() {
        return obj_class;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public FileNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(FileNode rootNode) {
        this.rootNode = rootNode;
    }
}
