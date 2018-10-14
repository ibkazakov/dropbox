package server.gui.contollers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import server.gui.ActionsGUI;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ConnectedController implements Initializable {

    @FXML
    private TableView<ConnectionParams> connectTable;

    @FXML
    private TableColumn remoteAddress;

    @FXML
    private TableColumn clientName;

    @FXML
    private TextField disconnectField;

    @FXML
    private Button disconnectButton;


    public void selectTable(MouseEvent mouseEvent) {
        try {
            String remoteAddress = connectTable.getSelectionModel().getSelectedItem().getRemoteAddress();
            disconnectField.setText(remoteAddress);
        }
        catch (Exception e) {
            // do nothing
        }
    }

    private ObservableList<ConnectionParams> connections = FXCollections.observableArrayList(
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disconnectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                disconnect(disconnectField.getText());
                disconnectField.clear();
            }
        });
        remoteAddress.setCellValueFactory(new PropertyValueFactory<ConnectionParams, String>("remoteAddress"));
        clientName.setCellValueFactory(new PropertyValueFactory<ConnectionParams, String>("clientName"));


        connectTable.setItems(connections);
        ActionsGUI.setConnected(this);
    }

    private void disconnect(String remoteAddress) {
        ActionsGUI.disconnect(remoteAddress);
    }

    public synchronized void addConnection(String remoteAddress) {
        connections.add(new ConnectionParams(remoteAddress, null));
    }

    public synchronized void removeConnection(String remoteAddress) {
        connections.removeIf(new Predicate<ConnectionParams>() {
            @Override
            public boolean test(ConnectionParams connectionParams) {
                if (remoteAddress.equals(connectionParams.getRemoteAddress())) return true;
                return false;
            }
        });
        connectTable.refresh();
    }

    public synchronized void disconnectByClientName(String clientName) {
        final StringBuilder obtainedremoteAddress = new StringBuilder();
        connections.stream().filter(new Predicate<ConnectionParams>() {
            @Override
            public boolean test(ConnectionParams connectionParams) {
                return ("'" + clientName + "'")
                        .equals(connectionParams.getClientName());
            }
        }).forEach(new Consumer<ConnectionParams>() {
            @Override
            public void accept(ConnectionParams connectionParams) {
                obtainedremoteAddress.setLength(0);
                obtainedremoteAddress.append(connectionParams.getRemoteAddress());
            }
        });
        if (obtainedremoteAddress.length() != 0) {
            ActionsGUI.disconnect(obtainedremoteAddress.toString());
        }

    }

    public synchronized void authAsClient(String remoteAddress, String clientName) {
        System.out.println("trying auth " + remoteAddress + " as " + clientName);

        connections.stream().filter(new Predicate<ConnectionParams>() {
            @Override
            public boolean test(ConnectionParams connectionParams) {
                if (remoteAddress.equals(connectionParams.getRemoteAddress())) return true;
                return false;
            }
        }).forEach(new Consumer<ConnectionParams>() {
            @Override
            public void accept(ConnectionParams connectionParams) {
                connectionParams.setClientName(clientName);
            }
        });

        connectTable.refresh();

        System.out.println(connections.toArray()[0].toString());
    }

   public static class ConnectionParams{
        private final SimpleStringProperty remoteAddress;
        private final SimpleStringProperty clientName;

        private ConnectionParams(String remoteAddress, String clientName) {
            this.remoteAddress = new SimpleStringProperty(remoteAddress);
            this.clientName = new SimpleStringProperty(clientName);
        }

        public String getRemoteAddress() {
            return remoteAddress.get();
        }

        public String getClientName() {
            return clientName.get();
        }

        public void setRemoteAddress(String remoteAddress) {
            this.remoteAddress.set(remoteAddress);
        }

        public void setClientName(String clientName) {
            this.clientName.set( "'" + clientName + "'");
        }

        @Override
        public String toString() {
            if (clientName.get() == null) return "null";
            return clientName.get();
        }
    }
}

