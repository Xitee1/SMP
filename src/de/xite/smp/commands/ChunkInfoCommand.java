package de.xite.smp.commands;

import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xite.smp.main.Main;
import de.xite.smp.utils.Actionbar;
import de.xite.smp.utils.MySQL;

public class ChunkInfoCommand implements CommandExecutor {
	String pr = ChatColor.GRAY+"["+ChatColor.RED+"ChunkInfo"+ChatColor.GRAY+"] ";
	public static HashMap<Player, Chunk> lastChunk = new HashMap<>();
	
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(s instanceof Player) {
			Player p = (Player) s;
			if(lastChunk.containsKey(p)) {
				lastChunk.remove(p);
				p.sendMessage(pr+ChatColor.RED+"Chunk-Info deaktiviert.");
			}else {
				lastChunk.put(p, p.getLocation().getChunk());
				p.sendMessage(pr+ChatColor.GREEN+"Chunk-Info aktiviert.");
				sendChunkInfoToPlayer(p);
			}
		}
		return true;
	}
	
	public static void sendChunkInfoToPlayer(Player p) {
		Chunk chunk = p.getLocation().getChunk();
		if(lastChunk.containsKey(p)) {
			Bukkit.getScheduler().runTaskAsynchronously(Main.pl, new Runnable() {
				@Override
				public void run() {
					if(lastChunk.get(p) != chunk) {
						lastChunk.replace(p, chunk);
						if(MySQL.checkExists(MySQL.prefix+"chunks", "id", "`loc_x`='" + chunk.getX() + "' AND `loc_z`='" + chunk.getZ() + "'")) {
							String date_created = MySQL.getString(MySQL.prefix+"chunks", "date_created", "`loc_x`='" + chunk.getX() + "' AND `loc_z`='" + chunk.getZ() + "'");
							String version_created = MySQL.getString(MySQL.prefix+"chunks", "version_created", "`loc_x`='" + chunk.getX() + "' AND `loc_z`='" + chunk.getZ() + "'");
							String date_modified = MySQL.getString(MySQL.prefix+"chunks", "date_modified", "`loc_x`='" + chunk.getX() + "' AND `loc_z`='" + chunk.getZ() + "'");
							String version_modified = MySQL.getString(MySQL.prefix+"chunks", "version_modified", "`loc_x`='" + chunk.getX() + "' AND `loc_z`='" + chunk.getZ() + "'");
							if(date_modified.equals("none") && version_modified.equals("none")) {
								Actionbar.sendActionBar(p, ChatColor.GREEN+"ChunkInfo "+ChatColor.DARK_GRAY + "| "+ChatColor.GRAY+ 
										"Erstellt am "+ChatColor.AQUA+date_created+ChatColor.GRAY+" mit der Version "+ChatColor.AQUA+version_created+ChatColor.DARK_GRAY+" | "+ChatColor.GRAY+ 
										"Originalzustand", 60*60);
							}else {
								Actionbar.sendActionBar(p, ChatColor.GREEN+"ChunkInfo "+ChatColor.DARK_GRAY+"| "+ChatColor.GRAY+
										"Erstellt am "+ChatColor.AQUA+date_created+ChatColor.GRAY+" mit der Version "+ChatColor.AQUA+version_created+ChatColor.DARK_GRAY+" | "+ChatColor.GRAY+
										"Bearbeitet am "+ChatColor.AQUA+date_modified+ChatColor.GRAY+" mit der Version "+ChatColor.AQUA+version_modified, 60*60);
							}
							
						}else {
							Actionbar.sendActionBar(p, ChatColor.GREEN+"ChunkInfo "+ChatColor.DARK_GRAY+"| "+ChatColor.RED+"Keine informationen für diesen Chunk.", 60*60);
						} 
					}
				}
			});
		}
	}
}
