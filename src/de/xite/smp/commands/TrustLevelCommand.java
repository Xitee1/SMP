package de.xite.smp.commands;

import java.util.ArrayList;
import java.util.List;

import de.xite.smp.main.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.xite.smp.main.Main;
import de.xite.smp.entities.SMPPlayer;
import net.md_5.bungee.api.ChatColor;

public class TrustLevelCommand implements CommandExecutor, TabCompleter{
	static Main pl = Main.pl;
	
	public static final String pr = ChatColor.GRAY+"["+ChatColor.RED+"TrustLevel"+ChatColor.GRAY+"] ";
	private static final String modifyPerm = "smp.trustlevel.modify";

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String arg2, String[] args) {
		if(args.length == 0) {
			// Show TL to player

			if(s instanceof Player) {
				Player p = (Player) s;

				SMPPlayer smpp = SMPPlayer.getPlayer(p.getUniqueId());
				if(!checkSMPPlayer(smpp, s))
					return true;
				
				p.sendMessage(pr+"Dein aktuelles TrustLevel ist "+showTrustLevel(smpp.getTrustLevel())+".");
				if(smpp.getTrustLevel() < SMPPlayer.maxTrustLevel) {
					p.sendMessage(pr+"Um auf Stufe "+(smpp.getTrustLevel() + 1)+" zu kommen, frage es bitte auf unserem "+ChatColor.AQUA+"Discord ( https://discord.gg/PZ2fHC3Wwr )"+ChatColor.GRAY+" an.");
					p.sendMessage(pr+"Bitte beachte, dass du am Anfang nur wenige Rechte bekommst, um griefing zu vermeiden.");
				}
			}else
				s.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
		}else if(args.length == 2 && args[0].equalsIgnoreCase("promote")) {
			// Promote player's TL (+1)

			if(!s.hasPermission(modifyPerm)) {
				s.sendMessage(Messages.generalNoPermission(pr));
				return true;
			}

			SMPPlayer smpp = SMPPlayer.getPlayer(SMPPlayer.nameToUUID(args[1]));
			if(!checkSMPPlayer(smpp, s))
				return true;

			if(smpp.getTrustLevel() == SMPPlayer.maxTrustLevel) {
				s.sendMessage(pr+ChatColor.RED+"Der Spieler "+ChatColor.YELLOW+args[1]+ChatColor.RED+" ist bereits auf der höchsten Stufe.");
			}else {
				smpp.setTrustLevel(smpp.getTrustLevel()+1);
				s.sendMessage(pr+"Der Spieler "+ChatColor.YELLOW+args[1]+ChatColor.GRAY+" hat nun das TrustLevel "+showTrustLevel(smpp.getTrustLevel())+".");
				Player t = Bukkit.getPlayer(args[1]);
				if(t != null)
					t.sendMessage(pr+ChatColor.GREEN+"Herzlichen Glückwunsch! "+ChatColor.GRAY+"Dein TrustLevel wurde erhöht! Du hast nun das TrustLevel "+showTrustLevel(smpp.getTrustLevel()));
			}
		}else if(args.length == 2 && args[0].equalsIgnoreCase("demote")) {
			// Demote player's TL (-1)

			if(!s.hasPermission(modifyPerm)) {
				s.sendMessage(Messages.generalNoPermission(pr));
				return true;
			}

			SMPPlayer smpp = SMPPlayer.getPlayer(SMPPlayer.nameToUUID(args[1]));
			if(!checkSMPPlayer(smpp, s))
				return true;

			if(smpp.getTrustLevel() == 1) {
				s.sendMessage(pr+ChatColor.RED+"Der Spieler "+ChatColor.YELLOW+args[1]+ChatColor.RED+" ist bereits auf der niedrigsten Stufe.");
			}else {
				smpp.setTrustLevel(smpp.getTrustLevel() - 1);
				s.sendMessage(pr+"Der Spieler "+ChatColor.YELLOW+args[1]+ChatColor.GRAY+" hat nun das TrustLevel "+showTrustLevel(smpp.getTrustLevel())+".");
			}
		}else if(args.length == 3 && args[0].equalsIgnoreCase("set")) {
			// Set player's TL

			if(!s.hasPermission(modifyPerm)) {
				s.sendMessage(Messages.generalNoPermission(pr));
				return true;
			}

			int i;
			try {
				i = Integer.parseInt(args[2]);
			}catch (Exception e) {
				s.sendMessage(pr+ChatColor.RED+"Du musst eine gültige Zahl angeben!");
				return true;
			}
			
			SMPPlayer smpp = SMPPlayer.getPlayer(SMPPlayer.nameToUUID(args[1]));
			if(!checkSMPPlayer(smpp, s))
				return true;

			if(i < 1 || i > SMPPlayer.maxTrustLevel) {
				s.sendMessage(pr+ChatColor.RED+"Ungültige Eingabe. Gültige TrustLevel: "+ChatColor.YELLOW+"1"+ChatColor.GRAY+"-"+ChatColor.YELLOW+SMPPlayer.maxTrustLevel);
			}else {
				smpp.setTrustLevel(i);
				s.sendMessage(pr+"Der Spieler "+ChatColor.YELLOW+args[1]+ChatColor.GRAY+" hat nun das TrustLevel "+showTrustLevel(smpp.getTrustLevel())+".");
			}
		}else {
			// No more command args, show correct syntax.

			if(!s.hasPermission(modifyPerm)) {
				s.sendMessage(Messages.commandSyntax(cmd, pr, ""));
			}else
				s.sendMessage(Messages.commandSyntax(cmd, pr, "<promote/demote/set> <Spieler> [Level]"));
		}
		return true;
	}


	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		if(s instanceof Player) {
			Player p = (Player) s;
			if(!p.hasPermission(modifyPerm))
				return list;
		}
		if(args.length == 1) {
			list.add("promote");
			list.add("demote");
			list.add("set");
		}
		if(args.length == 2) {
			for(Player all : Bukkit.getOnlinePlayers())
				list.add(all.getName());
		}
		if(args.length == 3 && args[0].equalsIgnoreCase("set")) {
			for(int i = 1; i <= SMPPlayer.maxTrustLevel; i++)
				list.add(String.valueOf(i));
		}
		
		return list;
	}
	
	private boolean checkSMPPlayer(SMPPlayer smpp, CommandSender s) {
		if(smpp == null) {
			s.sendMessage(Messages.playerNeverOnline(pr, null));
			return false;
		}
		return true;
	}


	private static String showTrustLevel(int current) {
		return ""+ChatColor.YELLOW+current+ChatColor.GRAY+"/"+ChatColor.YELLOW+SMPPlayer.maxTrustLevel+ChatColor.GRAY;
	}
}
