package de.xite.smp.listener.player;

import de.xite.smp.database.statement.ChunkStatement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import de.xite.smp.main.Main;
import de.xite.smp.main.Messages;
import de.xite.smp.utils.SMPPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BlockBreakPlaceListener implements Listener {
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		
		if(!isPlayerAllowedToBreakOrPlace(p, e.getBlock().getType())) {
			e.setCancelled(true);
			return;
		}
		
		if(!e.isCancelled()) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			String dateModified = sdf.format(new Date());
			String versionModified = Main.MCVersion;
			ChunkStatement.update(p.getLocation().getChunk(), dateModified, versionModified);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		
		if(!isPlayerAllowedToBreakOrPlace(p, e.getBlock().getType())) {
			e.setCancelled(true);
			return;
		}

		if(!e.isCancelled()) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			String dateModified = sdf.format(new Date());
			String versionModified = Main.MCVersion;
			ChunkStatement.update(p.getLocation().getChunk(), dateModified, versionModified);
		}
	}
	
	
	public Boolean isPlayerAllowedToBreakOrPlace(Player p, Material m) {
		/*
		 * Same thing as in InteractListener,
		 * however without the check against the TL 1 allowed list,
		 * because the player is only allowed to interact with them, not break or place them.
		 */
		
		SMPPlayer smpp = SMPPlayer.getPlayer(p.getUniqueId());
		
		if(smpp.getTrustLevel() == 1) {
			Messages.trustLevelNoAccess(p);
			return false;
		}
		if(smpp.getTrustLevel() == 2) {
			if(!SMPPlayer.isLVLmaxOnline()) {
				Messages.trustLevelOnlineNeeded(p);
				return false;
			}else {
				if(Main.dangerousBlocks.contains(m)) {
					Messages.trustLevelDangerousBlock(p);
					return false;
				}
			}
		}
		if(smpp.getTrustLevel() == 3) {
			if(Main.dangerousBlocks.contains(m)) {
				Messages.trustLevelDangerousBlock(p);
				return false;
			}
		}
		if(smpp.getTrustLevel() == 4) {
			if(!SMPPlayer.isLVLmaxOnline()) {
				if(Main.dangerousBlocks.contains(m)) {
					Messages.trustLevelOnlineNeeded(p);
					return false;
				}
			}
		}
		return true;
	}
}
