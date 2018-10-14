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
import java.util.Map;
import java.util.ResourceBundle;

public class ClientsController implements Initializable {

    //Buttons
    @FXML
    Button createButton;
    @FXML
    Button updateButton;
    @FXML
    Button deleteButton;
    @FXML
    Button renewButton;

    // create fields:
    @FXML
    TextField clientCreateField;
    @FXML
    TextField passwordCreateField;

    // update fields
    @FXML
    TextField clientUpdateField;
    @FXML
    TextField passwordUpdateField;

    // delete field
    @FXML
    TextField clientDeleteField;


    // table columns
    @FXML
    TableColumn clientName;
    @FXML
    TableColumn password;

    // table itself
    @FXML
    TableView<ClientParams> clientsTable;

    // table entities:
    private ObservableList<ClientParams> clients = FXCollections.observableArrayList();

    //table on click
    public void selectTable(MouseEvent mouseEvent) {
        try {
            ClientParams selected = clientsTable.getSelectionModel().getSelectedItem();
            String clientName = selected.getClientName();
            String password = selected.getPassword();
            clientUpdateField.setText(clientName.substring(1, clientName.length() - 1));
            passwordUpdateField.setText(password.substring(1, password.length() - 1));
            clientDeleteField.setText(clientName.substring(1, clientName.length() - 1));
        }
        catch (Exception e) {
            // do nothing
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initRenew();
        initTable();
        initCreate();
        initUpdate();
        initDelete();
        ActionsGUI.setClients(this);
    }

    private void initTable() {
        clientName.setCellValueFactory(new PropertyValueFactory<ConnectedController.ConnectionParams, String>("clientName"));
        password.setCellValueFactory(new PropertyValueFactory<ConnectedController.ConnectionParams, String>("password"));
        clientsTable.setItems(clients);
    }

    private void initCreate() {
        createButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ActionsGUI.createClient(clientCreateField.getText(), passwordCreateField.getText());
                clientCreateField.clear();
                passwordCreateField.clear();
                ActionsGUI.renewClientsTable();
            }
        });
    }

    private void initUpdate() {
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ActionsGUI.updateClientPassword(clientUpdateField.getText(), passwordUpdateField.getText());
                clientUpdateField.clear();
                passwordUpdateField.clear();
                clientDeleteField.clear();
                ActionsGUI.renewClientsTable();
            }
        });
    }

    private void initDelete() {
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ActionsGUI.deleteClient(clientDeleteField.getText());
                clientDeleteField.clear();
                clientUpdateField.clear();
                passwordUpdateField.clear();
                ActionsGUI.renewClientsTable();
            }
        });
    }

    private void initRenew() {
        renewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ActionsGUI.renewClientsTable();
            }
        });
    }


    public void updateTable(Map<String, String> clientsTableMap) {
        clients.clear();
        for(Map.Entry<String, String> oneClient : clientsTableMap.entrySet()) {
            String clientName = "'" + oneClient.getKey() + "'";
            String password = "'" + oneClient.getValue() + "'";
            clients.add(new ClientParams(clientName, password));
        }
    }

    public void clearTable() {
        clients.clear();
    }

    public static class ClientParams{
        private final SimpleStringProperty clientName;
        private final SimpleStringProperty password;

        private ClientParams(String clientName, String password) {
            this.clientName = new SimpleStringProperty(clientName);
            this.password = new SimpleStringProperty(password);
        }


        public String getClientName() {
            return clientName.get();
        }

        public void setClientName(String clientName) {
            this.clientName.set(clientName);
        }

        public String getPassword() {
            return password.get();
        }

        public void setPassword(String password) {
            this.password.set(password);
        }

    }

    public void setDisableClientsTableButtons(boolean value) {
        renewButton.setDisable(value);
        createButton.setDisable(value);
        updateButton.setDisable(value);
        deleteButton.setDisable(value);
        clientUpdateField.clear();
        passwordUpdateField.clear();
        clientDeleteField.clear();
    }
}
