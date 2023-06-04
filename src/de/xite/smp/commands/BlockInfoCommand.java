package de.xite.smp.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class BlockInfoCommand implements CommandExecutor{
	public static final String pr = ChatColor.GRAY+"["+ChatColor.RED+"BlockInfo"+ChatColor.GRAY+"] ";
	public static ArrayList<Player> players = new ArrayList<>();
	public static HashMap<Player, Long> fastLookupThrottle = new HashMap<>();
	
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(s instanceof Player) {
			Player p = (Player) s;
			if(players.contains(p)) {
				players.remove(p);
				p.sendMessage(pr+ChatColor.RED+"Block-Info deaktiviert.");
			} else {
				players.add(p);
				p.sendMessage(pr+ChatColor.GREEN+"Block-Info aktiviert. Link-klicke, um Block-Infos zu sehen - Rechts-klicke auf Kisten, Trichter oder ähnliches um Item Transaktionen zu sehen.");
			}
		}
		return true;
	}
}
