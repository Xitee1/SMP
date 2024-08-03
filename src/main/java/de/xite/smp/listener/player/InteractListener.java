package de.xite.smp.listener.player;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.xite.smp.commands.BlockInfoCommand;
import de.xite.smp.commands.TrustLevelCommand;
import de.xite.smp.main.Main;
import de.xite.smp.main.Messages;
import de.xite.smp.entities.SMPPlayer;
import de.xite.smp.utils.TimeUtils;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.CoreProtectAPI.ParseResult;
import net.md_5.bungee.api.ChatColor;

public class InteractListener implements Listener {
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		if(BlockInfoCommand.players.contains(p)) {
			e.setCancelled(true);
			sendBlockInfo(p, e);
			return;
		}
		
		Block b = e.getClickedBlock();
		if(b != null) {
			Material m = b.getType();
			if(!isPlayerAllowedToInteract(p, m)) {
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		Material m = b.getType();

		if(!isPlayerAllowedToInteract(p, m)) {
			e.setCancelled(true);
			return;
		}
	}
	
	private Boolean isPlayerAllowedToInteract(Player p, Material m) {
		/*
		 *  Check if the player is allowed to interact with the Material.
		 *  If it is in the allowed TL 1 list, return true. Block break / place will be checked later.
		 */
		if(Main.interactAllowedTrustLevel1.contains(m)) {
			return true;
		}
		
		// If the Material is not in the list, check what rights he has
		SMPPlayer smpp = SMPPlayer.getPlayer(p.getUniqueId());
		
		if(smpp.getTrustLevel() == 1) {
			Messages.trustLevelNoAccess(p);
			return false;
		}

		if(smpp.getTrustLevel() == 2) {
			if(!SMPPlayer.isLVLmaxOnline()) {
				Messages.trustLevelOnlineNeeded(p);
				return false;
			}

			//Main.pl.getLogger().warning("Material interact: "+m.getItemTranslationKey());
			if(Main.dangerousBlocks.contains(m)) {
				Messages.trustLevelDangerousBlock(p);
				sendDangerousInteract(p, m);
				return false;
			}
		}

		if(smpp.getTrustLevel() == 3) {
			if(Main.dangerousBlocks.contains(m)) {
				Messages.trustLevelDangerousBlock(p);
				sendDangerousInteract(p, m);
				return false;
			}
		}

		if(smpp.getTrustLevel() == 4) {
			if(!SMPPlayer.isLVLmaxOnline()) {
				if(Main.dangerousBlocks.contains(m)) {
					Messages.trustLevelOnlineNeeded(p);
					return false;
				}
			}
		}

		return true;
	}
	
	private void sendDangerousInteract(Player p, Material m) {
		Messages.broadcastToMaxTrustLevelPlayers(TrustLevelCommand.pr+ChatColor.RED+"Der Spieler "+ChatColor.YELLOW+p.getName()+ChatColor.RED+" hat versucht, mit "
				+ChatColor.DARK_AQUA+m+ChatColor.RED+" zu interagieren!");
	}
	
	private static void sendBlockInfo(Player p, PlayerInteractEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.pl, () -> {
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
						String block = ChatColor.AQUA+"x"+b.getX()+"/y"+b.getY()+"/z"+b.getZ()+ChatColor.GRAY;

						if(lookup.size() == 0) {
							p.sendMessage(BlockInfoCommand.pr+ChatColor.RED+"Keine Daten für den Block "+block+" gefunden.");
						}else {
							int i = 1;

							p.sendMessage(BlockInfoCommand.pr+ChatColor.GOLD+"-------------------------------");
							p.sendMessage(BlockInfoCommand.pr+ChatColor.GOLD+"Info für den Block "+block+":");

							for(String[] result : lookup) {
								if(i <= 10) {
									ParseResult parseResult = api.parseResult(result);
									if(!parseResult.isRolledBack()) {
										int actionID = parseResult.getActionId();
										Material m = parseResult.getType();
										String player = parseResult.getPlayer();
										long resultTime = parseResult.getTimestamp();
										long time = System.currentTimeMillis() / 1000L;

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
				//BlockInfoCommand.fastLookupThrottle.put(p, System.currentTimeMillis());
			}
		});
	}
}
