package de.xite.smp.listener;

import de.xite.smp.commands.BlockInfoCommand;
import de.xite.smp.commands.ChunkInfoCommand;
import de.xite.smp.sql.MySQL;
import de.xite.smp.utils.SMPPlayer;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		if(!MySQL.isConnected()) {
			e.disallow(Result.KICK_OTHER, Component.text(ChatColor.RED+"Der Server konnte keine Verbindung zur Datenbank herstellen! Bitte versuche es später erneut."));
			return;
		}
		
		SMPPlayer sp = SMPPlayer.getPlayer(p);
		if(sp.isBanned() || p.isBanned()) {
			e.disallow(Result.KICK_OTHER, Component.text(
							ChatColor.AQUA+"Du bist hier nicht mehr erwünscht.\n" +
							ChatColor.GRAY+"Generell nehmen wir keine Entbannungsanträge an.\n" +
							ChatColor.GRAY+"Wenn du aber einen guten Grund hast, kannst du es ja mal mit einer Anfrage in unserem Discord versuchen.\n\n" +
							ChatColor.RED+"Bangrund: "+ChatColor.AQUA+sp.getBanReason()));
			return;
		}
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(SMPPlayer.getPlayer(p).getTrustLevel() == 0)
			p.setCollidable(false);
		e.joinMessage(Component.text(ChatColor.YELLOW + p.getName() + " hat das Spiel betreten."));
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		e.quitMessage(Component.text(ChatColor.YELLOW + p.getName() + " hat das Spiel verlassen."));
		
		SMPPlayer sp = SMPPlayer.getPlayer(p);
		sp.setLastJoined();
		sp.savePlayTime();
		sp.setLogoutLocation(p.getLocation());
		sp.remove();
		
		// Remove all cache
		if(BlockInfoCommand.fastLookupThrottle.containsKey(p))
			BlockInfoCommand.fastLookupThrottle.remove(p);
		if(ChunkInfoCommand.lastChunk.containsKey(p))
			ChunkInfoCommand.lastChunk.remove(p);
		if(ChunkInfoCommand.isLoading.containsKey(p))
			ChunkInfoCommand.isLoading.remove(p);
	}
}
