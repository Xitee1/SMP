package de.xite.smp.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

import de.xite.smp.utils.Actionbar;
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
			if(!Main.allowedBlocks.contains(e.getClickedBlock().getType())) {
				e.setCancelled(true);
				sendErrorMessage(p);
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
