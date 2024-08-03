package de.xite.smp.listener.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.xite.smp.main.Messages;
import de.xite.smp.entities.SMPPlayer;

public class EntityDamageListener implements Listener {
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if(SMPPlayer.getPlayer(p.getUniqueId()).getTrustLevel() == 1) {
				e.setCancelled(true);
				Messages.trustLevelNoAccess(p);
			}
		}
	}
}
