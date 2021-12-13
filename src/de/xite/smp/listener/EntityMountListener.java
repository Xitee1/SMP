package de.xite.smp.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityMountEvent;

import de.xite.smp.main.Messages;
import de.xite.smp.utils.SMPPlayer;

public class EntityMountListener implements Listener {
	@EventHandler
	public void onRide(EntityMountEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(SMPPlayer.getPlayer(p).getTrustLevel() == 1) {
				if(e.getMount().getType() == EntityType.LLAMA ||
					e.getMount().getType() == EntityType.HORSE ||
					e.getMount().getType() == EntityType.SKELETON_HORSE ||
					e.getMount().getType() == EntityType.DONKEY ||
					e.getMount().getType() == EntityType.STRIDER ||
					e.getMount().getType() == EntityType.PIG) {
					
					e.setCancelled(true);
					Messages.trustLevelNoAccess(p);
				}
			}	
		}
	}
}
