package de.xite.smp.sql;

import de.xite.smp.main.Main;
import de.xite.smp.utils.ChunkManager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
			Main.pl.getLogger().info("Verbinde mit SQL...");
			
			HikariConfig config = new HikariConfig();
			if(useMySQL) {
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
			
			
			if(host == null || host.length() == 0) {
				pl.getLogger().severe("You haven't set a host");
				return false;
			} 
			if(port == 0) {
				Main.pl.getLogger().severe("You haven't set a port");
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
			if(database == null || database.length() == 0) {
				pl.getLogger().severe("You haven't set a database");
				return false;
			} 
			if(prefix == null || prefix.length() == 0) {
				pl.getLogger().severe("You haven't set a table prefix");
				return false;
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
		    config.addDataSourceProperty("useSSL", "false");

		    ds = new HikariDataSource(config);
		    if(useMySQL) {
		    	createTablesMySQL();
		    }else {
		    	createTablesSQLite();
		    }
			
			startMySQLHandler();
			pl.getLogger().info("SQL-Datenbank verbunden!");
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
			try {
				c.close();
				ds.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			MySQL.connect();
			pl.getLogger().severe("Konnte keine Verbindung zur SQL-Datenbank herstellen!");
			e.printStackTrace();
		}
        return null;
    }
	public static boolean isConnected() {
		if(ds == null)
			return false;
		
		if(ds.isClosed())
			return false;
		
		return true;
	}
	public static void startMySQLHandler() {
		// If a update failed, retry it until it is successful
		Bukkit.getScheduler().runTaskTimerAsynchronously(pl, new Runnable() {
			@Override
			public void run() {
				if(!failedUpdates.isEmpty())
					update(failedUpdates.get(0));
			}
		}, 5, 5);
		
		// Qeue for efficient updates - execute all waiting every minute
		Bukkit.getScheduler().runTaskTimerAsynchronously(pl, new Runnable() {
			@Override
			public void run() {
				executeAllWaitingUpdates();
			}
		}, 20*60*1, 20*60*1);
	}
	public static void executeAllWaitingUpdates() {
		if(isUpdating)
			return;
		isUpdating = true;
		if(!waitingUpdates.isEmpty() || ChunkManager.getAllChunks().size() != 0) {
			pl.getLogger().info("Executing all waiting updates..");
			Statement st;
			Connection c = getConnection();
			Long currentTime = System.currentTimeMillis();
			try {
				st = c.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
				try {
					c.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				return;
			}
			
			int i = 0;
			ArrayList<String> list = new ArrayList<>();
			list.addAll(waitingUpdates);
			for(String s : list) {
				try {
					st.executeUpdate(s);
					waitingUpdates.remove(s);
				} catch (SQLException e) {
					pl.getLogger().info("Could not update! Query:"+s);
				}
				i++;
			}
			list = null;
			
			for(ChunkManager cm : ChunkManager.getAllChunks()) {
				try {
					String qry = cm.getMySQLUpdateQuery();
					pl.getLogger().info("Executing Query: "+qry);
					st.executeUpdate(qry);
				} catch (SQLException e) {
					pl.getLogger().info("Could not update! Query:"+cm.getMySQLUpdateQuery());
				}
				cm.removeChunkFromCache();
				i++;
			}
			
			try {
				c.commit();
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
				try {
					c.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			Long took = (System.currentTimeMillis() - currentTime);
			Long average = took/i;
			pl.getLogger().info("Executed "+i+" updates. Took "+took/1000+"s ("+average+"ms/query).");
		}
		isUpdating = false;
	}
  	
	public static boolean update(String qry) {
		Connection c = getConnection();
		try {
			Statement st = c.createStatement();
			st.executeUpdate(qry);
			st.close();
			c.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			Main.pl.getLogger().severe("Konnte nicht mit MySQL kommunizieren! Der Query wird zur Warteschleife hinzugef??gt.");
			Main.pl.getLogger().severe("");
			failedUpdates.add(qry);
			try {
				c.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		}
	}
	public static ResultSet query(String qry) {
		Connection c = getConnection();
		try {
			Statement st = c.createStatement();
			return st.executeQuery(qry);
		} catch (SQLException e) {
			pl.getLogger().severe("There was an error whilst executing query: " + qry);
			pl.getLogger().severe("Error:");
			e.printStackTrace();
			try {
				c.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return null;
		} 
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
	public static Integer getInt(Statement st, String table, String value, String where) {
		try {
			ResultSet rs = st.executeQuery("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
			if(rs.next()) {
				int i = rs.getInt(value);
				rs.close();
				return i;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return Integer.valueOf(0);
	}
	public static Long getLong(Statement st, String table, String value, String where) {
		try {
			ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
			if(rs.next()) {
				Long l = rs.getLong(value);
				rs.close();
				return l;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return null;
	}
	public static Double getDouble(Statement st, String table, String value, String where) {
		try {
			ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
			if(rs.next()) {
				Double d = rs.getDouble(value);
				rs.close();
				return d;
			}
				
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return null;
	}
	public static Float getFloat(Statement st, String table, String value, String where) {
		try {
			ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
			if(rs.next()) {
				Float f = rs.getFloat(value);
				rs.close();
				return f;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return null;
	}
	public static Boolean getBoolean(Statement st, String table, String value, String where) {
		try {
			ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
			if(rs.next()) {
				Boolean b = rs.getBoolean(value);
				rs.close();
				return b;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return Boolean.valueOf(false);
	}
	public static String getString(Statement st, String table, String value, String where) {
		try {
			ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
			if(rs.next()) {
				String s = rs.getString(value);
				rs.close();
				return s;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return null;
	}
	public static ArrayList<String> getStringList(Statement st, String table, String value, String where) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
			if(rs.first()) {
				while(rs.next()) {
					result.add(rs.getString(value));
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return result;
	}
	public static ArrayList<String> getStringList(Statement st, String table, String value) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "`");
			if(rs.first()) {
				while(rs.next()) {
					result.add(rs.getString(value));
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static void deleteEntry(Statement st, String table, String where) {
		try {
			st.executeUpdate("DELETE FROM `" + table + "` WHERE " + where);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static boolean checkExists(Statement st, String table, String value, String where) {
		try {
			ResultSet rs = query("SELECT `" + value + "` FROM `" + table + "` WHERE " + where);
			if(rs.next())
				return true;
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return false;
	}
	
	
	
	/*
	 private static HikariDataSource dsMySQL;
	
	public static void convertMySQLToSQLite() {
		HikariConfig config = new HikariConfig();
		
	    config.setJdbcUrl("jdbc:mysql://"+host+":"+port+"/"+database);
	    config.setUsername(username);
	    config.setPassword(password);
	    
	    config.setMaximumPoolSize(15);
	    
	    config.addDataSourceProperty("characterEncoding", "UTF-8");
	    config.addDataSourceProperty("connectionTimeout", "28800");
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
	    config.addDataSourceProperty("useSSL", "false");

	    dsMySQL = new HikariDataSource(config);
	    
	    
		Connection c = getConnection();
		
		try {
			Statement st = dsMySQL.getConnection().createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM `"+MySQL.prefix+"chunks`");
			
			Statement stlite = c.createStatement();
			
			while(rs.next()) {
				String world = rs.getString("world");
				String date_created = rs.getString("date_created");
				String version_created = rs.getString("version_created");
				String date_modified = rs.getString("date_modified");
				String version_modified = rs.getString("version_modified");
				int x = rs.getInt("loc_x");
				int z = rs.getInt("loc_z");
				
				String qry = "INSERT INTO `" + MySQL.prefix + "chunks` (`world`, `loc_x`, `loc_z`, `version_created`, `date_created`, `version_modified`, `date_modified`) VALUES" + 
						"('"+world+"', '"+x+"', '"+z+"', '"+version_created+"', '"+date_created+"', '"+version_modified+"', '"+date_modified+"')";
				pl.getLogger().info(qry);
				stlite.executeUpdate(qry);
				
			}
			c.commit();
			rs.close();
			stlite.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			Statement st = dsMySQL.getConnection().createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM `"+MySQL.prefix+"players`");
			
			Statement stlite = c.createStatement();
			
			while(rs.next()) {
				String uuid = rs.getString("uuid");
				String name = rs.getString("name");
				int trustlevel = rs.getInt("trustLevel");
				Timestamp firstJoined = rs.getTimestamp("firstJoined");
				Timestamp lastJoined = rs.getTimestamp("lastJoined");
				int playTime = rs.getInt("playtime");
				String banReason = rs.getString("banReason");
				String location = rs.getString("logoutLocation");
				
				String qry = "INSERT INTO `"+MySQL.prefix+"players` (`uuid`, `name`, `trustlevel`, `firstJoined`, `lastJoined`, `logoutLocation`, `playTime`, `banReason`) VALUES"
						+ "('"+uuid+"', '"+name+"', '"+trustlevel+"', '"+firstJoined+"', '"+lastJoined+"', '"+location+"', '"+playTime+"', '"+banReason+"')";
				pl.getLogger().info(qry);
				stlite.executeUpdate(qry);
			}
			c.commit();
			stlite.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	*/
}
