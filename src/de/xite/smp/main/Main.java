package de.xite.smp.main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.xite.smp.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.xite.smp.discord.DiscordChatListener;
import de.xite.smp.discord.SMPcord;
import de.xite.smp.listener.entity.EntityDamageListener;
import de.xite.smp.listener.entity.EntityMountListener;
import de.xite.smp.listener.entity.EntitySpawnListener;
import de.xite.smp.listener.entity.EntityTargetListener;
import de.xite.smp.listener.player.BlockBreakPlaceListener;
import de.xite.smp.listener.player.FoodChangeListener;
import de.xite.smp.listener.player.InteractListener;
import de.xite.smp.listener.player.InventoryListener;
import de.xite.smp.listener.player.JoinQuitListener;
import de.xite.smp.listener.player.MoveListener;
import de.xite.smp.listener.world.ChunkListener;
import de.xite.smp.database.Database;
import de.xite.smp.utils.Actionbar;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin{
	public static Main pl;
	public static String MCVersion;
	
	public static List<Material> interactAllowedTrustLevel1 = new ArrayList<>(); // Allowed blocks for TrustLevel 1
	public static List<Material> dangerousBlocks = new ArrayList<>(); // Disallowed blocks for TrustLevel online needed
	
	public static String hexColorBegin = "#", hexColorEnd = ""; // hex color Syntax

	public static Boolean debug = false;

	@Override
	public void onEnable() {
		pl = this;

		// Set the current detected MC version
		String vstring = Bukkit.getBukkitVersion();
		MCVersion = vstring.substring(0, vstring.lastIndexOf("-R")).replace("_", ".");

		// Load config and services
		Config.loadAllConfigs();

		debug = pl.getConfig().getBoolean("debug");

		for(String s : pl.getConfig().getStringList("interactAllowedTrustLevel1"))
			interactAllowedTrustLevel1.add(Material.getMaterial(s));

		for(String s : pl.getConfig().getStringList("dangerousBlocks"))
			dangerousBlocks.add(Material.getMaterial(s));

		// Connect to MySQl database
		Database.connect();
		
		// Start up the Discord bot (if enabled)
		if(getConfig().getBoolean("discord.enabled"))
			Bukkit.getScheduler().runTaskAsynchronously(pl, SMPcord::connectDiscord);

		// Start other needed services/schedulers
		Actionbar.startActionbarService();

		// --- Register Commands --- //
		
		// TrustLevel
		getCommand("trustlevel").setExecutor(new TrustLevelCommand());
		getCommand("trustlevel").setTabCompleter(new TrustLevelCommand());
		getCommand("tl").setExecutor(new TrustLevelCommand());
		getCommand("tl").setTabCompleter(new TrustLevelCommand());

		// ChunkInfo
		getCommand("chunkinfo").setExecutor(new ChunkInfoCommand());
		getCommand("ci").setExecutor(new ChunkInfoCommand());
		
		// Blockinfo
		getCommand("blockinfo").setExecutor(new BlockInfoCommand());
		getCommand("bi").setExecutor(new BlockInfoCommand());
		
		// Spielzeit
		getCommand("spielzeit").setExecutor(new PlayTimeCommand());
		getCommand("playtime").setExecutor(new PlayTimeCommand());
		getCommand("sz").setExecutor(new PlayTimeCommand());
		getCommand("pt").setExecutor(new PlayTimeCommand());
		
		// Hilfe
		getCommand("hilfe").setExecutor(new HelpCommand());
		getCommand("help").setExecutor(new HelpCommand());

		// Location manager
		getCommand("setlocation").setExecutor(new SetLocationCommand());
		
		// --- Register Events --- //
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.registerEvents(new BlockBreakPlaceListener(), this);
		pm.registerEvents(new ChunkListener(), this);
		pm.registerEvents(new EntityDamageListener(), this);
		pm.registerEvents(new EntityMountListener(), this);
		pm.registerEvents(new EntityTargetListener(), this);
		pm.registerEvents(new EntitySpawnListener(), this);
		pm.registerEvents(new FoodChangeListener(), this);
		pm.registerEvents(new InteractListener(), this);
		pm.registerEvents(new InventoryListener(), this);
		pm.registerEvents(new JoinQuitListener(), this);
		pm.registerEvents(new MoveListener(), this);
		//pm.registerEvents(new SpartanAnticheat(), this);
		pm.registerEvents(new DiscordChatListener(), this);
	}

	@Override
	public void onDisable() {
		Database.executeAllBatches();
		Database.disconnect();

		// Prevent the hoster from deleting the logs
		pl.getLogger().info("Moving logs..");
		File destinationFolder = new File("server_logs");
		File originalLogFile = new File("logs/latest.log");
		if(!destinationFolder.exists())
			destinationFolder.mkdir();
		if(originalLogFile.exists()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd__HH_mm");
			try {
				Files.copy(originalLogFile, new File(destinationFolder.getAbsolutePath()+"/"+sdf.format(new Date())+".log"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		pl.getLogger().info("Logs moved!");
	}

	public static CoreProtectAPI getCoreProtect() {
		Plugin plugin = pl.getServer().getPluginManager().getPlugin("CoreProtect");
		// Check that CoreProtect is loaded
		if(!(plugin instanceof CoreProtect)) {
			return null;
		}
		// Check that the API is enabled
		CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
		if(!CoreProtect.isEnabled()) {
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
