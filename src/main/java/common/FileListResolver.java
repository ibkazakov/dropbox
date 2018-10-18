package common;


import client.gui.FileRecord;
import common.JSONSerializable.filelist.FileNode;
import javafx.scene.control.TreeItem;

import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;

public class FileListResolver {
    private Path rootPath = null;
    private FileNode rootNode = null;

    private Queue<FileNode> unexpanded = new ArrayDeque<FileNode>();

    private void expandDir(FileNode dirNode) throws Exception {
        Path dirPath = dirNode.getPath();
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for(Path entryPath : stream) {
                String filename = entryPath.toAbsolutePath().getFileName().toString();
                FileNode newNode = new FileNode(filename,Files.isDirectory(entryPath),
                        entryPath, rootPath.relativize(entryPath));
                dirNode.getChildren().add(newNode);

                if (Files.isDirectory(entryPath)) {
                    unexpanded.offer(newNode);
                }
            }
        }
        catch (DirectoryIteratorException e) {
            e.getCause().printStackTrace();
        }
    }

    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }

    private long resolveSize(FileNode node) throws Exception {
        Path path = node.getPath();
        long size = Files.size(path);
        for(FileNode child : node.getChildren()) {
            size += resolveSize(child);
        }
        node.setSize(size);
        return size;
    }

    public void loadTree() throws Exception {
        rootNode = new FileNode(rootPath.toAbsolutePath().getFileName().toString(),
                Files.isDirectory(rootPath), rootPath, rootPath.relativize(rootPath));
        if (Files.isDirectory(rootPath)) {
            unexpanded.offer(rootNode);
        }
        while(!unexpanded.isEmpty()) {
            FileNode dirNode = unexpanded.remove();
            expandDir(dirNode);
        }
        resolveSize(rootNode);
    }

    public static TreeItem<FileRecord> toTreeItem(FileNode rootNode) {
        TreeItem<FileRecord> rootItem = toItem(rootNode);

        Queue<FileNode>             unexpandedNodes = new ArrayDeque<FileNode>();
        Queue<TreeItem<FileRecord>> unexpandedItems = new ArrayDeque<TreeItem<FileRecord>>();

        if (rootNode.isDir()) {
            unexpandedNodes.offer(rootNode);
            unexpandedItems.offer(rootItem);
        }

        while(!unexpandedNodes.isEmpty()) {
            FileNode node = unexpandedNodes.remove();
            TreeItem<FileRecord> item = unexpandedItems.remove();

            for(FileNode childNode : node.getChildren()) {
                TreeItem<FileRecord> childItem = toItem(childNode);
                item.getChildren().add(childItem);

                if (childNode.isDir()) {
                    unexpandedNodes.offer(childNode);
                    unexpandedItems.offer(childItem);
                }
            }
        }

        return rootItem;
    }

    private static TreeItem<FileRecord> toItem(FileNode node) {
        String filename = node.getFilename();
        String size = Long.toString(node.getSize()) + " bytes" +
                (Files.isDirectory(node.getPath()) ? " (dir)" : " (file)");
        Path path = node.getPath();
        Path relativePath = node.getRelativePath();
        boolean dir = node.isDir();
        return new TreeItem<FileRecord>(new FileRecord(filename, size, path, relativePath, dir));
    }

    public FileNode getRootNode() {
        return rootNode;
    }
}
