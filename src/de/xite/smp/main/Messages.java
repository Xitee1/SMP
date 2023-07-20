package de.xite.smp.main;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import de.xite.smp.utils.Actionbar;
import de.xite.smp.utils.SMPPlayer;
import net.md_5.bungee.api.ChatColor;

public class Messages {
	public static void trustLevelNoAccess(Player p) {
		Actionbar.sendActionBar(p, 
				ChatColor.RED+"Du hast noch keine Rechte, um mit der Welt zu Interagieren. Mehr Infos: "+ChatColor.AQUA+"/trustlevel");
	}

	public static Component generalNoPermission(String prefix) {
		return Component.text(prefix + ChatColor.RED+"Du hast keine Berechtigung, um diesen Befehl auszuführen!");
	}

	public static Component playerNeverOnline(String prefix, String player) {
		if(player == null)
			return Component.text(prefix + ChatColor.RED+"Der angeforderte Spieler war noch nie online.");

		return Component.text(prefix + ChatColor.RED+"Der Spieler "+ChatColor.YELLOW+player+ChatColor.RED+" war noch nie online.");
	}

	public static void trustLevelOnlineNeeded(Player p) {
		Actionbar.sendActionBar(p, 
				ChatColor.RED+"Da dein TrustLevel zu gering ist, muss ein Spieler mit TL "+ChatColor.YELLOW+SMPPlayer.maxTrustLevel+ChatColor.RED+" online sein.");
	}

	public static void trustLevelDangerousBlock(Player p) {
		Actionbar.sendActionBar(p, 
				ChatColor.RED+"Du benötigst TrustLevel "+ChatColor.YELLOW+"4"+ChatColor.RED+", um dies zu tun. Mehr Infos im Discord.");
	}
	
	public static void broadcastToMaxTrustLevelPlayers(String message) {
		for(Player all : Bukkit.getOnlinePlayers()) {
			if(SMPPlayer.getPlayer(all.getUniqueId()).getTrustLevel() == SMPPlayer.maxTrustLevel) {
				all.sendMessage(message);
			}
		}
		Main.pl.getLogger().info("TrustLevel broadcast: "+message);
	}

	public static Component commandSyntax(Command c, String prefix, String parameters) {
		return Component.text(prefix+ChatColor.RED+"Syntax: "+ChatColor.AQUA+"/"+c.getName() + " "+parameters);
	}
}
