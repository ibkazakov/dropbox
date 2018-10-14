package server.database;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DataBase {
    private Path dbPath;
    private Connection connection;
    private Statement statement;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public DataBase(Path dbPath) {
        this.dbPath = dbPath;
    }

    public void connect() throws Exception {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath.toString());
        statement = connection.createStatement();
    }

    public void disconnect() throws Exception {
        connection.close();
    }

    public Map<String, String> getClients() throws Exception {
        HashMap<String, String> clients = new HashMap<String, String>();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM client");
        while (resultSet.next()) {
            String clientName = resultSet.getString(1);
            String password = resultSet.getString(2);
            clients.put(clientName, password);
        }
        return clients;
    }

    public boolean checkPassword(String clientName, String password) throws Exception {
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM client " +
                "WHERE clientName = " + "'" + clientName + "'" + " AND " +
        "password = " + "'" + password + "'");
        int count = resultSet.getInt(1);
        if (count == 0) return false;
        return true;
    }

    // false if nothing changed
    public boolean changePassword(String clientName, String newPassword) throws Exception {

        int changed = statement.executeUpdate("UPDATE client SET password = " +
                "'" + newPassword +"'" +
        "WHERE clientName = " + "'" + clientName + "'");

        if (changed == 0) return false;
        return true;
    }

    // false if client exists or nothing change
    public boolean newClient(String clientName, String password) throws Exception {
        if (clientExists(clientName)) return false;
        // client "" cannot created
        if (clientName.equals("")) return false;

        int changed = statement.executeUpdate("INSERT INTO client (clientName, password) " +
                "VALUES (" + "'" + clientName + "'" + ", "
                 + "'" + password + "'" + ")");
        if (changed == 0) return false;
        return true;
    }

    // false if client doesn't exist or nothing change
    public boolean removeClient(String clientName) throws Exception {
        if (!clientExists(clientName)) return false;
        int changed = statement.executeUpdate("DELETE FROM client " +
                "WHERE clientName = " + "'" + clientName + "'");

        if (changed == 0) return false;
        return true;
    }

    public static void main(String[] args) throws Exception {
        DataBase dataBase = new DataBase(Paths.get("serverFolder/clientsDB"));

        dataBase.connect();
        System.out.println(dataBase.removeClient("new1_client"));
        System.out.println(dataBase.getClients());

        dataBase.disconnect();
    }

    private boolean clientExists(String clientName) throws Exception {
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM client " +
                "WHERE clientName = " + "'" + clientName + "'");
        int count = resultSet.getInt(1);
        if (count == 0) return false;
        return true;
    }
}
