package me.stormers.rpg.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySql {
    private final String host = "localhost";
    private final String port = "3306";
    private final String database = "minecraft";
    private final String username = "root";
    private final String password = "";

    private Connection connection;

    public boolean connected() {
        return (connection != null);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if (!connected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" +
                            host + ":" + port + "/" + database + "?useSSL=false",
                    username, password);
        }

    }

    public void Disconect() {
        if (connected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
