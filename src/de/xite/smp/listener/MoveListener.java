package de.xite.smp.listener;

import de.xite.smp.commands.ChunkInfoCommand;
import de.xite.smp.utils.Actionbar;
import de.xite.smp.utils.MySQL;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {
	public static HashMap<Player, Chunk> lastChunk = new HashMap<>();
  
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (ChunkInfoCommand.players.contains(p)) {
			Chunk chunk = p.getLocation().getChunk();
			if (!lastChunk.containsKey(p))
				lastChunk.put(p, chunk); 
			if (lastChunk.get(p) != chunk) {
				lastChunk.replace(p, chunk);
				if (MySQL.checkExists(String.valueOf(MySQL.prefix) + "chunks", "id", "`loc_x`='" + chunk.getX() + "' AND `loc_z`='" + chunk.getZ() + "'")) {
					String date_created = MySQL.getString(String.valueOf(MySQL.prefix) + "chunks", "date_created", "`loc_x`='" + chunk.getX() + "' AND `loc_z`='" + chunk.getZ() + "'");
					String version_created = MySQL.getString(String.valueOf(MySQL.prefix) + "chunks", "date_created", "`loc_x`='" + chunk.getX() + "' AND `loc_z`='" + chunk.getZ() + "'");
					String date_modified = MySQL.getString(String.valueOf(MySQL.prefix) + "chunks", "date_modified", "`loc_x`='" + chunk.getX() + "' AND `loc_z`='" + chunk.getZ() + "'");
					String version_modified = MySQL.getString(String.valueOf(MySQL.prefix) + "chunks", "date_modified", "`loc_x`='" + chunk.getX() + "' AND `loc_z`='" + chunk.getZ() + "'");
					Actionbar.sendActionBar(p, ChatColor.GREEN + "ChunkInfo " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + 
							"Erstellt am " + ChatColor.AQUA + date_created + ChatColor.GRAY + " mit der Version " + ChatColor.AQUA + version_created + ChatColor.DARK_GRAY + " |" + ChatColor.GRAY + 
							"Bearbeitet am " + ChatColor.AQUA + date_modified + ChatColor.DARK_GRAY + " mit der Version " + ChatColor.AQUA + version_modified + ChatColor.DARK_GRAY, 3600);
				} else {
          Actionbar.sendActionBar(p, ChatColor.GREEN + "ChunkInfo " + ChatColor.DARK_GRAY + "| " + ChatColor.RED + " Keine informationen f�r diesen Chunk.", 3600);
				} 
			} 
		} 
	}
}
