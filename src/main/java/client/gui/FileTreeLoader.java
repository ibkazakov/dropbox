package client.gui;

import common.FileListResolver;
import javafx.scene.control.TreeItem;

import java.nio.file.*;
import java.util.*;

public class FileTreeLoader {
    private FileListResolver resolver = new FileListResolver();
    private Path rootPath;
    private TreeItem<FileRecord> root = null;

    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }

    public void loadTree() throws Exception {
        resolver.setRootPath(rootPath);
        resolver.loadTree();
        root = FileListResolver.toTreeItem(resolver.getRootNode());
    }

    public TreeItem<FileRecord> getRoot() {
        return root;
    }

    private long treeResolveSize(TreeItem<FileRecord> root) throws Exception {
        Path path = root.getValue().getPath();
        long size = Files.size(path);
        if (Files.isDirectory(path)) {
            for (TreeItem<FileRecord> child : root.getChildren()) {
                size += treeResolveSize(child);
            }
        }
        root.getValue().setSize(Long.toString(size) + " bytes" +
                (Files.isDirectory(path) ? " (dir)" : " (file)"));
        return size;
    }

    public void deleteItem(TreeItem<FileRecord> item) throws Exception {
        item.getParent().getChildren().remove(item);
        Path delPath = item.getValue().getPath();
        if (Files.notExists(delPath)) {
            ActionsGUI.setStatementText("trying to delete already not existing " + delPath.toString());
        }
        ActionsGUI.deltreeIfExists(delPath);
        // recalculate all sizes
        treeResolveSize(root);
    }

    // all files in dir and subdirs in disk
    public static List<FileRecord> filesInDir(TreeItem<FileRecord> dirItem) {
        Queue<TreeItem<FileRecord>> unexpanded = new ArrayDeque<TreeItem<FileRecord>>();
        List<FileRecord> filesList = new LinkedList<FileRecord>();
        unexpanded.offer(dirItem);
        while (!unexpanded.isEmpty()) {
            TreeItem<FileRecord> currentDirItem = unexpanded.remove();
            for (TreeItem<FileRecord> currentItem : currentDirItem.getChildren()) {
                if (currentItem.getValue().isDir()) {
                    unexpanded.offer(currentItem);
                } else {
                    filesList.add(currentItem.getValue());
                }
            }
        }
        return filesList;
    }

}
