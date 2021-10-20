package de.xite.smp.utils;

import de.xite.smp.main.Main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQL {
  static Main pl = Main.pl;
  public static String host = pl.getConfig().getString("mysql.host").replace(" ", "");
  public static int port = pl.getConfig().getInt("mysql.port");
  public static String username = pl.getConfig().getString("mysql.user").replace(" ", "");
  public static String password = pl.getConfig().getString("mysql.password").replace(" ", "");
  public static String database = pl.getConfig().getString("mysql.database").replace(" ", "");
  public static String prefix = pl.getConfig().getString("mysql.table-prefix").replace(" ", "");
  public static boolean useSSL = pl.getConfig().getBoolean("mysql.useSSL");
  public static Connection c;
  
  public static boolean connect() {
    try {
      if (!isConnected()) {
        Main.pl.getLogger().info("Connecting to MySQL...");
        if (host == null || host.length() == 0) {
          pl.getLogger().severe("You haven't set a host");
          return false;
        } 
        if (port == 0) {
          Main.pl.getLogger().severe("You haven't set a port");
          return false;
        } 
        if (username == null || username.length() == 0) {
          pl.getLogger().severe("You haven't set a username");
          return false;
        } 
        if (password == null || password.length() == 0 || password.equalsIgnoreCase("YourPassword")) {
          pl.getLogger().severe("You haven't set a password or you are using the default.");
          return false;
        } 
        if (database == null || database.length() == 0) {
          pl.getLogger().severe("You haven't set a database");
          return false;
        } 
        if (prefix == null || prefix.length() == 0) {
          pl.getLogger().severe("You haven't set a table prefix");
          return false;
        } 
        c = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=" + useSSL, username, password);
        pl.getLogger().info("MySQL connected!");
        pl.getLogger().info("Creating tables...");
        update("CREATE TABLE IF NOT EXISTS `" + prefix + "chunks` " + 
            
            "(`id` INT NOT NULL AUTO_INCREMENT,`loc_x` INT(255) NOT NULL, `loc_z` INT(255)," + 
            "`version_created` VARCHAR(255) NOT NULL," + 
            "`date_created` VARCHAR(255) NOT NULL," + 
            "`version_modified` VARCHAR(255) NOT NULL," + 
            "`date_modified` VARCHAR(255) NOT NULL," + 
            
            "PRIMARY KEY (`id`)) ENGINE = InnoDB;");
        pl.getLogger().info("Finished!");
      }
    
    } catch (SQLException e) {
      pl.getLogger().severe("Could not connect to MySQL! Please check your settings.");
      e.printStackTrace();
      return false;
    } 
    return true;
  }
  public static void disconnect() {
    if (isConnected())
      try {
        c.close();
      } catch (SQLException e) {
        
        e.printStackTrace();
      }  
  }
  
  public static void update(String qry) {
    try {
      c.createStatement();
      PreparedStatement ps = c.prepareStatement(qry);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  public static ResultSet query(String qry) {
    try {
      c.createStatement();
      PreparedStatement ps = c.prepareStatement(qry);
      return ps.executeQuery();
    } catch (SQLException e) {
      pl.getLogger().severe("There was an error whilst executing query: " + qry);
      pl.getLogger().severe("Error:");
      e.printStackTrace();
      return null;
    } 
  }
  
  public static boolean isConnected() {
    if (c == null)
      return false; 
    try {
      if (c.isClosed())
        return false; 
    } catch (Exception e) {
      return false;
    } 
    return true;
  }



  
  public static Integer getInt(String table, String value, String where) {
    try {
      ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
      if (rs.next())
        return Integer.valueOf(rs.getInt(value)); 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
    return Integer.valueOf(0);
  }
  public static Long getLong(String table, String value, String where) {
    try {
      ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
      if (rs.next())
        return Long.valueOf(rs.getLong(value)); 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
    return null;
  }
  public static Double getDouble(String table, String value, String where) {
    try {
      ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
      if (rs.next())
        return Double.valueOf(rs.getDouble(value)); 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
    return null;
  }
  public static Float getFloat(String table, String value, String where) {
    try {
      ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
      if (rs.next())
        return Float.valueOf(rs.getFloat(value)); 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
    return null;
  }
  
  public static String getString(String table, String value, String where) {
    try {
      ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
      if (rs.next())
        return rs.getString(value); 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
    return null;
  }
  public static ArrayList<String> getStringList(String table, String value, String where) {
    ArrayList<String> result = new ArrayList<String>();
    try {
      ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
      if (rs.first())
        do {
          if (result.contains(rs.getString(value)))
            continue;  result.add(rs.getString(value));
        
        }
        while (rs.next()); 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
    return result;
  }
  public static ArrayList<String> getStringList(String table, String value) {
    ArrayList<String> result = new ArrayList<String>();
    try {
      ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "`");
      if (rs.first())
        do {
          if (result.contains(rs.getString(value)))
            continue;  result.add(rs.getString(value));
        
        }
        while (rs.next()); 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
    return result;
  }

  
  public static void deleteEntry(String table, String where) { update("DELETE FROM `" + table + "` WHERE " + where); }

  
  public static boolean checkExists(String table, String value, String where) {
    try {
      ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
      if (rs.next() && 
        rs.getString(value) != null)
        return true; 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
    return false;
  }
  public static Boolean getBoolean(String table, String value, String where) {
    try {
      ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
      if (rs.next())
        return Boolean.valueOf(rs.getBoolean(value)); 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
    return Boolean.valueOf(false);
  }
}
