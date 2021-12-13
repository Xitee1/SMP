package de.xite.smp.listener;

import de.xite.smp.commands.BlockInfoCommand;
import de.xite.smp.commands.ChunkInfoCommand;
import de.xite.smp.main.Main;
import de.xite.smp.sql.MySQL;
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
		if(!MySQL.isConnected()) {
			e.disallow(Result.KICK_OTHER, Component.text(ChatColor.RED+"Der Server konnte keine Verbindung zur Datenbank herstellen! Bitte versuche es später erneut."));
			return;
		}
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(!Main.verified.contains(p.getName()))
			p.setCollidable(false);
		//e.setJoinMessage(ChatColor.YELLOW + p.getName() + " hat das Spiel betreten.");
		e.joinMessage(Component.text(ChatColor.YELLOW + p.getName() + " hat das Spiel betreten."));
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		//e.setQuitMessage(ChatColor.YELLOW + p.getName() + " hat das Spiel verlassen.");
		e.quitMessage(Component.text(ChatColor.YELLOW + p.getName() + " hat das Spiel verlassen."));
		
		// Remove all cache
		if(BlockInfoCommand.fastLookupThrottle.containsKey(p))
			BlockInfoCommand.fastLookupThrottle.remove(p);
		if(ChunkInfoCommand.lastChunk.containsKey(p))
			ChunkInfoCommand.lastChunk.remove(p);
		if(ChunkInfoCommand.isLoading.containsKey(p))
			ChunkInfoCommand.isLoading.remove(p);
	}
}
