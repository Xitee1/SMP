package de.xite.smp.main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.xite.smp.commands.BlockInfoCommand;
import de.xite.smp.commands.ChunkInfoCommand;
import de.xite.smp.commands.VerifyCommand;
import de.xite.smp.listener.ChunkListener;
import de.xite.smp.listener.InteractListener;
import de.xite.smp.listener.JoinQuitListener;
import de.xite.smp.listener.MoveListener;
import de.xite.smp.sql.MySQL;
import de.xite.smp.utils.Actionbar;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin{
	public static Main pl;
	public static String MCVersion;
	
	public static List<String> verified = new ArrayList<>(); // Allowed players (Fully trusted, can do everything)
	public static List<String> trusted = new ArrayList<>(); // Trusted players (At least one verified player has to be online and block blacklist)
	
	public static List<Material> nonVerified = new ArrayList<>(); // Allowed blocks for non verified
	public static List<Material> trustedBlacklist = new ArrayList<>(); // Blacklisted blocks for trusted, but not verified players
	
	public static String hexColorBegin = "#", hexColorEnd = ""; // hex color Syntax
	@Override
	public void onEnable() {
		pl = this;

		// Load config and services
		pl.reloadConfig();
		pl.getConfig().options().copyDefaults(true);
		pl.saveDefaultConfig();
		Actionbar.start();
		ChunkInfoCommand.chunkInfoUpdater();
		MySQL.connect();
		
		String vstring = Bukkit.getBukkitVersion();
		MCVersion = vstring.substring(0, vstring.lastIndexOf("-R")).replace("_", ".");
		
		// Register commands and listener
		getCommand("verify").setExecutor(new VerifyCommand());
		getCommand("chunkinfo").setExecutor(new ChunkInfoCommand());
		getCommand("ci").setExecutor(new ChunkInfoCommand());
		getCommand("blockinfo").setExecutor(new BlockInfoCommand());
		getCommand("bi").setExecutor(new BlockInfoCommand());
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new InteractListener(), this);
		pm.registerEvents(new JoinQuitListener(), this);
		pm.registerEvents(new ChunkListener(), this);
		pm.registerEvents(new MoveListener(), this);
		
		// Load config
		verified = pl.getConfig().getStringList("allowed"); // Load verified players
		trusted = pl.getConfig().getStringList("allowed");
		
		for(String s : pl.getConfig().getStringList("nonVerified"))
			nonVerified.add(Material.getMaterial(s));
		for(String s : pl.getConfig().getStringList("trustedBlacklist"))
			trustedBlacklist.add(Material.getMaterial(s));

	}
	@Override
	public void onDisable() {
		MySQL.executeAllWaitingUpdates();
	}
	public static CoreProtectAPI getCoreProtect() {
		Plugin plugin = pl.getServer().getPluginManager().getPlugin("CoreProtect");
		// Check that CoreProtect is loaded
		if(plugin == null || !(plugin instanceof CoreProtect)) {
			return null;
		}
		// Check that the API is enabled
		CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
		if(CoreProtect.isEnabled() == false) {
			return null;
		}
		// Check that a compatible version of the API is loaded
		if(CoreProtect.APIVersion() < 7) {
			return null;
		}
		return CoreProtect;
	}
	
    public final static char COLOR_CHAR = ChatColor.COLOR_CHAR;
    public static String translateHexColor(String message) {
    	try {
            final Pattern hexPattern = Pattern.compile(hexColorBegin + "([A-Fa-f0-9]{6})" + hexColorEnd);
            Matcher matcher = hexPattern.matcher(message);
            StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
            while (matcher.find()) {
                String group = matcher.group(1);
                matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                        + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                        + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                        + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
                        );
            }
            return matcher.appendTail(buffer).toString();
    	}catch (Exception e) {
    		pl.getLogger().severe("You have an invalid HEX-Color-Code! Please check the syntax! String: "+message);
    		return "InvalidHexColor";
		}
    }
}
