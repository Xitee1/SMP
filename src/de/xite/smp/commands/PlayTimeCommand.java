package de.xite.smp.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import de.xite.smp.utils.SMPPlayer;
import de.xite.smp.utils.TimeUtils;
import net.md_5.bungee.api.ChatColor;

public class PlayTimeCommand implements CommandExecutor{
	String pr = ChatColor.GRAY+"["+ChatColor.RED+"Spielzeit"+ChatColor.GRAY+"] ";
	
	@Override
	public boolean onCommand(@NotNull CommandSender s, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(args.length == 0) {
			if(!(s instanceof Player)) {
				s.sendMessage(pr+ChatColor.RED+"Dieser Befehl kann nur ein Spieler ausführen!");
				s.sendMessage(pr+ChatColor.RED+"Syntax: "+ChatColor.AQUA+"/spielzeit [Spieler]");
				return true;
			}
			Player p = (Player) s;
			SMPPlayer sp = SMPPlayer.getPlayer(p.getUniqueId());
			p.sendMessage(pr+"Deine Spielzeit: "+TimeUtils.convertPlayTimeFromSecondsToString(sp.getPlayTime()));
		}else if(args.length == 1) {
			Player t = Bukkit.getPlayer(args[0]);
			UUID uuid = null;
			if(t == null) {
				uuid = SMPPlayer.nameToUUID(args[0]);
			}else
				uuid = t.getUniqueId();
			if(uuid == null) {
				s.sendMessage(pr+ChatColor.RED+"Der Spieler "+ChatColor.YELLOW+args[0]+ChatColor.RED+" war noch nie online.");
				return true;
			}
			s.sendMessage(pr+ChatColor.YELLOW+args[0]+ChatColor.GRAY+"'s Spielzeit: "+TimeUtils.convertPlayTimeFromSecondsToString(SMPPlayer.getPlayer(uuid).getPlayTime()));
		}else
			if(s instanceof Player) {
				s.sendMessage(pr+ChatColor.RED+"Syntax: "+ChatColor.AQUA+"/spielzeit <Spieler>");
			}else
				s.sendMessage(pr+ChatColor.RED+"Syntax: "+ChatColor.AQUA+"/spielzeit [Spieler]");
		return true;
	}
}
