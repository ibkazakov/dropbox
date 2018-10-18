package common.JSONSerializable.filelist;

import com.alibaba.fastjson.annotation.JSONField;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class FileNode {
    @JSONField(ordinal = 1)
    private String filename;
    @JSONField(ordinal = 2)
    private long size;
    @JSONField(ordinal = 3)
    private boolean dir;
    @JSONField(ordinal = 4)
    private List<FileNode> children = new ArrayList<FileNode>();

    @JSONField(ordinal = 5)
    private Path path;

    @JSONField(ordinal = 6)
    private Path relativePath;


    public FileNode(String filename, boolean dir, Path path, Path relativePath) {
        this.filename = filename;
        this.dir = dir;
        this.path = path;
        this.relativePath = relativePath;
    }


    public FileNode(String filename, long size, boolean dir, List<FileNode> children, Path path, Path relativePath) {
        this.filename = filename;
        this.size = size;
        this.dir = dir;
        this.children = children;
        this.path = path;
        this.relativePath = relativePath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDir() {
        return dir;
    }

    public void setDir(boolean dir) {
        this.dir = dir;
    }

    public List<FileNode> getChildren() {
        return children;
    }

    public void setChildren(List<FileNode> children) {
        this.children = children;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Path getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(Path relativePath) {
        this.relativePath = relativePath;
    }
}