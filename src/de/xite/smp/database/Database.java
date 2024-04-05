package de.xite.smp.database;

import de.xite.smp.commands.ChunkInfoCommand;
import de.xite.smp.database.migrations.Migrate_v3;
import de.xite.smp.database.statement.ChunkStatement;
import de.xite.smp.main.Main;
import de.xite.smp.utils.ChunkInfo;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import de.xite.smp.utils.SMPPlayer;
import org.bukkit.Bukkit;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.entity.Player;

public class Database {
	static Main pl = Main.pl;

	public static String sqlType = pl.getConfig().getString("sql.type");
	public static String prefix = pl.getConfig().getString("sql.table-prefix");

	public static PreparedStatement chunkInsertPS;
	public static PreparedStatement chunkUpdatePS;
	
    private static HikariDataSource ds = null;
    private static Connection c = null;

	public static boolean connect() {
		if(!isConnected()) {
			Main.pl.getLogger().info("Connecting to SQL database...");

			if(prefix == null || prefix.isEmpty()) {
				pl.getLogger().severe("You haven't set a table prefix");
				return false;
			}

			HikariConfig config = new HikariConfig();
			config.addDataSourceProperty("characterEncoding", "UTF-8");
			config.addDataSourceProperty("connectionTimeout", "30");

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

			if(sqlType.equals("mysql")) {
				String host = pl.getConfig().getString("sql.mysql.host");
				int port = pl.getConfig().getInt("sql.mysql.port");
				String username = pl.getConfig().getString("sql.mysql.user");
				String password = pl.getConfig().getString("sql.mysql.password");
				String database = pl.getConfig().getString("sql.mysql.database");
				boolean useSSL = pl.getConfig().getBoolean("sql.mysql.useSSL");

				if(host == null || host.isEmpty()) {
					pl.getLogger().severe("You haven't set a host.");
					return false;
				}
				if(port == 0) {
					Main.pl.getLogger().severe("You haven't set a port.");
					return false;
				}
				if(database == null || database.isEmpty()) {
					pl.getLogger().severe("You haven't set a database.");
					return false;
				}
				if(username == null || username.isEmpty()) {
					pl.getLogger().severe("You haven't set a username.");
					return false;
				}
				if(password == null || password.isEmpty()) {
					pl.getLogger().severe("You haven't set a password.");
					return false;
				}

				// Set config details
				config.setJdbcUrl("jdbc:mysql://"+host+":"+port+"/"+database);
				config.setUsername(username);
				config.setPassword(password);
				/* Disable SSL to suppress the unverified server identity warning */
				config.addDataSourceProperty("useSSL", useSSL);
			}

			if(sqlType.equals("sqlite")) {
				File database = new File(pl.getDataFolder(), "database.db");
				if(!database.exists()) {
					try {
						database.createNewFile();
					} catch (IOException e) {
						pl.getLogger().log(Level.SEVERE, "File write error: database.db");
						throw new RuntimeException(e);
					}
				}

				// Set config details
				config.setJdbcUrl("jdbc:sqlite:"+database);
				config.setMaximumPoolSize(1);
			}

		    ds = new HikariDataSource(config);

			getConnection(); // Get connection
			pl.getLogger().info("SQL connected. Creating tables...");

			// Create tables (if not exists)
			createTables(sqlType);
			runMigrations();

			// Create prepared statements
			try {
				chunkInsertPS = getConnection().prepareStatement("INSERT INTO `"+prefix+"chunks` (`world`, `loc_x`, `loc_z`, `version_created`, `date_created`, `version_modified`, `date_modified`) VALUES (?, ?, ?, ?, ?, ?, ?)");
				chunkUpdatePS = getConnection().prepareStatement("UPDATE `"+prefix+"chunks` SET `version_modified`=?, `date_modified`=? WHERE `world`=? AND `loc_x`=? AND `loc_z`=?");
			} catch (SQLException e) {
				pl.getLogger().severe("Could not prepare statement.");
				throw new RuntimeException(e);
			}

			// Start schedulers
			startMySQLHandler();

			pl.getLogger().info("SQL initialization finished. Database is ready!");
		}
		return true;
	}

	public static void disconnect() {
		try {
			chunkInsertPS.close();
			chunkUpdatePS.close();

			// Close all ChunkInfo statements
			for(Map.Entry<Player, ChunkInfo> ci : ChunkInfoCommand.chunkInfoList.entrySet())
				ci.getValue().stopSendingChunkInfoToPlayer();

			// Finally, close the connection
			c.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

    public static Connection getConnection() {
        try {
        	if(c == null || c.isClosed()) {
        		c = ds.getConnection();
        	}
        	
			return c;
		} catch (SQLException e) {
			pl.getLogger().severe("Could not get SQL connection!");
			throw new RuntimeException(e);
		}
    }

	public static boolean isConnected() {
		return !(ds == null || ds.isClosed());
	}

	public static void startMySQLHandler() {
		// Execute batches every 30 seconds
		Bukkit.getScheduler().runTaskTimerAsynchronously(pl, () -> {
			executeAllBatches();

			for(Map.Entry<UUID, SMPPlayer> smppMap : SMPPlayer.players.entrySet()) {
				SMPPlayer smpp = smppMap.getValue();
				smpp.persist();
			}
		}, 20*60*5, 20*60*5);
	}

	private static void runMigrations() {
		int currentVersion = pl.getConfig().getInt("databaseVersion");
		if(currentVersion < 3) {
			Migrate_v3.migrate();
			pl.getConfig().set("databaseVersion", 3);
			pl.saveConfig();
		}
	}

	public static void executeAllBatches() {
		try {
			if(Main.debug)
				pl.getLogger().info("Executing all batches..");

			chunkInsertPS.executeBatch();
			if(Main.debug) {
				Main.pl.getLogger().info("Executed " + ChunkStatement.chunkInsertPSBatches + " batched chunk inserts.");
			}
			ChunkStatement.chunkInsertPSBatches = 0;

			chunkUpdatePS.executeBatch();
			if(Main.debug) {
				Main.pl.getLogger().info("Executed " + ChunkStatement.chunkUpdatePSBatches + " batched chunk updates.");
			}
			ChunkStatement.chunkInsertPSBatches = 0;

			if(Main.debug)
				pl.getLogger().info("All batches have been executed.");
		} catch (SQLException e) {
			pl.getLogger().severe("Could not execute statement batch!");
			throw new RuntimeException(e);
		}
	}

	private static void createTables(String sqlType) {
		try (Statement st = getConnection().createStatement()) {
			if(sqlType.equals("mysql")) {
				// chunks table
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

				// players table
				st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + prefix + "players` " +
						"(`id` INT NOT NULL AUTO_INCREMENT," +
						"`uuid` VARCHAR(50) NOT NULL," +
						"`name` VARCHAR(16) NOT NULL," +
						"`trustlevel` INT NOT NULL," +
						"`firstJoined` DATETIME NOT NULL," +
						"`lastJoined` DATETIME NOT NULL," +
						"`logoutLocation` VARCHAR(255) NOT NULL," +
						"`playTime` INT NOT NULL," +
						"`banReason` VARCHAR(255)," +
						"PRIMARY KEY (`id`)) ENGINE = InnoDB;");
			}

			if(sqlType.equals("sqlite")) {
				// chunks table
				st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + prefix + "chunks` " +
						"(`world` VARCHAR(255) NOT NULL," +
						"`loc_x` INT NOT NULL," +
						"`loc_z` INT NOT NULL," +
						"`version_created` VARCHAR(255) NOT NULL," +
						"`date_created` VARCHAR(255) NOT NULL," +
						"`version_modified` VARCHAR(255) NOT NULL," +
						"`date_modified` VARCHAR(255) NOT NULL);");

				// players table
				st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + prefix + "players` " +
						"(`uuid` VARCHAR(50) NOT NULL," +
						"`name` VARCHAR(16) NOT NULL," +
						"`trustlevel` INT NOT NULL," +
						"`firstJoined` DATETIME NOT NULL," +
						"`lastJoined` DATETIME," +
						"`logoutLocation` VARCHAR(255)," +
						"`playTime` INT NOT NULL," +
						"`banReason` VARCHAR(255));");
			}
		} catch (SQLException e) {
			pl.getLogger().severe("Tables could not be created!");
			throw new RuntimeException(e);
		}
	}
	
	// ----------- //
	// SQL Utils //
	// ------------//
	public static String getString(String table, String value, String where) {
		String result = null;
		String query = "SELECT `"+ value + "` FROM `" + prefix + table + "` WHERE " + where;

		try (Statement st = getConnection().createStatement(); ResultSet rs = st.executeQuery(query)) {
			if(rs.next())
				result = rs.getString(value);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result;
	}
}
