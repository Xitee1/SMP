package de.xite.smp.utils;

import de.xite.smp.main.Config;
import de.xite.smp.main.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class Locations {

    public static Location getLocation(String name) {
        FileConfiguration cfg =  Config.getLocationsConfig();

        String w = cfg.getString(name+".world");
        double x = cfg.getDouble(name+".x");
        double y = cfg.getDouble(name+".y");
        double z = cfg.getDouble(name+".z");
        float yaw = (float) cfg.getDouble(name+".yaw");
        float pitch = (float) cfg.getDouble(name+".pitch");

        if(w == null) {
            Logger.error("Could not find location in locations.yaml named '"+name+"'!");
            return null;
        }

        return new Location(Bukkit.getWorld(w), x, y, z, yaw, pitch);
    }

    public static void setLocation(Location loc, String name) {
        String w = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();

        FileConfiguration cfg =  Config.getLocationsConfig();

        cfg.set(name+".world", w);
        cfg.set(name+".x", x);
        cfg.set(name+".y", y);
        cfg.set(name+".z", z);
        cfg.set(name+".yaw", yaw);
        cfg.set(name+".pitch", pitch);

        Config.saveLocationsConfig();
    }
}
