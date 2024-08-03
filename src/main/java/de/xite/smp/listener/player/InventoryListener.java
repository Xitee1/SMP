package de.xite.smp.listener.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import de.xite.smp.main.Messages;
import de.xite.smp.entities.SMPPlayer;

public class InventoryListener implements Listener {
	@EventHandler
	public void onInventoryInteract(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			SMPPlayer smpp = SMPPlayer.getPlayer(p.getUniqueId());
			
			if(smpp.getTrustLevel() == 1) {
				InventoryType invType = e.getClickedInventory().getType();
				if(invType != InventoryType.PLAYER && invType != InventoryType.CRAFTING && invType != InventoryType.WORKBENCH) {
					e.setCancelled(true);
					Messages.trustLevelNoAccess(p);
					return;
				}
			}
			
			if(smpp.getTrustLevel() == 2) {
				if(!SMPPlayer.isLVLmaxOnline()) {
					Messages.trustLevelOnlineNeeded(p);
					return;
				}
			}
		}
	}
}
