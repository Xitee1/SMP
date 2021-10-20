package de.xite.smp.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.xite.smp.commands.ChunkInfoCommand;
import de.xite.smp.commands.VerifyCommand;
import de.xite.smp.listener.ChunkListener;
import de.xite.smp.listener.InteractListener;
import de.xite.smp.listener.MoveListener;
import de.xite.smp.utils.Actionbar;
import de.xite.smp.utils.MySQL;

public class Main extends JavaPlugin{
	public static Main pl;
	public static String MCVersion;
	  
	public static List<String> verified = new ArrayList<>(); // Allowed players (Fully trusted, can do everything)
	public static List<String> trusted = new ArrayList<>(); // Trusted players (At least one verified player has to be online and block blacklist)
	
	public static List<Material> nonVerified = new ArrayList<>(); // Allowed blocks for non verified
	public static List<Material> trustedBlacklist = new ArrayList<>(); // Blacklisted blocks for trusted, but not verified players
	@Override
	public void onEnable() {
	    pl = this;

	    // Load config and services
	    pl.reloadConfig();
	    pl.getConfig().options().copyDefaults(true);
	    pl.saveDefaultConfig();
	    Actionbar.start();
	    MySQL.connect();
	    
	    String vstring = Bukkit.getBukkitVersion();
	    MCVersion = vstring.substring(0, vstring.lastIndexOf("-R")).replace("_", ".");

	    // Register commands and listener
	    getCommand("verify").setExecutor(new VerifyCommand());
	    getCommand("chunkinfo").setExecutor(new ChunkInfoCommand());
	    getCommand("ci").setExecutor(new ChunkInfoCommand());
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
}
