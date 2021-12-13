package de.xite.smp.utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.xite.smp.main.Main;
import net.kyori.adventure.text.Component;

public class Actionbar {
	static Main pl = Main.pl;
	
    static HashMap<Player, Integer> counter = new HashMap<Player, Integer>();
    static HashMap<Player, String> message = new HashMap<Player, String>();
    
    public static void sendActionBar(Player p, String msg) {
    	p.sendActionBar(Component.text(msg));
    }
    public static void sendActionBar(Player p, String msg, int seconds) {
    	p.sendActionBar(Component.text(msg));
    	message.put(p, msg);
    	counter.put(p, seconds);
    }
    public static void removeActionBar(Player p) {
    	if(counter.containsKey(p))
    		counter.remove(p);
    	if(message.containsKey(p))
    		message.remove(p);
    }
    public static void start() {
    	pl.getLogger().info("ActionBar manager started!");
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
			@Override
			public void run() {
				for(Player p : counter.keySet()) {
					int count = counter.get(p);
					count--;
					if(count != 0) {
						counter.replace(p, count);
						sendActionBar(p, message.get(p));
					}else {
						counter.remove(p);
						message.remove(p);
					}
				}
			}
		}, 20, 20);
    }
}
