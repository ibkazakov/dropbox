package client.gui.controllers;

import client.netty.ClientHandler;
import client.gui.ActionsGUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    @FXML
    Button authButton;
    @FXML
    TextField clientNameField;
    @FXML
    TextField passwordField;

    public void tryAuth(MouseEvent mouseEvent) {
        String clientName = clientNameField.getText();
        String password = passwordField.getText();
        ClientHandler clientHandler = ActionsGUI.getClientHandler();
        clientHandler.tryAuth(clientName, password);
    }

    public void disconnect() {
        ActionsGUI.shutdownClient();
    }

    // exec by inner client's api
    public void authSuccess() {
        //pass to the workspace window
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    authButton.setDisable(true);
                    ActionsGUI.setClientSender(ActionsGUI.getClientHandler().getClientSender());
                    // primary renew of workspace:
                    ActionsGUI.getWorkspaceController().localUpdate();
                    ActionsGUI.getWorkspaceController().cloudUpdate();

                    ActionsGUI.getPrimaryStage().setScene(ActionsGUI.getWorkspaceScene());
                    authButton.setDisable(false);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // we get an "Not on FX application thread" IllegalStateException without this wrapping
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //register controller
        ActionsGUI.setAuthController(this);
    }
}
