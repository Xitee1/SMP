package de.xite.smp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import de.xite.smp.commands.ChunkInfoCommand;
import de.xite.smp.commands.TrustLevelCommand;

public class BlockBreakListener implements Listener{
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		
		if(!TrustLevelCommand.checkBlockBreakPlace(p, e.getBlock().getType())) {
			e.setCancelled(true);
			return;
		}
		
		ChunkInfoCommand.registerChunkInteraction(p);
	}
}
