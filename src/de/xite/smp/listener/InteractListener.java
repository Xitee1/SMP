package de.xite.smp.listener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import de.xite.smp.commands.BlockInfoCommand;
import de.xite.smp.main.Main;
import de.xite.smp.utils.Actionbar;
import de.xite.smp.utils.MySQL;
import de.xite.smp.utils.TimeUtils;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.CoreProtectAPI.ParseResult;
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
			}
		}else if(!BlockInfoCommand.players.contains(p)) {
			Chunk chunk = p.getLocation().getChunk();
			Bukkit.getScheduler().runTaskAsynchronously(Main.pl, new Runnable() {
				@Override
				public void run() {
					if(chunk != null) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
						MySQL.update("UPDATE `" + MySQL.prefix + "chunks` SET `version_modified`='" + Main.MCVersion + "', `date_modified`='" + sdf.format(new Date()) + "' " + 
								"WHERE `loc_x`='" + chunk.getX() + "' AND `loc_z`='" + chunk.getZ() + "';");
			        }
				}
			});
		}
		if(BlockInfoCommand.players.contains(p)) {
			e.setCancelled(true);
			Bukkit.getScheduler().runTaskAsynchronously(Main.pl, new Runnable() {
				@Override
				public void run() {
					if(BlockInfoCommand.fastLookupThrottle.containsKey(p)) {
						if((System.currentTimeMillis() - BlockInfoCommand.fastLookupThrottle.get(p)) < 500) {
							p.sendMessage(BlockInfoCommand.pr+ChatColor.RED+"Bitte etwas langsamer!");
							return;
						}
					}
					if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
						BlockInfoCommand.fastLookupThrottle.put(p, System.currentTimeMillis());
						CoreProtectAPI api = Main.getCoreProtect();
						if(api != null) {
							Block b = e.getClickedBlock();
							List<String[]> lookup = api.blockLookup(b, -1);
							if(lookup != null) {
								if(lookup.size() == 0) {
									p.sendMessage(BlockInfoCommand.pr+ChatColor.RED+"Keine Daten für den Block "+ChatColor.AQUA+b.getType().name()+ChatColor.RED+" gefunden.");
								}else {
									p.sendMessage(BlockInfoCommand.pr+ChatColor.GOLD+"-------------------------------");
									p.sendMessage(BlockInfoCommand.pr+ChatColor.GOLD+"Info für den Block "+ChatColor.AQUA+"x"+b.getX()+"/y"+b.getY()+"/"+b.getZ()+ChatColor.GRAY+":");
									int i = 1;
									for(String[] result : lookup) {
										if(i <= 10) {
											ParseResult parseResult = api.parseResult(result);
											if(!parseResult.isRolledBack()) {
												int actionID = parseResult.getActionId();
												Material m = parseResult.getType();
												String player = parseResult.getPlayer();
												int resultTime = parseResult.getTime();
												int time = (int) (System.currentTimeMillis() / 1000L);
												
												/*if(p.isOp()) {
													if(player.equals("#tnt") || player.equals("#fire")) {
														MySQL.update("DELETE FROM `co_block` WHERE `x`='"+b.getX()+"' AND `y`='"+b.getY()+"' AND `z`='"+b.getZ()+"' AND `user`='20';");
														MySQL.update("DELETE FROM `co_block` WHERE `x`='"+b.getX()+"' AND `y`='"+b.getY()+"' AND `z`='"+b.getZ()+"' AND `user`='87';");
														p.sendMessage(player+" aus Datenbank entfernt.");
													}
												}*/
												
												if(actionID == 0) {
													// Block removed
													p.sendMessage(Main.translateHexColor(BlockInfoCommand.pr+ChatColor.GRAY+"vor "+TimeUtils.getTimeSince(resultTime, time, false) +" "+ChatColor.DARK_GRAY+"- "
															+ChatColor.DARK_AQUA+player+ChatColor.GRAY+" hat #00B6B6"+m.name()+"#CB2100 entfernt."));
												}
												if(actionID == 1) {
													// Block placed
													p.sendMessage(Main.translateHexColor(BlockInfoCommand.pr+ChatColor.GRAY+"vor "+TimeUtils.getTimeSince(resultTime, time, false) +" "+ChatColor.DARK_GRAY+"- "
															+ChatColor.DARK_AQUA+player+ChatColor.GRAY+" hat #00B6B6"+m.name()+"#00AA16 platziert."));
												}
											}
											i++;
										}
									}
									p.sendMessage(BlockInfoCommand.pr+ChatColor.GOLD+"-------------------------------");
								}
							}
						}
					}
					if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						BlockInfoCommand.fastLookupThrottle.put(p, System.currentTimeMillis());
					}
				}
			});
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
