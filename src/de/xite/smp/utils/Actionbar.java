package de.xite.smp.utils;

import java.util.HashMap;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.xite.smp.main.Main;

public class Actionbar {
	static Main pl = Main.pl;

	static HashMap<Player, Integer> counter = new HashMap<>();
	static HashMap<Player, String> message = new HashMap<>();

	public static void sendActionBar(Player p, String msg) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
		// TODO fix deprecation
	}

	public static void sendActionBar(Player p, String msg, int seconds) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
		message.put(p, msg);
		counter.put(p, seconds);
	}

	public static void removeActionbar(Player p, boolean forceRemoveText) {
		counter.remove(p);
		message.remove(p);
		if(forceRemoveText)
			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
	}

	public static void startActionbarService() {
		pl.getLogger().info("ActionBar manager started.");
		Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, () -> {
			for(Player p : counter.keySet()) {
				int count = counter.get(p) - 1;

				if(count > 0) {
					counter.replace(p, count);
					sendActionBar(p, message.get(p));
				}else {
					counter.remove(p);
					message.remove(p);
				}
			}
		}, 20, 20);
	}
}