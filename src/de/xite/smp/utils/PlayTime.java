package de.xite.smp.utils;

import de.xite.smp.entities.SMPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.xite.smp.main.Main;

public class PlayTime {
	public static void startPlaytimeCounter() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(Main.pl, () -> {
			for(Player p : Bukkit.getOnlinePlayers())
				SMPPlayer.getPlayer(p.getUniqueId()).countPlayTime();
		}, 20, 20);
	}
}
