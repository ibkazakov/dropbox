package client.gui.controllers;

import client.gui.ActionsGUI;
import client.gui.FileRecord;
import client.gui.FileTreeLoader;
import common.FileListResolver;
import common.JSONSerializable.filelist.FileNode;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class WorkspaceController implements Initializable {

    // local store files table:
    @FXML
    TreeTableView<FileRecord> localTable;
    @FXML
    TreeTableColumn<FileRecord, String> localTableName;
    @FXML
    TreeTableColumn<FileRecord, String> localTableSize;

    //local buttons
    @FXML
    Button localUpdateButton;
    @FXML
    Button localDeleteButton;
    @FXML
    Button localPostButton;


    // cloud table
    @FXML
    TreeTableView<FileRecord> cloudTable;
    @FXML
    TreeTableColumn<FileRecord, String> cloudTableName;
    @FXML
    TreeTableColumn<FileRecord, String> cloudTableSize;

    //cloud buttons
    @FXML
    Button cloudUpdateButton;
    @FXML
    Button cloudDeleteButton;
    @FXML
    Button cloudGetButton;


    @FXML
    Label statement;



    public void disconnect() {
        ActionsGUI.shutdownClient();
    }

    public void quit() {
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initLocalTable();
        initCloudTable();
        ActionsGUI.setWorkspaceController(this);
    }

    private void initLocalTable() {
        localTableName.setCellValueFactory((
                TreeTableColumn.CellDataFeatures<FileRecord, String> param) -> new ReadOnlyStringWrapper(
                param.getValue().getValue().getFilename()));
        localTableSize.setCellValueFactory((
                TreeTableColumn.CellDataFeatures<FileRecord, String> param) -> new ReadOnlyStringWrapper(
                param.getValue().getValue().getSize()));
        localTable.setShowRoot(false);
    }

    private void initCloudTable() {
        cloudTableName.setCellValueFactory((
                TreeTableColumn.CellDataFeatures<FileRecord, String> param) -> new ReadOnlyStringWrapper(
                param.getValue().getValue().getFilename()));
        cloudTableSize.setCellValueFactory((
                TreeTableColumn.CellDataFeatures<FileRecord, String> param) -> new ReadOnlyStringWrapper(
                param.getValue().getValue().getSize()));
        cloudTable.setShowRoot(false);
    }

    public void localUpdate() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    localUpdateButton.setDisable(true);
                    ActionsGUI.getFileTreeLoader().loadTree();
                    localTable.setRoot(ActionsGUI.getFileTreeLoader().getRoot());
                    ActionsGUI.setStatementText("local view updated at " + new Date());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    localUpdateButton.setDisable(false);
                }
            }
        });
    }

    public void localDelete() {
        try {
            localDeleteButton.setDisable(true);
            TreeItem<FileRecord> delItem = localTable.getSelectionModel().getSelectedItem();
            if (delItem != null) {
                ActionsGUI.getFileTreeLoader().deleteItem(delItem);
                ActionsGUI.setStatementText( delItem.getValue().getPath().toString() + " successfully deleted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            localDeleteButton.setDisable(false);
        }
    }

    public void localPost() {
        localPostButton.setDisable(true);
        TreeItem<FileRecord> sendItem = localTable.getSelectionModel().getSelectedItem();
        if (sendItem != null) {
            String filename = sendItem.getValue().getRelativePath().toString();
            Path path = sendItem.getValue().getPath();
            if (!Files.isDirectory(path)) {
                ActionsGUI.getClientSender().post(filename);
            } else  {
                List<FileRecord> filesList = FileTreeLoader.filesInDir(sendItem);
                ActionsGUI.getClientSender().create_dir(filename);
                for(FileRecord fileRecord : filesList) {
                    String fileInDirName = fileRecord.getRelativePath().toString();
                    ActionsGUI.getClientSender().post(fileInDirName);
                }
            }
        }
        cloudUpdate();
        localPostButton.setDisable(false);
    }

    public void cloudUpdate() {
        ActionsGUI.getClientSender().getfilelist();
    }

    public void cloudDelete() {
        cloudDeleteButton.setDisable(true);
        TreeItem<FileRecord> selected = cloudTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Path relativePath = selected.getValue().getRelativePath();
            String fileName = relativePath.toString();
            ActionsGUI.getClientSender().delete(fileName);
        }
        cloudUpdate();
        cloudDeleteButton.setDisable(false);
    }

    public void cloudGet() throws Exception {
        cloudGetButton.setDisable(true);
        TreeItem<FileRecord> selected = cloudTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // relativize to the loacal store
            Path path = localTable.getRoot().getValue().getPath().resolve(selected.getValue().getRelativePath());
            String filename = selected.getValue().getRelativePath().toString();
            System.out.println("trying to get " + filename);

            ActionsGUI.deltreeIfExists(path);

            if (selected.getValue().isDir()) {
                Files.createDirectory(path);
                List<FileRecord> filesList = FileTreeLoader.filesInDir(selected);
                for (FileRecord fileRecord : filesList) {
                    String fileInDirName = fileRecord.getRelativePath().toString();
                    ActionsGUI.getClientSender().get(fileInDirName);
                }
            } else {
                ActionsGUI.getClientSender().get(filename);
            }

        }
        cloudGetButton.setDisable(false);
    }


    public void setStatementText(String text) {
        statement.setText(text);
    }

    public void updateCloudTable(FileNode rootNode) {
        TreeItem<FileRecord> rootItem = FileListResolver.toTreeItem(rootNode);
        cloudTable.setRoot(rootItem);
        ActionsGUI.setStatementText("cloud view updated at " + new Date());
    }


}
