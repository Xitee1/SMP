package de.xite.smp.listener.world;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import de.xite.smp.commands.BlockInfoCommand;
import de.xite.smp.commands.TrustLevelCommand;
import de.xite.smp.utils.ChunkManager;

public class BlockPlaceListener implements Listener {
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		
		if(!TrustLevelCommand.checkBlockBreakPlace(p, e.getBlock().getType())) {
			e.setCancelled(true);
			return;
		}
		
		if(!BlockInfoCommand.players.contains(p))
			ChunkManager.getChunk(p.getLocation().getChunk(), true).updateChunk();
	}
}
