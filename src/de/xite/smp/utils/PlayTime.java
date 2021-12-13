package de.xite.smp.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.xite.smp.main.Main;

public class PlayTime {
	public static void startPlaytimeCounter() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(Main.pl, new Runnable() {
			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers())
					SMPPlayer.getPlayer(p).countPlayTime();
			}
		}, 20, 20);
		
		// Save the playtime every 30 minutes (in case server crashes)
		Bukkit.getScheduler().runTaskTimerAsynchronously(Main.pl, new Runnable() {
			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers())
					SMPPlayer.getPlayer(p).savePlayTime();
			}
		}, 20*60*30, 20*60*30);
	}
}
