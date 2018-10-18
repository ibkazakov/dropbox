package client;

import client.gui.ActionsGUI;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Client extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // bind fxml resourses
        Parent connectRoot = FXMLLoader.load(getClass().getResource("/GUI_FXML/client/connect.fxml"));
        Parent authRoot = FXMLLoader.load(getClass().getResource("/GUI_FXML/client/auth.fxml"));
        Parent workspaceRoot = FXMLLoader.load(getClass().getResource("/GUI_FXML/client/workspace.fxml"));

        Scene connectScene = new Scene(connectRoot);
        Scene authScene = new Scene(authRoot);
        Scene workspaceScene = new Scene(workspaceRoot);

        ActionsGUI.setConnectScene(connectScene);
        ActionsGUI.setAuthScene(authScene);
        ActionsGUI.setWorkspaceScene(workspaceScene);
        ActionsGUI.setPrimaryStage(primaryStage);

        primaryStage.setTitle("GeekDropBox client");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                ActionsGUI.shutdownClient();
            }
        });
        primaryStage.setResizable(false);
        primaryStage.setScene(connectScene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
