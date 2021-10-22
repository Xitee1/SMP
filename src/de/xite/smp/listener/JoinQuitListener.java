package de.xite.smp.listener;

import de.xite.smp.commands.ChunkInfoCommand;
import de.xite.smp.main.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(!Main.verified.contains(p.getName()))
			p.setCollidable(false);
		e.setJoinMessage(ChatColor.YELLOW + p.getName() + " hat das Spiel betreten.");
		ChunkInfoCommand.sendChunkInfoToPlayer(p);
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		e.setQuitMessage(ChatColor.YELLOW + p.getName() + " hat das Spiel verlassen.");
	}
}
