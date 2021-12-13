package de.xite.smp.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.xite.smp.sql.MySQL;

public class SMPPlayer {
	private static HashMap<UUID, SMPPlayer> players = new HashMap<>();
	public static int maxTrustLevel = 6;
	
	String name, banReason = "Deine Spielerdaten konnten nicht geladen werden. Bitte versuche es später erneut.";
	UUID uuid;
	Integer trustlevel, playTime;
	Timestamp firstJoined, lastJoined;
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
	
	public SMPPlayer(UUID uuid) {
		try {
			Statement st = MySQL.getConnection().createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM `"+MySQL.prefix+"players` WHERE `uuid`='"+uuid+"'");
			this.uuid = uuid;
			if(rs.next()) {
				this.name = rs.getString("name");
				this.trustlevel = rs.getInt("trustLevel");
				this.firstJoined = rs.getTimestamp("firstJoined");
				this.lastJoined = rs.getTimestamp("lastJoined");
				this.playTime = rs.getInt("playtime");
				this.banReason = rs.getString("banReason");
				String[] location = rs.getString("logoutLocation").split("_");
				this.logoutLocaction = new Location(Bukkit.getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]),
						Float.parseFloat(location[4]), Float.parseFloat(location[5]));
			}else {
				Player p = Bukkit.getPlayer(uuid);
				if(p != null) {
					this.name = p.getName();
				}else {
					this.name = NameFetcher.getName(uuid);
				}
				st.executeUpdate("INSERT INTO `"+MySQL.prefix+"players` (`id`, `uuid`, `name`, `trustlevel`, `firstJoined`, `lastJoined`, `logoutLoc_x`, `logoutLoc_y`, `logoutLoc_z`, `playTime`, `banReason`) VALUES"
						+ "(NULL, '"+uuid+"', '"+this.name+"', '0', NOW, NOW, 'world_0_0_0_0_0', '0', 'none')");
				this.trustlevel = 0;
				this.firstJoined = rs.getTimestamp("firstJoined");
				this.lastJoined = rs.getTimestamp("lastJoined");
				this.logoutLocaction = null;
				this.playTime = 0;
				this.banReason = "none";
			}
			rs.close();
			st.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	public static SMPPlayer getPlayer(UUID uuid) {
		if(players.containsKey(uuid))
			return players.get(uuid);
		SMPPlayer sp = new SMPPlayer(uuid);
		players.put(uuid, sp);
		return sp;
	}
	public static SMPPlayer getPlayer(Player p) {
		return getPlayer(p.getUniqueId());
	}
	public static UUID getUUID(String name) {
		Statement st;
		UUID uuid = null;
		try {
			st = MySQL.getConnection().createStatement();
			uuid = UUID.fromString(MySQL.getString(st, MySQL.prefix+"players", "uuid", "`name`='"+name+"'"));
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return uuid;
	}
	public static Boolean isLVLmaxOnline() {
		for(Player all : Bukkit.getOnlinePlayers())
			if(SMPPlayer.getPlayer(all).getTrustLevel() == maxTrustLevel)
				return true;
		return false;
	}
	
	
	public void remove() {
		if(players.containsKey(uuid))
			players.remove(uuid);
	}
	
	// Trust Level
	public void setTrustLevel(int level) {
		MySQL.update("UPDATE `"+MySQL.prefix+"players` SET `trustLevel`='"+level+"' WHERE `uuid`='"+this.uuid+"'");
		this.trustlevel = level;
	}
	public Integer getTrustLevel() {
		return this.trustlevel;
	}
	
	// First joined
	public Timestamp getFirstJoined() {
		return this.firstJoined;
	}
	
	// Last joined
	public void setLastJoined() {
		MySQL.update("UPDATE `"+MySQL.prefix+"players` SET `lastJoined`=NOW WHERE `uuid`='"+this.uuid+"'");
	}
	public Timestamp getLastJoined() {
		return this.lastJoined;
	}
	
	// Location
	public void setLogoutLocation(Location loc) {
		World w = loc.getWorld();
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		float yaw = loc.getYaw();
		float pitch = loc.getPitch();
		this.logoutLocaction = loc;
		MySQL.update("UPDATE `"+MySQL.prefix+"players` SET `logoutLocation`='"+w+"_"+x+"_"+y+"_"+z+"_"+yaw+"_"+pitch+"' WHERE `uuid`='"+this.uuid+"'");
	}
	public Location getLocation() {
		return this.logoutLocaction;
	}
	
	// Playtime
	public void countPlayTime() {
		this.playTime += 1;
	}
	public void savePlayTime() {
		MySQL.update("UPDATE `"+MySQL.prefix+"players` SET `playTime`='"+this.playTime+"' WHERE `uuid`='"+this.uuid+"'");
	}
	public Integer getPlayTime() {
		return this.playTime;
	}
	
	// Ban
	public void setBanReason(String reason) {
		MySQL.update("UPDATE `"+MySQL.prefix+"players` SET `banReason`='"+reason+"' WHERE `uuid`='"+this.uuid+"'");
		this.banReason = reason;
	}
	public String getBanReason() {
		return this.banReason;
	}
	public Boolean isBanned() {
		return this.banReason.equals("none") ? false : true;
	}
}
