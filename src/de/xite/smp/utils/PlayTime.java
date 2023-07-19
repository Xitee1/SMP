package de.xite.smp.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.xite.smp.main.Main;

public class PlayTime {
	public static void startPlaytimeCounter() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(Main.pl, () -> {
			for(Player p : Bukkit.getOnlinePlayers())
				SMPPlayer.getPlayer(p.getUniqueId()).countPlayTime();
		}, 20, 20);
		
		// Save the playtime every 5 minutes (in case server crashes)
		Bukkit.getScheduler().runTaskTimerAsynchronously(Main.pl, () -> {
			for(Player p : Bukkit.getOnlinePlayers())
				SMPPlayer.getPlayer(p.getUniqueId()).savePlayTime();
		}, 20*60*5, 20*60*5);
	}
}
