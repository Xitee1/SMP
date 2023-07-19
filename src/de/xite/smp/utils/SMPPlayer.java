package de.xite.smp.utils;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.xite.smp.main.Main;
import de.xite.smp.database.Database;

public class SMPPlayer {
	private static HashMap<UUID, SMPPlayer> players = new HashMap<>();
	public static int maxTrustLevel = 6;
	
	String name, banReason = "Deine Spielerdaten konnten nicht geladen werden. Bitte versuche es später erneut.";
	UUID uuid;
	Integer trustlevel = 1, playTime = 0;
	Timestamp firstJoined = null, lastJoined = null;
	Location logoutLocaction;
	
	
	/*
	 * TrustLevels:
	 * 1: Du kannst mit manchen Dingen interagieren (z.B. eine bestimmte Art von Knopf oder Druckplatte)
	 * 2: Du kannst mit allem interagieren; Du kannst Bauen (ausgeschlossen Lava, Feuer, etc.), wenn jemand mit TrustLevel 6 online ist
	 * 3: Du kannst mit allem interagieren; Du kannst Bauen (ausgeschlossen Lava, Feuer, etc.)
	 * 4: Du kannst mit allem interagieren; Du kannst Bauen, wenn jemand mit TrustLevel 6 online ist
	 * 5: Du kannst mit allem interagieren; Du kannst Bauen
	 * 6: Du kannst mit allem interagieren; Du kannst Bauen; Das AntiCheat überprüft dich nicht mehr
	 */
	
	private SMPPlayer(UUID uuid) {
		try (Statement st = Database.getConnection().createStatement()) {
			ResultSet rs = st.executeQuery("SELECT * FROM `"+ Database.prefix+"players` WHERE `uuid`='"+uuid+"'");
			if(rs.next()) {
				this.uuid = uuid;
				this.name = rs.getString("name");
				this.trustlevel = rs.getInt("trustLevel");
				this.firstJoined = rs.getTimestamp("firstJoined");
				this.lastJoined = rs.getTimestamp("lastJoined");
				this.playTime = rs.getInt("playtime");
				this.banReason = rs.getString("banReason");
				String[] location = rs.getString("logoutLocation").split("%");
				this.logoutLocaction = new Location(Bukkit.getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]),
						Float.parseFloat(location[4]), Float.parseFloat(location[5]));
			}
			rs.close();
		} catch (SQLException e1) {
			Main.pl.getLogger().severe("Could not load player with UUID "+uuid+"!");
			e1.printStackTrace();
		}
	}

	public static SMPPlayer getPlayer(UUID uuid) {
		if(!players.containsKey(uuid)) {
			SMPPlayer smpPlayer = new SMPPlayer(uuid);
			// Validate that the player has been loaded successfully
			if(smpPlayer.getName() == null)
				return null;
			players.put(uuid, smpPlayer);
		}

		return players.get(uuid);
	}

	public static void unloadSMPPlayer(UUID uuid) {
		players.remove(uuid);
	}

	public static SMPPlayer create(UUID uuid) {
		Player p = Bukkit.getPlayer(uuid);
		if(p != null) {
			try {
				Statement st = Database.getConnection().createStatement();
				st.executeUpdate("INSERT INTO `"+ Database.prefix+"players` (`uuid`, `name`, `trustlevel`, `firstJoined`, `lastJoined`, `logoutLocation`, `playTime`, `banReason`) VALUES"
						+ "('"+uuid+"', '"+p.getName()+"', '1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'world%0%0%0%0%0', '0', 'none')");
				st.close();
				return getPlayer(uuid);
			}catch (SQLException e) {
				Main.pl.getLogger().severe("Could not create new player with UUID "+uuid+"!");
				e.printStackTrace();
			}
		}else {
			Main.pl.getLogger().severe("Player must be online in order to create SMPPlayer");
		}
		return null; // Validate that the player has actually been created
	}
	
	public static UUID nameToUUID(String name) {
		String uuid = Database.getString("players", "uuid", "`name`='"+name+"'");
		if(uuid == null)
			return null;
		return UUID.fromString(uuid);
	}
	// ------------- //
	// General utils //
	// ------------- //
	public static Boolean isLVLmaxOnline() {
		for(Player all : Bukkit.getOnlinePlayers())
			if(SMPPlayer.getPlayer(all.getUniqueId()).getTrustLevel() == maxTrustLevel)
				return true;
		return false;
	}

	private void updateSQLPlayerValue(String key, String value) {
		Database.update("UPDATE `"+ Database.prefix+"players` SET `"+key+"`='"+value+"' WHERE `uuid`='"+this.uuid+"'");
	}

	// ---------------- //
	// SMPPlayer object //
	// ---------------- //
	// Player name
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
		updateSQLPlayerValue("name", name);
	}


	// Trust Level
	public Integer getTrustLevel() {
		return this.trustlevel;
	}

	public void setTrustLevel(int level) {
		updateSQLPlayerValue("trustlevel", String.valueOf(level));
		this.trustlevel = level;
	}

	
	// First joined
	public Timestamp getFirstJoined() {
		return this.firstJoined;
	}
	/*
	public void setFirstJoined(Timestamp date) {
		updateSQLPlayerValue("firstjoined", null);
	}
	*/


	// Last joined
	public void saveLastJoined() {
		Database.update("UPDATE `"+Database.prefix+"players` SET `lastJoined`=CURRENT_TIMESTAMP WHERE `uuid`='"+this.uuid+"'");
	}

	public Timestamp getLastJoined() {
		return this.lastJoined;
	}


	// Location
	public Location getLocation() {
		return this.logoutLocaction;
	}

	public void setLogoutLocation(Location loc) {
		String w = loc.getWorld().getName();
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		float yaw = loc.getYaw();
		float pitch = loc.getPitch();
		String locationString = w+"%"+x+"%"+y+"%"+z+"%"+yaw+"%"+pitch;
		updateSQLPlayerValue("logoutLocation", locationString);

		this.logoutLocaction = loc;
	}

	
	// Playtime
	public void countPlayTime() {
		this.playTime += 1;
	}

	public Integer getPlayTime() {
		return this.playTime;
	}

	public void savePlayTime() {
		updateSQLPlayerValue("playTime", String.valueOf(this.playTime));
	}
	
	// Ban
	public Boolean isBanned() {
		return !this.banReason.equals("none");
	}

	public String getBanReason() {
		return this.banReason;
	}

	public void setBanReason(String reason) {
		updateSQLPlayerValue("banReason", reason);
		this.banReason = reason;
	}
}
