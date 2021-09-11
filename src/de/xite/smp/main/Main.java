package de.xite.smp.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.xite.smp.utils.Actionbar;
import de.xite.smp.verify.InteractListener;
import de.xite.smp.verify.VerifyCommand;

public class Main extends JavaPlugin{
	public static Main pl;
	
	public static List<String> verified = new ArrayList<>(); // Allowed players (Fully trusted, can do everything)
	public static List<String> trusted = new ArrayList<>(); // Trusted players (At least one verified player has to be online and block blacklist)
	
	public static List<Material> nonVerified = new ArrayList<>(); // Allowed blocks for non verified
	public static List<Material> trustedBlacklist = new ArrayList<>(); // Blacklisted blocks for trusted, but not verified players
	@Override
	public void onEnable() {
		pl = this;
		getCommand("verify").setExecutor(new VerifyCommand());
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new InteractListener(), this);
		pm.registerEvents(new JoinQuitListener(), this);
		
		pl.reloadConfig();
		pl.getConfig().options().copyDefaults(true);
		pl.saveDefaultConfig();
		
		// Load config
		verified = pl.getConfig().getStringList("allowed"); // Load verified players
		trusted = pl.getConfig().getStringList("allowed");
		
		for(String s : pl.getConfig().getStringList("nonVerified"))
			nonVerified.add(Material.getMaterial(s));
		for(String s : pl.getConfig().getStringList("trustedBlacklist"))
			trustedBlacklist.add(Material.getMaterial(s));
		
		Actionbar.start();
	}
}
