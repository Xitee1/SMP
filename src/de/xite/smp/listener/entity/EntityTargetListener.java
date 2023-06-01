package de.xite.smp.listener.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import de.xite.smp.utils.SMPPlayer;

public class EntityTargetListener implements Listener {
	@EventHandler
	public void onMob(EntityTargetLivingEntityEvent e) {
		if(e.getTarget() instanceof Player) {
			Player p = (Player) e.getTarget();
			if(SMPPlayer.getPlayer(p.getUniqueId()).getTrustLevel() == 1) {
				e.setCancelled(true);
				return;
			}
		}
	}
}
