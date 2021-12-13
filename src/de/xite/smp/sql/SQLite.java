package de.xite.smp.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import de.xite.smp.main.Main;


public class SQLite {
    static Main plugin = Main.pl;
    static Connection connection;

    public static Connection getSQLConnection() {
        File database = new File(plugin.getDataFolder(), "database.db");
        if (!database.exists()){
            try {
            	database.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: database.db");
            }
        }
        try {
            if(connection != null && !connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + database);
            plugin.getLogger().info("Connected to SQLite!");
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Put it in /lib folder.");
        }
        return null;
    }
}
