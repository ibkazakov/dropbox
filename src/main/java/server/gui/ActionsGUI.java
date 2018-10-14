package server.gui;

import io.netty.channel.Channel;
import javafx.stage.Stage;
import server.database.DataBase;
import server.gui.contollers.ClientsController;
import server.gui.contollers.ConnectedController;
import server.gui.contollers.LogController;
import server.gui.contollers.StartController;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionsGUI {
    private static Stage primaryStage;

    private static StartController start;
    private static ConnectedController connected;
    private static ClientsController clients;
    private static LogController log;

    private static DataBase dataBase;

    private static Map<String, Channel> channelMap = new HashMap<String, Channel>();

    public synchronized static void renewClientsTable() {
        try {
            clients.updateTable(dataBase.getClients());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static void deleteClient(String clientName) {
        try {
            boolean success = dataBase.removeClient(clientName);
            if (success) {
                // delete clients folder.
                Path clientDirectory = start.getServerPath().resolve(clientName);
                // deltree
                if (Files.exists(clientDirectory)) {
                    Files.walkFileTree(clientDirectory, new SimpleFileVisitor<Path>() {
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
                }
                // disconnect if client with such name connected
                connected.disconnectByClientName(clientName);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static void createClient(String clientName, String password) {
        try {
            boolean success = dataBase.newClient(clientName, password);
            if (success) {
                // folder for the new client addition
                Path clientDirectory = start.getServerPath().resolve(clientName);
                if (Files.notExists(clientDirectory)) {
                    // simply create new directory for client
                    Files.createDirectory(clientDirectory);
                } else {
                    if (!Files.isDirectory(clientDirectory)) {
                        // if it is a file with such name: delete and create directory
                        Files.delete(clientDirectory);
                        Files.createDirectory(clientDirectory);
                    }
                    // else do nothing: directory with such name exists.
                    // Files in it will belong to the new client
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static void updateClientPassword(String clientName, String newPassword) {
        try {
            dataBase.changePassword(clientName, newPassword);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static void setDisableClientsTableButtons(boolean value) {
        clients.setDisableClientsTableButtons(value);
    }

    public synchronized static void clearClientsTable() {
        clients.clearTable();
    }

    public synchronized static void newConnected(Channel channel) {
        channelMap.put(channel.remoteAddress().toString(), channel);
        connected.addConnection(channel.remoteAddress().toString());
    }

    public synchronized static void authChannel(String remoteAddress, String clientName) {
        connected.authAsClient(remoteAddress, clientName);
    }

    public synchronized static void channelDisconnected(Channel channel) {
        channelMap.remove(channel.remoteAddress().toString(), channel);
        connected.removeConnection(channel.remoteAddress().toString());
    }

    public synchronized static void disconnect(String remoteAddress) {
        if (channelMap.containsKey(remoteAddress)) {
            channelMap.get(remoteAddress).close();
            connected.removeConnection(remoteAddress);
        }
    }


    public synchronized static void setStart(StartController start) {
        ActionsGUI.start = start;
    }

    public synchronized static void setConnected(ConnectedController connected) {
        ActionsGUI.connected = connected;
    }

    public synchronized static void setClients(ClientsController clients) {
        ActionsGUI.clients = clients;
    }

    public synchronized static void setLog(LogController log) {
        ActionsGUI.log = log;
    }

    public synchronized static void logMessage(String message) {
       log.printMessage(message);
    }

    public synchronized static void shutdownServer() {
        start.exitShutdown();
    }

    public synchronized static void setDataBase(Path dbPath) throws Exception {
        dataBase = new DataBase(dbPath);
        dataBase.connect();
    }

    public synchronized static DataBase getDataBase() {
        return dataBase;
    }

    public synchronized static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public synchronized static Stage getPrimaryStage() {
        return primaryStage;
    }
}

