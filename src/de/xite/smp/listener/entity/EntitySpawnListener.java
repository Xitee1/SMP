package de.xite.smp.listener.entity;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpellCastEvent;

public class EntitySpawnListener {
	@EventHandler
	public void onSpawn(EntitySpellCastEvent e) {
		if(e.getEntityType() == EntityType.PHANTOM) {
			e.setCancelled(true);
			return;
		}
	}
}
