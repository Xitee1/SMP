package de.xite.smp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.ChatColor;

public class HelpCommand implements CommandExecutor{
	
	String pr = ChatColor.GRAY+"["+ChatColor.RED+"Hilfe"+ChatColor.GRAY+"] ";
	
	@Override
	public boolean onCommand(@NotNull CommandSender s, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		s.sendMessage(pr+"Nützliche Befehle:");
		s.sendMessage(pr+ChatColor.GRAY+"- "+ChatColor.AQUA+"/hilfe "+ChatColor.GRAY+"| Dieses Hilfe-Menü");
		s.sendMessage(pr+ChatColor.GRAY+"- "+ChatColor.AQUA+"/trustlevel "+ChatColor.GRAY+"| Zeigt dir dein TrustLevel an");
		s.sendMessage(pr+ChatColor.GRAY+"- "+ChatColor.AQUA+"/ci "+ChatColor.GRAY+"| Zeigt dir Infos über die Chunks an");
		s.sendMessage(pr+ChatColor.GRAY+"- "+ChatColor.AQUA+"/bi "+ChatColor.GRAY+"| Zeigt dir Infos über einen Block an");
		return true;
	}
}
