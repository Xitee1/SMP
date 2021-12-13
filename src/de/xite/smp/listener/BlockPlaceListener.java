package de.xite.smp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import de.xite.smp.commands.ChunkInfoCommand;
import de.xite.smp.commands.TrustLevelCommand;

public class BlockPlaceListener implements Listener {
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		
		if(!TrustLevelCommand.checkBlockBreakPlace(p, e.getBlock().getType())) {
			e.setCancelled(true);
			return;
		}
		
		ChunkInfoCommand.registerChunkInteraction(p);
	}
}
