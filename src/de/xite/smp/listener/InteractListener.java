package de.xite.smp.listener;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import de.xite.smp.main.Main;
import de.xite.smp.utils.Actionbar;
import de.xite.smp.utils.MySQL;
import net.md_5.bungee.api.ChatColor;

public class InteractListener implements Listener{
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(!Main.verified.contains(p.getName())) {
			e.setCancelled(true);
			sendErrorMessage(p);
		}
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(!Main.verified.contains(p.getName())) {
			e.setCancelled(true);
			sendErrorMessage(p);
		}
	}
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if(!Main.verified.contains(p.getName())) {
				e.setCancelled(true);
				sendErrorMessage(p);
			}
		}
	}
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(!Main.verified.contains(p.getName())) {
			if(e.getClickedBlock() != null && !Main.nonVerified.contains(e.getClickedBlock().getType())) {
				e.setCancelled(true);
				sendErrorMessage(p);
			}else {
				Chunk chunk = p.getLocation().getChunk();
				if(chunk != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
					MySQL.update("UPDATE UPDATE `" + MySQL.prefix + "chunks` SET `version_modified`='" + Main.MCVersion + "', `modified_date`='" + sdf.format(new Date()) + "' " + 
							"WHERE `loc_x`='" + chunk.getX() + "' AND `loc_z`='" + chunk.getZ() + "';");
		        } 
			}  
		}
	}
	@EventHandler
	public void onRide(EntityMountEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(!Main.verified.contains(p.getName())) {
				if(e.getMount().getType() == EntityType.LLAMA ||
					e.getMount().getType() == EntityType.HORSE ||
					e.getMount().getType() == EntityType.SKELETON_HORSE ||
					e.getMount().getType() == EntityType.DONKEY ||
					e.getMount().getType() == EntityType.STRIDER ||
					e.getMount().getType() == EntityType.PIG) {
					
					e.setCancelled(true);
					sendErrorMessage(p);
				}
			}	
		}
	}
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(!Main.verified.contains(p.getName())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInventoryInteract(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(!Main.verified.contains(p.getName())) {
				if(e.getClickedInventory().getType() == InventoryType.CHEST) {
					e.setCancelled(true);
					sendErrorMessage(p);
				}
			}
		}
	}
	
	@EventHandler
	public void onMob(EntityTargetLivingEntityEvent e) {
		if(e.getTarget() instanceof Player) {
			Player p = (Player) e.getTarget();
			if(!Main.verified.contains(p.getName())) {
				e.setCancelled(true);
			}
		}
	}
	
	public void sendErrorMessage(Player p) {
		Actionbar.sendActionBar(p, ChatColor.RED+"Dein Account wurde noch nicht verifiziert! "+ChatColor.AQUA+"/verify "+ChatColor.RED+"für mehr infos.", 5);
	}
}
