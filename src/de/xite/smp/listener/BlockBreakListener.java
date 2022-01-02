package de.xite.smp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import de.xite.smp.commands.BlockInfoCommand;
import de.xite.smp.commands.TrustLevelCommand;
import de.xite.smp.utils.ChunkManager;

public class BlockBreakListener implements Listener{
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		
		if(!TrustLevelCommand.checkBlockBreakPlace(p, e.getBlock().getType())) {
			e.setCancelled(true);
			return;
		}
		
		if(!BlockInfoCommand.players.contains(p))
			ChunkManager.getChunk(p.getLocation().getChunk(), true).updateChunk();
	}
}
