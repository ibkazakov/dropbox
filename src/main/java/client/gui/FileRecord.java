package client.gui;

import javafx.beans.property.SimpleStringProperty;

import java.nio.file.Path;

public class FileRecord {
    private final SimpleStringProperty filename;
    private final SimpleStringProperty size;
    private final Path path;
    private final Path relativePath;
    private final boolean dir;

    public FileRecord(String filename, String size, Path path, Path relativePath, boolean dir) {
        this.filename = new SimpleStringProperty(filename);
        this.size = new SimpleStringProperty(size);
        this.path = path;
        this.relativePath = relativePath;
        this.dir = dir;
    }

    public Path getPath() {
        return path;
    }

    public String getFilename() {
        return filename.get();
    }

    public String getSize() {
        return size.get();
    }

    public void setFilename(String filename) {
        this.filename.set(filename);
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public Path getRelativePath() {
        return relativePath;
    }

    public boolean isDir() {
        return dir;
    }
}