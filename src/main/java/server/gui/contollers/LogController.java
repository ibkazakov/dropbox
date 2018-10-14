package server.gui.contollers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import server.gui.ActionsGUI;

import java.net.URL;
import java.util.ResourceBundle;


public class LogController implements Initializable {
    @FXML
    TextArea logArea;

    @FXML
    Button clearButton;

    public void printMessage(String message) {
        logArea.appendText(message);
        logArea.appendText("\n");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                logArea.clear();
            }
        });
        ActionsGUI.setLog(this);
    }
}
