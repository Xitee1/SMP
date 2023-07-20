package de.xite.smp.commands;

import de.xite.smp.main.Messages;
import de.xite.smp.utils.Locations;
import de.xite.smp.utils.SMPPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetLocationCommand implements CommandExecutor {
    public static final String prefix = ChatColor.GRAY+"["+ChatColor.RED+"LocationManager"+ChatColor.GRAY+"] ";

    @Override
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args) {
        if(!(s instanceof Player)) {
            s.sendMessage(Component.text("Nur Spieler können diesen Befehl ausführen!").color(TextColor.color(255, 0 ,0)));
            return true;
        }

        Player p = (Player) s;

        SMPPlayer smpp = SMPPlayer.getPlayer(p.getUniqueId());
        if(smpp.getTrustLevel() != SMPPlayer.maxTrustLevel) {
            s.sendMessage(Messages.generalNoPermission(prefix));
            return true;
        }

        if(args.length == 1) {
            Location loc = p.getLocation();
            String name = args[0].toLowerCase();

            Locations.setLocation(loc, name);
            s.sendMessage(Component.text(prefix+ChatColor.GREEN+"Die Location "+ChatColor.AQUA+name+ChatColor.GREEN+" wurde gesetzt."));

            return true;
        }

        s.sendMessage(Messages.commandSyntax(cmd, prefix, "<Name>"));

        return true;
    }
}
