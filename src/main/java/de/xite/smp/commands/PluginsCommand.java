package de.xite.smp.commands;

import de.xite.smp.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PluginsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        cs.sendMessage("§7Folgende Plugins sind auf dem Server installiert:");

        for (Map<?, ?> plugin : Main.getPluginConfig().getPluginDescriptions()) {
            String pluginName = (String) plugin.get("name");
            String pluginDescription = (String) plugin.get("description");
            cs.sendMessage("§7- §8"+pluginName+": §7"+pluginDescription);
        }
        return true;
    }
}
