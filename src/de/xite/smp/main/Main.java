package de.xite.smp.main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.xite.smp.utils.Actionbar;

public class Main extends JavaPlugin{
	public static Main pl;
	
	public static ArrayList<String> verified = new ArrayList<>();//Allowed players
	public static ArrayList<String> trusted = new ArrayList<>();//Allowed players
	
	public static ArrayList<Material> allowedBlocks = new ArrayList<>();// Allowed blocks for non verified
	public static ArrayList<Material> allowedBlocksTrust = new ArrayList<>();// Allowed blocks for trust level 1
	@Override
	public void onEnable() {
		pl = this;
		getCommand("verify").setExecutor(new VerifyCommand());
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new InteractListener(), this);
		
		pl.reloadConfig();
		pl.getConfig().options().copyDefaults(true);
		pl.saveDefaultConfig();
		
		
		//Add allowed Blocks for non-verified players
		allowedBlocks.add(Material.ACACIA_PRESSURE_PLATE);
		allowedBlocks.add(Material.BIRCH_PRESSURE_PLATE);
		allowedBlocks.add(Material.CRIMSON_PRESSURE_PLATE);
		allowedBlocks.add(Material.DARK_OAK_PRESSURE_PLATE);
		allowedBlocks.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
		allowedBlocks.add(Material.JUNGLE_PRESSURE_PLATE);
		allowedBlocks.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
		allowedBlocks.add(Material.OAK_PRESSURE_PLATE);
		allowedBlocks.add(Material.POLISHED_BLACKSTONE_PRESSURE_PLATE);
		allowedBlocks.add(Material.SPRUCE_PRESSURE_PLATE);
		allowedBlocks.add(Material.STONE_PRESSURE_PLATE);
		allowedBlocks.add(Material.WARPED_PRESSURE_PLATE);
		
		allowedBlocks.add(Material.CHEST);
		allowedBlocks.add(Material.HOPPER);
		allowedBlocks.add(Material.CHEST_MINECART);
		allowedBlocks.add(Material.HOPPER_MINECART);
		allowedBlocks.add(Material.FURNACE_MINECART);
		
		allowedBlocks.add(Material.ACACIA_DOOR);
		allowedBlocks.add(Material.BIRCH_DOOR);
		allowedBlocks.add(Material.CRIMSON_DOOR);
		allowedBlocks.add(Material.DARK_OAK_DOOR);
		allowedBlocks.add(Material.JUNGLE_DOOR);
		allowedBlocks.add(Material.OAK_DOOR);
		allowedBlocks.add(Material.SPRUCE_DOOR);
		allowedBlocks.add(Material.WARPED_DOOR);
		
		//Add not allowed blocks for trust 1
		allowedBlocksTrust.add(Material.LAVA_BUCKET);
		allowedBlocksTrust.add(Material.FLINT_AND_STEEL);
		
		verified.addAll(pl.getConfig().getStringList("allowed"));
		
		Actionbar.start();
	}
}
