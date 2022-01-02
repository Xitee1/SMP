package de.xite.smp.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.xite.smp.main.Main;
import de.xite.smp.sql.MySQL;
import net.md_5.bungee.api.ChatColor;

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
	
	private SMPPlayer(UUID uuid) {
		try {
			Connection c = MySQL.getConnection();
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM `"+MySQL.prefix+"players` WHERE `uuid`='"+uuid+"'");
			this.uuid = uuid;
			if(rs.next()) {
				this.name = rs.getString("name");
				this.trustlevel = rs.getInt("trustLevel");
				this.firstJoined = rs.getTimestamp("firstJoined");
				this.lastJoined = rs.getTimestamp("lastJoined");
				this.playTime = rs.getInt("playtime");
				this.banReason = rs.getString("banReason");
				String[] location = rs.getString("logoutLocation").split("%");
				this.logoutLocaction = new Location(Bukkit.getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]),
						Float.parseFloat(location[4]), Float.parseFloat(location[5]));
			}else {
				Player p = Bukkit.getPlayer(uuid);
				if(p != null) {
					this.name = p.getName();
					Bukkit.getScheduler().runTaskLater(Main.pl, new Runnable() {
						@Override
						public void run() {
							for(Player all : Bukkit.getOnlinePlayers())
								all.sendMessage(ChatColor.GREEN+"Neuer Spieler! Herzlich Willkommen, "+ChatColor.YELLOW+p.getName()+ChatColor.GREEN+"!");
						}
					}, 20);
				}else {
					this.name = NameFetcher.getName(uuid);
				}
				st.executeUpdate("INSERT INTO `"+MySQL.prefix+"players` (`id`, `uuid`, `name`, `trustlevel`, `firstJoined`, `lastJoined`, `logoutLocation`, `playTime`, `banReason`) VALUES"
						+ "(NULL, '"+uuid+"', '"+this.name+"', '1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'world%0%0%0%0%0', '0', 'none')");
				this.trustlevel = 1;
				this.firstJoined = Timestamp.valueOf(LocalDateTime.now());
				this.lastJoined = Timestamp.valueOf(LocalDateTime.now());
				this.logoutLocaction = null;
				this.playTime = 0;
				this.banReason = "none";
				
				
			}
			rs.close();
			st.close();
			c.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	public static SMPPlayer getPlayer(UUID uuid) {
		if(!players.containsKey(uuid))
			players.put(uuid, new SMPPlayer(uuid));
		return players.get(uuid);
	}
	public static SMPPlayer getPlayer(Player p) {
		return getPlayer(p.getUniqueId());
	}
	
	public static UUID getUUID(String name) {
		Statement st;
		UUID uuid = null;
		try {
			Connection c = MySQL.getConnection();
			st = c.createStatement();
			uuid = UUID.fromString(MySQL.getString(st, MySQL.prefix+"players", "uuid", "`name`='"+name+"'"));
			st.close();
			c.close();
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
		MySQL.update("UPDATE `"+MySQL.prefix+"players` SET `lastJoined`=CURRENT_TIMESTAMP WHERE `uuid`='"+this.uuid+"'");
	}
	public Timestamp getLastJoined() {
		return this.lastJoined;
	}
	
	// Location
	public void setLogoutLocation(Location loc) {
		String w = loc.getWorld().getName();
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		float yaw = loc.getYaw();
		float pitch = loc.getPitch();
		this.logoutLocaction = loc;
		MySQL.update("UPDATE `"+MySQL.prefix+"players` SET `logoutLocation`='"+w+"%"+x+"%"+y+"%"+z+"%"+yaw+"%"+pitch+"' WHERE `uuid`='"+this.uuid+"'");
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
