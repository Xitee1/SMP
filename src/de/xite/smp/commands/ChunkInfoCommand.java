package de.xite.smp.commands;

import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChunkInfoCommand implements CommandExecutor {
	public static ArrayList<Player> players = new ArrayList<>();
	String pr = ChatColor.GRAY + "[" + ChatColor.RED + "ChunkInfo" + ChatColor.GRAY + "] ";
	
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (s instanceof Player) {
			Player p = (Player)s;
			if (players.contains(p)) {
				players.remove(p);
				p.sendMessage(String.valueOf(this.pr) + ChatColor.RED + "Chunk-Info deaktiviert.");
			} else {
				players.add(p);
				p.sendMessage(String.valueOf(this.pr) + ChatColor.GREEN + "Chunk-Info aktiviert.");
			} 
		} 
		return true;
	}
}
