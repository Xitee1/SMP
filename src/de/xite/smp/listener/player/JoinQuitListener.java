package de.xite.smp.listener.player;

import de.xite.smp.commands.BlockInfoCommand;
import de.xite.smp.commands.ChunkInfoCommand;
import de.xite.smp.discord.SMPcord;
import de.xite.smp.main.Main;
import de.xite.smp.sql.MySQL;
import de.xite.smp.utils.SMPPlayer;
import net.kyori.adventure.text.Component;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

public class JoinQuitListener implements Listener {
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		if(!MySQL.isConnected()) {
			e.disallow(Result.KICK_OTHER, Component.text(ChatColor.RED+"Der Server konnte keine Verbindung zur Datenbank herstellen! Bitte kontaktiere einen Admin (Discord) und versuche es später erneut."));
			return;
		}
		
		SMPPlayer smpp = SMPPlayer.getPlayer(p.getUniqueId());
		if(p.isBanned() || (smpp != null && smpp.isBanned())) {
			String banReason = "Unbekannt.";
			if(smpp != null)
				banReason = smpp.getBanReason();
			e.disallow(Result.KICK_OTHER, Component.text(
							ChatColor.AQUA+"Du bist hier nicht mehr erwünscht.\n" +
							ChatColor.GRAY+"Generell nehmen wir keine Entbannungsanträge an.\n" +
							ChatColor.GRAY+"Wenn du aber einen guten Grund hast, kannst du es ja mal mit einer Anfrage in unserem Discord versuchen.\n\n" +
							ChatColor.RED+"Grund: "+ChatColor.AQUA+banReason));
		}
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		SMPPlayer smpp = SMPPlayer.getPlayer(p.getUniqueId());

		// Join message
		e.joinMessage(Component.text(ChatColor.YELLOW + p.getName() + " hat das Spiel betreten."));
		SMPcord.sendChatMessage("_**"+p.getName()+" hat das Spiel betreten.**_");

		// Check if SMPPlayer does exist. If not, create the SMPPlayer and send welcome message.
		if(smpp == null) {
			smpp = SMPPlayer.create(uuid);
			if(smpp != null) {
				Bukkit.getScheduler().runTaskLater(Main.pl, () -> {
					Bukkit.getServer().broadcast(Component.text(ChatColor.GREEN+"Herzlich Willkommen, "+ChatColor.YELLOW+p.getName()+ChatColor.GREEN+"!"));
				}, 20);
			}else {
				p.kick(Component.text("Leider gab es ein Datenbank-Problem. Benachrichtige bitte einen Admin im Discord und versuche es später erneut."));
				Main.pl.getLogger().severe("Player joined but does not exist in database!");
				return;
			}
		}

		// Update current player name (in case the player changed it)
		smpp.setName(p.getName());

		// TrustLevel permissions
		int trustLevel = smpp.getTrustLevel();
		if(trustLevel == 1)
			p.setCollidable(false);

		PermissionAttachment perms = p.addAttachment(Main.pl);
		perms.setPermission("trustlevel.level."+trustLevel, true);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
		UUID uuid = p.getUniqueId();

		// Quit message
		e.quitMessage(Component.text(ChatColor.YELLOW + p.getName() + " hat das Spiel verlassen."));
		SMPcord.sendChatMessage("_**"+p.getName()+" hat das Spiel verlassen.**_");

		// Remove player's cache
		BlockInfoCommand.fastLookupThrottle.remove(p);
		ChunkInfoCommand.lastChunk.remove(p);
		ChunkInfoCommand.isLoading.remove(p);

		// Save player data
		Bukkit.getScheduler().runTaskAsynchronously(Main.pl, () -> {
			SMPPlayer sp = SMPPlayer.getPlayer(uuid);
			if(sp != null) {
				// sp.setLastJoined();
				sp.savePlayTime();
				sp.setLogoutLocation(loc);

				SMPPlayer.unloadSMPPlayer(uuid);
			}else {
				Main.pl.getLogger().severe("SMPPlayer "+p.getName()+" is null and could not be saved!");
			}

		});
	}
}
