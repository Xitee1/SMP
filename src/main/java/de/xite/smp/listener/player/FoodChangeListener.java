package de.xite.smp.listener.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.xite.smp.entities.SMPPlayer;

public class FoodChangeListener implements Listener{
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(SMPPlayer.getPlayer(p.getUniqueId()).getTrustLevel() == 1) {
				e.setCancelled(true);
			}
		}
	}
}
