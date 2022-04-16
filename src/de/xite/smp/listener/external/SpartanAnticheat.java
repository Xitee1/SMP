package de.xite.smp.listener.external;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.xite.smp.utils.SMPPlayer;
import me.vagdedes.spartan.api.PlayerViolationEvent;

public class SpartanAnticheat implements Listener {
	
	@EventHandler
	public void Event(PlayerViolationEvent e) {
		Player p = e.getPlayer();
		if(SMPPlayer.getPlayer(p).getTrustLevel() == SMPPlayer.maxTrustLevel) {
			e.setCancelled(true);
		}
	}
}
