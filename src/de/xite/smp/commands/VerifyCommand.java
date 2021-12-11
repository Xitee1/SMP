package de.xite.smp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xite.smp.main.Main;
import net.md_5.bungee.api.ChatColor;

public class VerifyCommand implements CommandExecutor{
	static Main pl = Main.pl;

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(args.length == 0) {
				p.sendMessage("Du musst dich verifizieren, bevor du mit der Welt interagieren kannst.");
				p.sendMessage("Bitte betrete dazu den XiteSMP Discord Server (https://discord.gg/PZ2fHC3Wwr) und frag Xitee (Owner) nach einem verify.");
			}else if(p.hasPermission("smp.verify") || p.isOp()){
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("list")) {
						String s = "";
						for(String all : Main.verified) {
							s += all+", ";
						}
						s = s.substring(0, s.length()-2);
						p.sendMessage("Verifizierte Spieler: "+s);
					}
				}else
				if(args.length == 2) {
					if(args[0].equalsIgnoreCase("add")) {
						Main.verified.add(args[1]);
						pl.getConfig().set("allowed", Main.verified);
						pl.saveConfig();
						p.sendMessage("Spieler "+args[1]+" wurde hinzugefügt!");
						Player t = Bukkit.getPlayer(args[1]);
						if(t != null) {
							t.setCollidable(true);
							t.sendMessage(ChatColor.GREEN+"Herzlichen Glückwunsch! Du wurdest verifiziert!");
							t.sendMessage(ChatColor.GOLD+"Bitte beachte, dass griefen und hacking verboten ist!! Solltest du griefen oder hacken wirst du umgehend gebannt!");
						}
							
					}else if(args[0].equalsIgnoreCase("remove")) {
						Main.verified.remove(args[1]);
						pl.getConfig().set("allowed", Main.verified);
						pl.saveConfig();
						Player t = Bukkit.getPlayer(args[1]);
						if(t != null)
							t.setCollidable(false);
						p.sendMessage("Spieler "+args[1]+" wurde entfernt!");
					}else {
						p.sendMessage("Syntax: /verify <list/add/remove> [player]");
					}
				}
			}else {
				p.sendMessage("Du darfst keine argumente verwenden!");
			}
		}else {
			Bukkit.getScheduler().runTaskAsynchronously(pl, new Runnable() {
				@Override
				public void run() {
					/*
					sender.sendMessage("Scanning world...");
					ChunkListener.listRegions(Bukkit.getWorld("world"));
					sender.sendMessage("Scanning world_nether...");
					ChunkListener.listRegions(Bukkit.getWorld("world_nether"));
					sender.sendMessage("Scanning world_the_end...");
					ChunkListener.listRegions(Bukkit.getWorld("world_the_end"));
					*/
				}
			});
		}
		return true;
	}

}
