package de.xite.smp.commands;

import java.util.UUID;

import de.xite.smp.main.Messages;
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
	String prefix = ChatColor.GRAY+"["+ChatColor.RED+"Spielzeit"+ChatColor.GRAY+"] ";
	
	@Override
	public boolean onCommand(@NotNull CommandSender s, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(args.length == 0) {
			if(!(s instanceof Player)) {
				s.sendMessage(prefix +ChatColor.RED+"Diesen Befehl kann nur ein Spieler ausführen!");
				s.sendMessage(Messages.commandSyntax(command, prefix, "[Spieler]"));
				return true;
			}
			Player p = (Player) s;
			SMPPlayer sp = SMPPlayer.getPlayer(p.getUniqueId());
			p.sendMessage(prefix +"Deine Spielzeit beträgt: "+ChatColor.AQUA + TimeUtils.convertPlayTimeFromSecondsToString(sp.getPlayTime()));
		}else if(args.length == 1) {
			Player t = Bukkit.getPlayer(args[0]);
			UUID uuid;
			if(t == null) {
				uuid = SMPPlayer.nameToUUID(args[0]);
			}else
				uuid = t.getUniqueId();
			if(uuid == null) {
				s.sendMessage(Messages.playerNeverOnline(prefix, args[0]));
				return true;
			}
			s.sendMessage(prefix +ChatColor.YELLOW+args[0]+ChatColor.GRAY+"'s Spielzeit beträgt: "+ChatColor.AQUA + TimeUtils.convertPlayTimeFromSecondsToString(SMPPlayer.getPlayer(uuid).getPlayTime()));
		}else
			if(s instanceof Player) {
				s.sendMessage(Messages.commandSyntax(command, prefix, "<Spieler>"));
			}else
		s.sendMessage(Messages.commandSyntax(command, prefix, "[Spieler]"));
		return true;
	}
}
