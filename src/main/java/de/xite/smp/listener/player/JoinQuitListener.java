package de.xite.smp.listener.player;

import de.xite.smp.commands.BlockInfoCommand;
import de.xite.smp.commands.ChunkInfoCommand;
import de.xite.smp.discord.SMPcord;
import de.xite.smp.main.Main;
import de.xite.smp.database.Database;
import de.xite.smp.utils.Locations;
import de.xite.smp.entities.SMPPlayer;
import net.kyori.adventure.text.Component;

import java.sql.Timestamp;
import java.util.List;
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

		SMPPlayer smpp = SMPPlayer.getPlayer(p.getUniqueId());
		if(!Database.isConnected() || smpp.getDataLoadingFailed()) {
			e.disallow(Result.KICK_OTHER, Component.text(
					"Deine Spielerdaten konnten nicht geladen werden.\n" +
					"Bitte versuche es erneut. Sollte das Problem weiterhin\n" +
					"bestehen, melde dich bitte im Discord."
			));
		}

		if(p.isBanned() || (smpp.isBanned())) {
			String banReason = smpp.getBanReason();
			if(banReason == null) {
				banReason = "Unbekannt.";
			}
			e.disallow(Result.KICK_BANNED, Component.text(
							ChatColor.AQUA+"Du bist gebannt.\n" +
							ChatColor.RED+"Grund: "+ChatColor.AQUA+banReason));
		}
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		SMPPlayer smpp = SMPPlayer.getPlayer(uuid);

		// update SMPPlayer
		smpp.setName(p.getName());
		smpp.setLastJoined(new Timestamp(System.currentTimeMillis()));
		smpp.persist();

		// Join message
		e.joinMessage(Component.text(ChatColor.YELLOW + p.getName() + " hat das Spiel betreten."));
		SMPcord.sendChatMessage("**"+p.getName()+" hat das Spiel betreten.**");

		// Check if SMPPlayer does exist. If not, create the SMPPlayer and send welcome message.
		if(!p.hasPlayedBefore()) {
			Bukkit.getScheduler().runTaskLater(Main.pl, () -> {
				Location spawn = Locations.getLocation("spawn");
				if(spawn != null)
					p.teleport(spawn);

				Bukkit.getServer().broadcast(Component.text(ChatColor.GREEN+"Herzlich Willkommen, "+ChatColor.YELLOW+p.getName()+ChatColor.GREEN+"!"));
				SMPcord.sendChatMessage("**Herzlich Willkommen, "+p.getName()+"!**");
			}, 20);
		}

		// TrustLevel permissions
		int trustLevel = smpp.getTrustLevel();
		if(trustLevel == 1)
			p.setCollidable(false);

		PermissionAttachment pa = p.addAttachment(Main.pl);

		pa.setPermission("trustlevel.level."+trustLevel, true);

		List<String> perms = Main.pl.getConfig().getStringList("trustlevel."+trustLevel+".perms");
		for(String s : perms) {
			pa.setPermission(s, true);
			if(Main.debug)
				Main.pl.getLogger().info("Player "+p.getName()+" got the permission: "+s);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
		UUID uuid = p.getUniqueId();
		SMPPlayer smpp = SMPPlayer.getPlayer(uuid);

		// Quit message
		e.quitMessage(Component.text(ChatColor.YELLOW + p.getName() + " hat das Spiel verlassen."));
		SMPcord.sendChatMessage("**"+p.getName()+" hat das Spiel verlassen.**");

		// Remove player's cache
		BlockInfoCommand.fastLookupThrottle.remove(p);
		BlockInfoCommand.players.remove(p);
		ChunkInfoCommand.chunkInfoList.remove(p);

		// Save player data
		smpp.setLogoutLocation(loc);
		SMPPlayer.updatePlayTime(smpp, p);
		Bukkit.getScheduler().runTaskAsynchronously(Main.pl, () -> {
			smpp.persist();
			SMPPlayer.unloadSMPPlayer(uuid);
		});
	}
}
