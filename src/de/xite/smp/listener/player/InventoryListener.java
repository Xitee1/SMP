package de.xite.smp.listener.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import de.xite.smp.main.Messages;
import de.xite.smp.utils.SMPPlayer;

public class InventoryListener implements Listener {
	@EventHandler
	public void onInventoryInteract(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(SMPPlayer.getPlayer(p).getTrustLevel() == 1) {
				if(e.getClickedInventory().getType() == InventoryType.CHEST) {
					e.setCancelled(true);
					Messages.trustLevelNoAccess(p);
					return;
				}
			}
		}
	}
}
