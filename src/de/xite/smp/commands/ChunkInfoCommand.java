package de.xite.smp.commands;

import net.md_5.bungee.api.ChatColor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xite.smp.main.Main;
import de.xite.smp.sql.MySQL;
import de.xite.smp.utils.Actionbar;

public class ChunkInfoCommand implements CommandExecutor {
	private static String pr = ChatColor.GRAY+"["+ChatColor.RED+"ChunkInfo"+ChatColor.GRAY+"] ";
	public static HashMap<Player, Chunk> lastChunk = new HashMap<>();
	public static HashMap<Player, Boolean> isLoading = new HashMap<>();
	private static Statement statement = null;
	
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(s instanceof Player) {
			Player p = (Player) s;
			if(lastChunk.containsKey(p)) {
				lastChunk.remove(p);
				isLoading.remove(p);
				p.sendMessage(pr+ChatColor.RED+"Chunk-Info deaktiviert.");
				Actionbar.removeActionBar(p);
				if(lastChunk.isEmpty()) {
					try {
						statement.close();
						statement = null;
						Main.pl.getLogger().info("ChunkInfo Statement geschlossen");
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}else {
				if(statement == null) {
					try {
						statement = MySQL.getConnection().createStatement();
						Main.pl.getLogger().info("ChunkInfo Statement erstellt");
					} catch (SQLException e) {
						e.printStackTrace();
						return true;
					}
				}
				
				lastChunk.put(p, null);
				isLoading.put(p, false);
				sendChunkInfoToPlayer(p);
				p.sendMessage(pr+ChatColor.GREEN+"Chunk-Info aktiviert.");
			}
		}
		return true;
	}
	
	private static void sendChunkInfoToPlayer(Player p) {
		if(!lastChunk.containsKey(p))
			return;
		Chunk chunk = p.getLocation().getChunk();
		Bukkit.getScheduler().runTaskAsynchronously(Main.pl, new Runnable() {
			@Override
			public void run() {
				if(lastChunk.get(p) != chunk) {
					lastChunk.replace(p, chunk);
					Actionbar.sendActionBar(p, ChatColor.GREEN+"ChunkInfo "+ChatColor.DARK_GRAY+"| "+ChatColor.GOLD+"Lädt..", 20*60);
				}
				isLoading.replace(p, true);
				try {
					ResultSet rs = statement.executeQuery("SELECT `date_created`,`version_created`,`date_modified`,`version_modified`  FROM `"+MySQL.prefix+"chunks` WHERE "+
							"`world`='"+chunk.getWorld().getName()+"' AND `loc_x`='" + chunk.getX() + "' AND `loc_z`='" + chunk.getZ() +"'");
					if(rs.next()) {
						String date_created = rs.getString("date_created");
						String version_created = rs.getString("version_created");
						String date_modified = rs.getString("date_modified");
						String version_modified = rs.getString("version_modified");
						
						if(date_modified.equals("none") && version_modified.equals("none")) {
							Actionbar.sendActionBar(p, ChatColor.GREEN+"ChunkInfo "+ChatColor.DARK_GRAY+"| "+ChatColor.GRAY+ 
									"Erstellt am "+ChatColor.AQUA+date_created+ChatColor.GRAY+" mit der Version "+ChatColor.AQUA+version_created+ChatColor.DARK_GRAY+" | "+ChatColor.GRAY+ 
									"Originalzustand", 60*60);
						}else {
							Actionbar.sendActionBar(p, ChatColor.GREEN+"ChunkInfo "+ChatColor.DARK_GRAY+"| "+ChatColor.GRAY+
									"Erstellt am "+ChatColor.AQUA+date_created+ChatColor.GRAY+" mit der Version "+ChatColor.AQUA+version_created+ChatColor.DARK_GRAY+" | "+ChatColor.GRAY+
									"Bearbeitet am "+ChatColor.AQUA+date_modified+ChatColor.GRAY+" mit der Version "+ChatColor.AQUA+version_modified, 20*60);
						}
					}else {
						Actionbar.sendActionBar(p, ChatColor.GREEN+"ChunkInfo "+ChatColor.DARK_GRAY+"| "+ChatColor.RED+"Keine informationen für diesen Chunk.", 20*60);
					}
				} catch (SQLException e) {
					Main.pl.getLogger().severe("Konnte keine Daten von der MySQL Datenbank holen!");
					e.printStackTrace();
				}
				isLoading.replace(p, false);
			}
		});
	}
	
	
	public static void chunkInfoUpdater() {
		Bukkit.getScheduler().runTaskTimer(Main.pl, new Runnable() {
			@Override
			public void run() {
				for(Player p : lastChunk.keySet())
					if(!isLoading.get(p))
						sendChunkInfoToPlayer(p);
			}
		}, 20*5, 20);
	}
}
