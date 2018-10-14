package server.gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerGUI extends Application {

    // tabs_sizes
    private static final double START_WIDTH = 500.0;
    private static final double START_HEIGHT = 250.0;

    private static final double CONNECTED_WIDTH = 500.0;
    private static final double CONNECTED_HEIGHT = 485.0;

    private static final double CLIENTS_WIDTH = 700.0;
    private static final double CLIENTS_HEIGHT = 563.0;

    private static final double LOG_WIDTH = 550.0;
    private static final double LOG_HEIGHT = 550.0;

    // tabs
    private Tab startTab = new Tab("startServer");
    private Tab connectedTab = new Tab("connectedClients");
    private Tab clientsTab = new Tab("clientsList");
    private Tab logTab = new Tab("log");



    @Override
    public void start(Stage primaryStage) throws Exception {
        // bind fxml resourses
        Parent startRoot = FXMLLoader.load(getClass().getResource("/GUI_FXML/server/start.fxml"));
        Parent connectedRoot = FXMLLoader.load(getClass().getResource("/GUI_FXML/server/connected.fxml"));
        Parent clientsRoot = FXMLLoader.load(getClass().getResource("/GUI_FXML/server/clients.fxml"));
        Parent logRoot = FXMLLoader.load(getClass().getResource("/GUI_FXML/server/log.fxml"));

        startTab.setContent(startRoot);
        connectedTab.setContent(connectedRoot);
        clientsTab.setContent(clientsRoot);
        logTab.setContent(logRoot);

        // scene init. Default tab is startTab
        Group root = new Group();
        Scene scene = new Scene(root, START_WIDTH, START_HEIGHT);

        //tabpane settings:
        TabPane tabPane = new TabPane();
        tabPane.setLayoutX(0);
        tabPane.setLayoutY(0);
        tabPane.setCursor(Cursor.HAND);
        tabPane.setSide(Side.TOP);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setTabMinHeight(20);
        tabPane.setTabMinWidth(100);
        tabPane.getTabs().addAll(startTab, connectedTab, clientsTab, logTab);
        tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                switch (newValue.intValue()) {
                    case 0:
                        scene.getWindow().setWidth(START_WIDTH);
                        scene.getWindow().setHeight(START_HEIGHT);
                        break;
                    case 1:
                        scene.getWindow().setWidth(CONNECTED_WIDTH);
                        scene.getWindow().setHeight(CONNECTED_HEIGHT);
                        break;
                    case 2:
                        scene.getWindow().setWidth(CLIENTS_WIDTH);
                        scene.getWindow().setHeight(CLIENTS_HEIGHT);
                        break;
                    case 3:
                        scene.getWindow().setWidth(LOG_WIDTH);
                        scene.getWindow().setHeight(LOG_HEIGHT);
                        break;
                }
            }
        });
        root.getChildren().add(tabPane);

        // show
        primaryStage.setTitle("GeekDropBox server");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                ActionsGUI.shutdownServer();
            }
        });

        ActionsGUI.setPrimaryStage(primaryStage);

        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
