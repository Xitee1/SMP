package de.xite.smp.sql;

import de.xite.smp.main.Main;
import de.xite.smp.utils.ChunkManager;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySQL {
	static Main pl = Main.pl;
	public static boolean useMySQL = pl.getConfig().getBoolean("mysql.use");
	public static String host = pl.getConfig().getString("mysql.host").replace(" ", "");
	public static int port = pl.getConfig().getInt("mysql.port");
	public static String username = pl.getConfig().getString("mysql.user").replace(" ", "");
	public static String password = pl.getConfig().getString("mysql.password").replace(" ", "");
	public static String database = pl.getConfig().getString("mysql.database").replace(" ", "");
	public static String prefix = pl.getConfig().getString("mysql.table-prefix").replace(" ", "");
	public static boolean useSSL = pl.getConfig().getBoolean("mysql.useSSL");
	
	private static ArrayList<String> failedUpdates = new ArrayList<>();
	public static ArrayList<String> waitingUpdates = new ArrayList<>();
	private static boolean isUpdating = false;
	
    private static HikariDataSource ds = null;
    private static Connection c = null;

	public static boolean connect() {
		if(!isConnected()) {
			Main.pl.getLogger().info("Connecting to SQL database...");

			if(prefix == null || prefix.length() == 0) {
				pl.getLogger().severe("You haven't set a table prefix");
				return false;
			}

			HikariConfig config = new HikariConfig();
			if(useMySQL) {
				if(host == null || host.length() == 0) {
					pl.getLogger().severe("You haven't set a host");
					return false;
				}
				if(port == 0) {
					Main.pl.getLogger().severe("You haven't set a port");
					return false;
				}
				if(database == null || database.length() == 0) {
					pl.getLogger().severe("You haven't set a database");
					return false;
				}
				if(username == null || username.length() == 0) {
					pl.getLogger().severe("You haven't set a username");
					return false;
				}
				if(password == null || password.length() == 0 || password.equalsIgnoreCase("YourPassword")) {
					pl.getLogger().severe("You haven't set a password or you are using the default.");
					return false;
				}

				config.setJdbcUrl("jdbc:mysql://"+host+":"+port+"/"+database);
			    config.setUsername(username);
			    config.setPassword(password);
			}else {
		        File database = new File(pl.getDataFolder(), "database.db");
		        if(!database.exists()) {
		            try {
		            	database.createNewFile();
		            } catch (IOException e) {
		                pl.getLogger().log(Level.SEVERE, "File write error: database.db");
		            }
		        }
				
			    config.setJdbcUrl("jdbc:sqlite:"+database);
			}

		    config.setMaximumPoolSize(15);
		    
		    config.addDataSourceProperty("characterEncoding", "UTF-8");
		    config.addDataSourceProperty("connectionTimeout", "28800");
		    /* https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration */
		    /* https://cdn.oreillystatic.com/en/assets/1/event/21/Connector_J%20Performance%20Gems%20Presentation.pdf */
		    config.addDataSourceProperty("cachePrepStmts", "true");
		    config.addDataSourceProperty("prepStmtCacheSize", "250");
		    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		    config.addDataSourceProperty("useServerPrepStmts", "true");
		    config.addDataSourceProperty("useLocalSessionState", "true");
		    config.addDataSourceProperty("rewriteBatchedStatements", "true");
		    config.addDataSourceProperty("cacheServerConfiguration", "true");
		    config.addDataSourceProperty("maintainTimeStats", "false");
	        config.addDataSourceProperty("useUnicode",true);
	        config.addDataSourceProperty("allowMultiQueries",true);
		    /* Disable SSL to suppress the unverified server identity warning */
		    config.addDataSourceProperty("useSSL", useSSL);

		    ds = new HikariDataSource(config);
		    if(useMySQL) {
		    	createTablesMySQL();
		    }else {
		    	createTablesSQLite();
		    }
			
			startMySQLHandler();
			pl.getLogger().info("SQL connected!");
		}
		return true;
	}
    public static Connection getConnection() {
        try {
        	if(c == null || c.isClosed()) {
        		c = ds.getConnection();
        		c.setAutoCommit(false);
        	}
        	
			return c;
		} catch (SQLException e) {
			pl.getLogger().severe("Could not connect to SQL database! Retry in 3 seconds..");
			e.printStackTrace();
			Bukkit.getScheduler().runTaskLaterAsynchronously(Main.pl, MySQL::connect, 20 * 3);
		}
        return null;
    }

	public static boolean isConnected() {
		return !(ds == null || ds.isClosed());
	}

	public static void startMySQLHandler() {
		// If an update failed, retry it until it is successful
		Bukkit.getScheduler().runTaskTimerAsynchronously(pl, new Runnable() {
			@Override
			public void run() {
				if(!failedUpdates.isEmpty()) {
					update(failedUpdates.get(0));
					failedUpdates.remove(0);
				}
			}
		}, 5, 5);
		
		// Queue for efficient updates - execute all waiting every minute
		Bukkit.getScheduler().runTaskTimerAsynchronously(pl, new Runnable() {
			@Override
			public void run() {
				executeAllWaitingUpdates();
			}
		}, 20*60, 20*60);
	}

	public static void executeAllWaitingUpdates() {
		if(isUpdating)
			return;
		isUpdating = true;

		if(!waitingUpdates.isEmpty() || ChunkManager.getAllChunks().size() != 0) {
			pl.getLogger().info("Executing all waiting updates ("+waitingUpdates.size()+")..");
			try {
				Statement st = getConnection().createStatement();

				// Execute all chunk updates
				for(ChunkManager cm : ChunkManager.getAllChunks()) {
					st.executeUpdate(cm.getMySQLUpdateQuery());
					cm.removeChunkFromCache();
				}

				// Execute other waiting updates
				ArrayList<String> wU = new ArrayList<>(waitingUpdates);
				for(String s : wU) {
					st.executeUpdate(s);
					waitingUpdates.remove(s);
				}

				st.close();
				getConnection().commit();
			} catch (SQLException e) {
				e.printStackTrace();
				pl.getLogger().severe("Could not execute waiting SQL updates. Closing connection to reconnect.");
			}
			pl.getLogger().info("All waiting updates have been executed.");
		}
		isUpdating = false;
	}
  	
	public static boolean update(String qry) {
		try {
			Statement st = getConnection().createStatement();
			st.executeUpdate(qry);
			c.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			Main.pl.getLogger().severe("Could not execute MySQL update.");
		}
		return false;
	}

	public static ResultSet query(String qry) {
		try {
			Statement st = getConnection().createStatement();
			return st.executeQuery(qry);
		} catch (SQLException e) {
			pl.getLogger().severe("There was an error whilst executing query: " + qry);
			pl.getLogger().severe("Error:");
			e.printStackTrace();
		}
		return null;
	}
  

	private static void createTablesMySQL() {
		try {
			Connection c = getConnection();
			Statement st = c.createStatement();
			// chunk table
			st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + prefix + "chunks` " + 
					"(`id` INT NOT NULL AUTO_INCREMENT," +
					"`world` VARCHAR(255) NOT NULL," +
					"`loc_x` INT NOT NULL," +
					"`loc_z` INT NOT NULL," + 
					"`version_created` VARCHAR(255) NOT NULL," + 
					"`date_created` VARCHAR(255) NOT NULL," + 
					"`version_modified` VARCHAR(255) NOT NULL," + 
					"`date_modified` VARCHAR(255) NOT NULL," + 
					"PRIMARY KEY (`id`)) ENGINE = InnoDB;");
			
			st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + prefix + "players` " + 
					"(`id` INT NOT NULL AUTO_INCREMENT," +
					"`uuid` VARCHAR(50) NOT NULL," +
					"`name` VARCHAR(16) NOT NULL," +
					"`trustlevel` INT NOT NULL," +
					"`firstJoined` DATETIME NOT NULL," +
					"`lastJoined` DATETIME NOT NULL," + 
					"`logoutLocation` VARCHAR(255) NOT NULL," + 
					"`playTime` INT NOT NULL," + 
					"`banReason` VARCHAR(255) NOT NULL," + 
					"PRIMARY KEY (`id`)) ENGINE = InnoDB;");
			c.commit();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void createTablesSQLite() {
		try {
			Connection c = getConnection();
			Statement st = c.createStatement();
			// chunk table
			st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + prefix + "chunks` " + 
					"(`world` VARCHAR(255) NOT NULL," +
					"`loc_x` INT NOT NULL," +
					"`loc_z` INT NOT NULL," + 
					"`version_created` VARCHAR(255) NOT NULL," + 
					"`date_created` VARCHAR(255) NOT NULL," + 
					"`version_modified` VARCHAR(255) NOT NULL," + 
					"`date_modified` VARCHAR(255) NOT NULL);");
			
			st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + prefix + "players` " + 
					"(`uuid` VARCHAR(50) NOT NULL," +
					"`name` VARCHAR(16) NOT NULL," +
					"`trustlevel` INT NOT NULL," +
					"`firstJoined` DATETIME NOT NULL," +
					"`lastJoined` DATETIME NOT NULL," + 
					"`logoutLocation` VARCHAR(255) NOT NULL," + 
					"`playTime` INT NOT NULL," + 
					"`banReason` VARCHAR(255) NOT NULL);");
			c.commit();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// ----------- //
	// MySQl Utils //
	// ------------//
	public static String getString(String table, String value, String where) {
		String result = null;
		try {
			ResultSet rs = query("SELECT `"+ value + "` FROM `" + prefix + table + "` WHERE " + where);
			if(rs.next())
				result = rs.getString(value);

			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return result;
	}
}
