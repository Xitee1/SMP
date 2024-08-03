package de.xite.smp.commands;

import java.util.HashMap;

import de.xite.smp.utils.ChunkInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChunkInfoCommand implements CommandExecutor {
	private static final String prefix = ChatColor.GRAY+"["+ ChatColor.RED+"ChunkInfo"+ChatColor.GRAY+"] ";
	//public static HashMap<Player, Chunk> lastChunk = new HashMap<>();
	//public static HashMap<Player, Boolean> isLoading = new HashMap<>();
	//private static Statement statement = null;
	//private static Connection c = null;
	public static HashMap<Player, ChunkInfo> chunkInfoList = new HashMap<>();
	
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(!(s instanceof Player)) {
			s.sendMessage(Component.text("Dieser Befehl kann nur von Spielern ausgeführt werden."));
			return true;
		}
		Player p = (Player) s;

		if(chunkInfoList.containsKey(p)) {
			ChunkInfo chunkInfo = chunkInfoList.get(p);
			chunkInfo.stopSendingChunkInfoToPlayer();
			chunkInfoList.remove(p);

			p.sendMessage(prefix+ChatColor.RED+"Chunk-Info wird nicht mehr angezeigt.");
		}else {
			ChunkInfo chunkInfo = new ChunkInfo(p);
			chunkInfo.startSendingChunkInfoToPlayer();
			chunkInfoList.put(p, chunkInfo);

			p.sendMessage(prefix+ChatColor.GREEN+"Chunk-Info wird nun angezeigt.");
		}

		/*
		if(lastChunk.containsKey(p)) {
			lastChunk.remove(p);
			isLoading.remove(p);
			p.sendMessage(pr+ChatColor.RED+"Chunk-Info deaktiviert.");
			Actionbar.removeActionbar(p);
			if(lastChunk.isEmpty()) {
				try {
					if(statement != null) {
						statement.close();
						statement = null;
					}
					if(c != null) {
						c.close();
						c = null;
					}
					Main.pl.getLogger().info("ChunkInfo Statement geschlossen");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}else {
			if(statement == null) {
				try {
					c = MySQL.getConnection();
					statement = c.createStatement();
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

		 */
		return true;
	}
	/*
	private static void sendChunkInfoToPlayer(Player p) {
		if(!lastChunk.containsKey(p))
			return;
		Chunk chunk = p.getLocation().getChunk();
		Bukkit.getScheduler().runTaskAsynchronously(Main.pl, new Runnable() {
			@Override
			public void run() {
				try {
					if(statement == null || statement.isClosed()) {
						Actionbar.sendActionBar(p, ChatColor.GREEN+"ChunkInfo "+ChatColor.DARK_GRAY+"| "+ChatColor.GOLD+"Fehler! Schalte ChunkInfo aus und wieder ein.", 20*60);
						return;
					}
						
				} catch (SQLException e1) {
					Actionbar.sendActionBar(p, ChatColor.GREEN+"ChunkInfo "+ChatColor.DARK_GRAY+"| "+ChatColor.GOLD+"Fehler! Schalte ChunkInfo aus und wieder ein.", 20*60);
					e1.printStackTrace();
					return;
				}
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
						
						String date_modified, version_modified;
						ChunkManager cm = ChunkManager.getChunk(chunk, false);
						if(cm != null) {
							date_modified = cm.getDateModified();
							version_modified = cm.getVersionModified();
						}else {
							date_modified = rs.getString("date_modified");
							version_modified = rs.getString("version_modified");
						}
						
						if(date_modified.equals("none")) {
							Actionbar.sendActionBar(p, ChatColor.GREEN+"ChunkInfo "+ChatColor.DARK_GRAY+"| "+ChatColor.GRAY+ 
									"Erstellt am "+ChatColor.AQUA+date_created+ChatColor.GRAY+" mit der Version "+ChatColor.AQUA+version_created+ChatColor.DARK_GRAY+" | "+ChatColor.GRAY+ 
									"Originalzustand", 60*60);
						}else {
							Actionbar.sendActionBar(p, ChatColor.GREEN+"ChunkInfo "+ChatColor.DARK_GRAY+"| "+ChatColor.GRAY+
									"Erstellt: "+ChatColor.AQUA+date_created+ChatColor.GRAY+" ("+ChatColor.AQUA+version_created+ChatColor.GRAY+")"+ChatColor.DARK_GRAY+" | "+ChatColor.GRAY+
									"Bearbeitet: "+ChatColor.AQUA+date_modified+ChatColor.GRAY+" ("+ChatColor.AQUA+version_modified+ChatColor.GRAY+")", 20*60);
						}
					}else {
						Actionbar.sendActionBar(p, ChatColor.GREEN+"ChunkInfo "+ChatColor.DARK_GRAY+"| "+ChatColor.RED+"Keine Informationen für diesen Chunk.", 20*60);
					}
				} catch (SQLException e) {
					Main.pl.getLogger().severe("Konnte keine Daten von der MySQL Datenbank holen!");
					Actionbar.sendActionBar(p, ChatColor.GREEN+"ChunkInfo "+ChatColor.DARK_GRAY+"| "+ChatColor.GOLD+"Fehler! Schalte ChunkInfo aus und wieder ein.", 20*60);
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

	 */
}
