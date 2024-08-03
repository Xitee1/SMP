package de.xite.smp.commands;

import java.util.UUID;

import de.xite.smp.main.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import de.xite.smp.entities.SMPPlayer;
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
			SMPPlayer smpp = SMPPlayer.getPlayer(p.getUniqueId());
			SMPPlayer.updatePlayTime(smpp, p);
			p.sendMessage(prefix +"Deine Spielzeit beträgt: "
					+ChatColor.AQUA + TimeUtils.convertPlayTimeFromSecondsToString(smpp.getPlayTime()));
		}else if(args.length == 1) {
			String playerName = args[0];
			long playTime = getPlaytime(playerName);
			if(playTime == -1) {
				s.sendMessage(Messages.playerNeverOnline(prefix, args[0]));
				return true;
			}
			s.sendMessage(prefix +ChatColor.YELLOW+args[0]+ChatColor.GRAY+"'s Spielzeit beträgt: "
					+ChatColor.AQUA + TimeUtils.convertPlayTimeFromSecondsToString(playTime));
		}else
			if(s instanceof Player) {
				s.sendMessage(Messages.commandSyntax(command, prefix, "<Spieler>"));
			}else
		s.sendMessage(Messages.commandSyntax(command, prefix, "[Spieler]"));
		return true;
	}

	private static long getPlaytime(String playerName) {
		Player t = Bukkit.getPlayer(playerName);
		SMPPlayer smpp;
		if(t == null) {
			UUID uuid = SMPPlayer.nameToUUID(playerName);
			if(uuid == null) {
				return -1;
			}
			smpp = SMPPlayer.getPlayer(uuid);
		}else {
			smpp = SMPPlayer.getPlayer(t.getUniqueId());
			SMPPlayer.updatePlayTime(smpp, t);
		}

		return smpp.getPlayTime();
	}
}
