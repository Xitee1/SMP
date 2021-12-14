package de.xite.smp.main;

import org.bukkit.entity.Player;

import de.xite.smp.utils.Actionbar;
import de.xite.smp.utils.SMPPlayer;
import net.md_5.bungee.api.ChatColor;

public class Messages {
	public static void trustLevelNoAccess(Player p) {
		Actionbar.sendActionBar(p, 
				ChatColor.RED+"Du hast noch keine Rechte, um mit der Welt zu Interagieren. Mehr Infos: "+ChatColor.AQUA+"/trustlevel");
	}
	public static void trustLevelOnlineNeeded(Player p) {
		Actionbar.sendActionBar(p, 
				ChatColor.RED+"Ein Spieler mit TrustLevel "+ChatColor.YELLOW+SMPPlayer.maxTrustLevel+ChatColor.RED+" muss online sein. Mehr Infos: "+ChatColor.AQUA+"/trustlevel");
	}
	public static void trustLevelDangerousBlock(Player p) {
		Actionbar.sendActionBar(p, 
				ChatColor.RED+"Du benötigst TrustLevel "+ChatColor.YELLOW+"4"+ChatColor.RED+", um dies zu tun. Mehr Infos: "+ChatColor.AQUA+"/trustlevel");
	}
}
