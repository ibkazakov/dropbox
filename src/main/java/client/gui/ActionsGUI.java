package client.gui;

import client.netty.ClientHandler;
import client.senders.ClientSender;
import client.netty.ClientThread;
import client.gui.controllers.AuthController;
import client.gui.controllers.WorkspaceController;
import common.JSONSerializable.filelist.FileNode;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class ActionsGUI {
    // 3 windows
    private static Scene connectScene;
    private static Scene authScene;
    private static Scene workspaceScene;

    //controllers
    private static AuthController authController;
    private static WorkspaceController workspaceController;

    //primary stage
    private static Stage primaryStage;

    //client thread
    private static ClientThread clientThread;

    //client handler(for auth stage)
    private static ClientHandler clientHandler;

    //client sender of commands(for workspace stage)
    private static ClientSender clientSender;

    //clients Folder
    private static FileTreeLoader fileTreeLoader = new FileTreeLoader();


    public static void deltreeIfExists(Path delPath) throws Exception {
        if (Files.exists(delPath)) {
            // deltree
            if (Files.isDirectory(delPath)) {
                Files.walkFileTree(delPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }

                });
            } else {
                Files.delete(delPath);
            }
        }
    }

    public static void shutdownClient() {
        if (clientThread != null) {
            clientThread.shutdown();
        }
    }

    public static void successAuth() {
        authController.authSuccess();
    }

    public static void channelDisconnected() {
        // switch to the connect window
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                primaryStage.setScene(connectScene);
            }
        });
    }

    public static void updateCloudTable(FileNode rootNode) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                workspaceController.updateCloudTable(rootNode);
            }
        });
    }

    public static void setStatementText(String message) {
        workspaceController.setStatementText(message);
    }

    public static Scene getConnectScene() {
        return connectScene;
    }

    public static void setConnectScene(Scene connectScene) {
        ActionsGUI.connectScene = connectScene;
    }

    public static Scene getAuthScene() {
        return authScene;
    }

    public static void setAuthScene(Scene authScene) {
        ActionsGUI.authScene = authScene;
    }

    public static Scene getWorkspaceScene() {
        return workspaceScene;
    }

    public static void setWorkspaceScene(Scene workspaceScene) {
        ActionsGUI.workspaceScene = workspaceScene;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        ActionsGUI.primaryStage = primaryStage;
    }


    public static ClientHandler getClientHandler() {
        return clientHandler;
    }

    public static void setClientHandler(ClientHandler clientHandler) {
        ActionsGUI.clientHandler = clientHandler;
    }

    public static ClientSender getClientSender() {
        return clientSender;
    }

    public static void setClientSender(ClientSender clientSender) {
        ActionsGUI.clientSender = clientSender;
    }

    public static ClientThread getClientThread() {
        return clientThread;
    }

    public static void setClientThread(ClientThread clientThread) {
        ActionsGUI.clientThread = clientThread;
    }


    public static AuthController getAuthController() {
        return authController;
    }

    public static void setAuthController(AuthController authController) {
        ActionsGUI.authController = authController;
    }


    public static WorkspaceController getWorkspaceController() {
        return workspaceController;
    }

    public static void setWorkspaceController(WorkspaceController workspaceController) {
        ActionsGUI.workspaceController = workspaceController;
    }

    public static FileTreeLoader getFileTreeLoader() {
        return fileTreeLoader;
    }
}
