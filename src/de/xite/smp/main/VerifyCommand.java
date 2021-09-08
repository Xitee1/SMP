package de.xite.smp.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VerifyCommand implements CommandExecutor{
	static Main pl = Main.pl;

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(args.length == 0) {
				p.sendMessage("Du musst dich verifizieren bevor du mit der Welt interagieren kannst.");
				p.sendMessage("Schreibe mir dazu bitte eine kurze Email an xitecraft1@gmail.com.");
				p.sendMessage("Diese Mail muss nur deinen Ingamename enthalten.");
				p.sendMessage("Innerhalb einem Tag werde ich dich zur Whitelist hinzufügen. Es könnte aber auch mal ein paar Tage länger dauern.");
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
					}else if(args[0].equalsIgnoreCase("remove")) {
						Main.verified.remove(args[1]);
						pl.getConfig().set("allowed", Main.verified);
						pl.saveConfig();
						
						p.sendMessage("Spieler "+args[1]+" wurde entfernt!");
					}else {
						p.sendMessage("Syntax: /verify <list/add/remove> [player]");
					}
				}
			}else {
				p.sendMessage("Du darfst keine argumente verwenden!");
			}
		}
		return true;
	}

}
