package server.gui.contollers;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import server.netty.ServerThread;
import server.gui.ActionsGUI;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.io.File;

public class StartController implements Initializable {
    @FXML
    TextField port;

    @FXML
    TextField serverFolder;

    @FXML
    Button startButton;

    @FXML
    Button stopButton;

    @FXML
    TextField dbfile;

    @FXML
    Button chooseServerFolder;

    @FXML
    Button chooseDBfile;

    // extracted values
    Path serverPath;
    Path dbPath;
    int serverPort;

    //server itself
    ServerThread serverThread;

    //javafx choosers
    final DirectoryChooser directoryChooser = new DirectoryChooser();
    final FileChooser fileChooser = new FileChooser();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initChoosers();
        initStartButton();
        initStopButton();
        ActionsGUI.setStart(this);
    }

    private void initChoosers() {
        // directoryChooser.setInitialDirectory(Paths.get("").toFile());
        // fileChooser.setInitialDirectory(Paths.get("").toFile());
        chooseServerFolder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File choosedDir = directoryChooser.showDialog(ActionsGUI.getPrimaryStage());

                if (choosedDir != null) {
                    serverFolder.setText(choosedDir.getAbsolutePath());
                } else {
                    serverFolder.clear();
                }
            }
        });
        chooseDBfile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File choosedFile = fileChooser.showOpenDialog(ActionsGUI.getPrimaryStage());
                if (choosedFile != null) {
                    dbfile.setText(choosedFile.getAbsolutePath());
                } else {
                    dbfile.clear();
                }
            }
        });
    }

    private void initStartButton() {
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    startButton.setDisable(true);
                    stopButton.setDisable(false);
                    extractServerParams();
                    // register db
                    ActionsGUI.setDataBase(dbPath);
                    serverThread = new ServerThread(serverPort, serverPath);
                    serverThread.setDaemon(true);
                    serverThread.start();
                    ActionsGUI.renewClientsTable();
                    ActionsGUI.setDisableClientsTableButtons(false);
                    ActionsGUI.logMessage("Server started on port " + serverPort +
                            ".\nServer folder is " + serverPath.toAbsolutePath().toString() +
                            "\nClients database: " + dbPath.toAbsolutePath().toString());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initStopButton() {
        stopButton.setDisable(true);
        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stopButton.setDisable(true);
                startButton.setDisable(false);
                serverThread.shutdown();
                serverThread = null;
                ActionsGUI.logMessage("Server on port " + serverPort + " was shutdown!");
            }
        });
    }

    private void extractServerParams() {
        serverPort = Integer.parseInt(port.getText());
        serverPath = Paths.get(serverFolder.getText());
        dbPath = Paths.get(dbfile.getText());
    }

    public void exitShutdown() {
        if (serverThread != null) {
            serverThread.shutdown();
        }
    }

    public Path getServerPath() {
        return serverPath;
    }
}
