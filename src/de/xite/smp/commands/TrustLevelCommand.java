package de.xite.smp.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xite.smp.main.Main;
import de.xite.smp.main.Messages;
import de.xite.smp.utils.SMPPlayer;
import de.xite.smp.utils.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;

public class TrustLevelCommand implements CommandExecutor{
	static Main pl = Main.pl;
	
	String pr = ChatColor.GRAY+"["+ChatColor.RED+"Verify"+ChatColor.GRAY+"] ";

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(args.length == 0) {
			if(s instanceof Player) {
				Player p = (Player) s;
				SMPPlayer sp = SMPPlayer.getPlayer(p);
				
				p.sendMessage(pr+"Dein aktuelles TrustLevel ist "+ChatColor.YELLOW+sp.getTrustLevel()+ChatColor.GRAY+"/"+ChatColor.YELLOW+SMPPlayer.maxTrustLevel+ChatColor.GRAY+".");
				if(sp.getTrustLevel() == 0) {
					p.sendMessage(pr+"Um auf Stufe 1 zu kommen, betrete bitte unseren Discord (https://discord.gg/PZ2fHC3Wwr) und frag Xitee (Owner).");
					p.sendMessage(pr+"Mit TrustLevel 1 hast du schon sehr viele Freiheiten und kannst überall bauen (wenn jmd. mit Level "+SMPPlayer.maxTrustLevel+" online ist).");
				}
			}
		}else if(args.length == 2 && args[0].equalsIgnoreCase("promote")) {
			if(s instanceof Player) {
				Player p = (Player) s;
				if(!p.hasPermission("smp.trustlevel.modify")) {
					p.sendMessage(pr+ChatColor.RED+"Du hast nicht die ausreichende Rechte, um dies zu tun!");
					return true;
				}
			}
			SMPPlayer sp = SMPPlayer.getPlayer(UUIDFetcher.getUUID(args[1]));
			if(sp.getTrustLevel() == SMPPlayer.maxTrustLevel) {
				s.sendMessage(pr+ChatColor.RED+"Der Spieler "+ChatColor.YELLOW+args[1]+ChatColor.RED+" ist bereits auf der höchsten Stufe.");
			}else {
				sp.setTrustLevel(sp.getTrustLevel()+1);
				s.sendMessage(pr+"Der Spieler "+ChatColor.YELLOW+args[1]+ChatColor.GRAY+" hat nun das TrustLevel "+ChatColor.YELLOW+sp.getTrustLevel()+ChatColor.GRAY+"/"+ChatColor.YELLOW+SMPPlayer.maxTrustLevel+ChatColor.GRAY+".");
				Player t = Bukkit.getPlayer(args[1]);
				if(t != null)
					t.sendMessage(pr+ChatColor.GREEN+"Herzlichen Glückwunsch! "+ChatColor.GRAY+"Dein TrustLevel wurde von "+ChatColor.YELLOW+(sp.getTrustLevel()-1)+ChatColor.GRAY+" auf "+ChatColor.YELLOW+sp.getTrustLevel()+ChatColor.GRAY+" erhöht!");
			}
		}else if(args.length == 2 && args[0].equalsIgnoreCase("demote")) {
			if(s instanceof Player) {
				Player p = (Player) s;
				if(!p.hasPermission("smp.trustlevel.modify")) {
					p.sendMessage(pr+ChatColor.RED+"Du hast nicht die ausreichende Rechte, um dies zu tun!");
					return true;
				}
			}
			SMPPlayer sp = SMPPlayer.getPlayer(UUIDFetcher.getUUID(args[1]));
			if(sp.getTrustLevel() == 1) {
				s.sendMessage(pr+ChatColor.RED+"Der Spieler "+ChatColor.YELLOW+args[1]+ChatColor.RED+" ist bereits auf der niedrigsten Stufe.");
			}else {
				sp.setTrustLevel(sp.getTrustLevel()-1);
				s.sendMessage(pr+"Der Spieler "+ChatColor.YELLOW+args[1]+ChatColor.GRAY+" hat nun das TrustLevel "+ChatColor.YELLOW+sp.getTrustLevel()+ChatColor.GRAY+"/"+ChatColor.YELLOW+SMPPlayer.maxTrustLevel+ChatColor.GRAY+".");
			}
		}else if(args.length == 3 && args[0].equalsIgnoreCase("set")) {
			if(s instanceof Player) {
				Player p = (Player) s;
				if(!p.hasPermission("smp.trustlevel.modify")) {
					p.sendMessage(pr+ChatColor.RED+"Du hast nicht die ausreichende Rechte, um dies zu tun!");
					return true;
				}
			}
			int i = 0;
			try {
				i = Integer.parseInt(args[2]);
			}catch (Exception e) {
				s.sendMessage(pr+ChatColor.RED+"Du musst eine gültige Zahl angeben!");
				return true;
			}
			
			SMPPlayer sp = SMPPlayer.getPlayer(UUIDFetcher.getUUID(args[1]));
			if(i < 1 || i > SMPPlayer.maxTrustLevel) {
				s.sendMessage(pr+ChatColor.RED+"Ungültige Eingabe. Gültige TrustLevel: "+ChatColor.YELLOW+"1"+ChatColor.GRAY+"-"+ChatColor.YELLOW+SMPPlayer.maxTrustLevel);
			}else {
				sp.setTrustLevel(i);
				s.sendMessage(pr+"Der Spieler "+ChatColor.YELLOW+args[1]+ChatColor.GRAY+" hat nun das TrustLevel "+ChatColor.YELLOW+sp.getTrustLevel()+ChatColor.GRAY+"/"+ChatColor.YELLOW+SMPPlayer.maxTrustLevel+ChatColor.GRAY+".");
			}
		}else {
			if(s instanceof Player) {
				Player p = (Player) s;
				if(!p.hasPermission("smp.trustlevel.modify")) {
					s.sendMessage(pr+ChatColor.RED+"Syntax: "+ChatColor.AQUA+"/trustlevel");
					return true;
				}
			}
			s.sendMessage(pr+ChatColor.RED+"Syntax: "+ChatColor.AQUA+"/trustlevel <promote/demote/set> <Spieler> [Level]");
		}
		return true;
	}
	
	
	public static Boolean checkBlockBreakPlace(Player p, Material m) {
		SMPPlayer sp = SMPPlayer.getPlayer(p);
		if(sp.getTrustLevel() == 1) {
			Messages.trustLevelNoAccess(p);
			return false;
		}
		if(sp.getTrustLevel() == 2) {
			if(!SMPPlayer.isLVLmaxOnline()) {
				Messages.trustLevelOnlineNeeded(p);
				return false;
			}else {
				if(Main.dangerousBlocks.contains(m)) {
					Messages.trustLevelDangerousBlock(p);
					return false;
				}
			}
		}
		if(sp.getTrustLevel() == 3) {
			if(Main.dangerousBlocks.contains(m)) {
				Messages.trustLevelDangerousBlock(p);
				return false;
			}
		}
		if(sp.getTrustLevel() == 4) {
			if(!SMPPlayer.isLVLmaxOnline()) {
				if(Main.dangerousBlocks.contains(m)) {
					Messages.trustLevelOnlineNeeded(p);
					return false;
				}
			}
		}
		return true;
	}
}
