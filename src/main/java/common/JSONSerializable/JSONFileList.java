package common.JSONSerializable;

import com.alibaba.fastjson.annotation.JSONField;


import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class JSONFileList {
   @JSONField
   private List<String> pathsFileList = new LinkedList<String>();

   @JSONField
   private List<String> pathsDirectoryList = new LinkedList<String>();

   @JSONField
   private boolean success = false;

   @JSONField
   private final String obj_class = "JSONFileList";



   public void addFilePath(Path path) {
       pathsFileList.add(path.toString());
   }

   public void addDirectoryPath(Path path) {
       pathsDirectoryList.add(path.toString());
   }


    public List<String> getPathsFileList() {
        return pathsFileList;
    }

    public void setPathsFileList(List<String> pathsFileList) {
        this.pathsFileList = pathsFileList;
    }

    public List<String> getPathsDirectoryList() {
        return pathsDirectoryList;
    }

    public void setPathsDirectoryList(List<String> pathsDirectoryList) {
        this.pathsDirectoryList = pathsDirectoryList;
    }

    public String getObj_class() {
        return obj_class;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
