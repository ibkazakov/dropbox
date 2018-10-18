package client.gui.controllers;


import java.io.File;

import client.netty.ClientHandler;
import client.netty.ClientThread;
import client.gui.ActionsGUI;
import javafx.fxml.FXML;


import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

import java.nio.file.Path;
import java.nio.file.Paths;


public class ConnectController {

    @FXML
    TextField hostField;

    @FXML
    TextField portField;

    @FXML
    TextField clientFolderField;

    @FXML
    Button connectButton;

    //javafx chooser
    final DirectoryChooser directoryChooser = new DirectoryChooser();

    public void connect(MouseEvent mouseEvent) {
        connectButton.setDisable(true);
        try {
            String host = hostField.getText();
            int port = Integer.parseInt(portField.getText());
            Path clientPath = Paths.get(clientFolderField.getText());
            ClientThread clientThread = new ClientThread(host, port, clientPath);
            clientThread.setDaemon(true);
            clientThread.start();
            ClientHandler clientHandler = clientThread.getHandler();
            if (clientThread.isConnected()) {
                ActionsGUI.setClientThread(clientThread);
                ActionsGUI.setClientHandler(clientHandler);
                // save obtained client Folder
                ActionsGUI.getFileTreeLoader().setRootPath(clientPath);
                // pass to the auth window
                ActionsGUI.getPrimaryStage().setScene(ActionsGUI.getAuthScene());
            }
        }
        catch (Exception e) {
            //do nothing
        }
        finally {
            connectButton.setDisable(false);
        }
    }

    public void chooseClientFolder(MouseEvent mouseEvent) {
        File choosedDir = directoryChooser.showDialog(ActionsGUI.getPrimaryStage());
        if (choosedDir != null) {
            clientFolderField.setText(choosedDir.getAbsolutePath());
        } else  {
            clientFolderField.clear();
        }
    }
}
