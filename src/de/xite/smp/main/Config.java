package de.xite.smp.main;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    static Main pl = Main.pl;

    private static YamlConfiguration locationsConfig = null;

    public static void loadAllConfigs() {
        pl.reloadConfig();
        pl.getConfig().options().copyDefaults(true);
        pl.saveDefaultConfig();

        createLocationsConfig(true);
    }


    private static void createLocationsConfig(boolean loadConfig) {
        File f = new File(pl.getDataFolder(), "locations.yaml");
        if(!f.exists()) {
            try {
                f.createNewFile();

                /*
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);

                cfg.addDefault("key", "value");

                cfg.options().copyDefaults(true);
                cfg.save(f);
                */

            } catch (IOException e) {
                pl.getLogger().severe("Could not create locations config!");
                throw new RuntimeException(e);
            }
        }

        if(loadConfig)
            locationsConfig = YamlConfiguration.loadConfiguration(f);
    }

    public static YamlConfiguration getLocationsConfig() {
        if(locationsConfig == null) {
            throw new RuntimeException("Could not get locations config because it hasn't loaded (yet)!");
        }

        return locationsConfig;
    }

    public static void saveLocationsConfig() {
        File f = new File(pl.getDataFolder(), "locations.yaml");
        try {
            getLocationsConfig().save(f);
        } catch (IOException e) {
            pl.getLogger().severe("Could not save locations config!");
            throw new RuntimeException(e);
        }
    }

    public static FileConfiguration getDefaultConfig() {
        return pl.getConfig();
    }
}
