package de.xite.smp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import de.xite.smp.commands.ChunkInfoCommand;

public class MoveListener implements Listener {
  
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		ChunkInfoCommand.sendChunkInfoToPlayer(p);
	}
}
