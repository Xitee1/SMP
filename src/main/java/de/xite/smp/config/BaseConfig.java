package de.xite.smp.config;

import de.xite.smp.main.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class BaseConfig {
    private YamlConfiguration yamlConfiguration;

    protected abstract void init();
    protected abstract String getConfigFileName();

    public void load() {
        Main.pl.saveResource(getConfigFileName(), false);

        yamlConfiguration = YamlConfiguration.loadConfiguration(new File(Main.pl.getDataFolder(), getConfigFileName()));

        init();
    }

    protected void save() {
        File f = new File(Main.pl.getDataFolder(), getConfigFileName());
        try {
            getYamlConfiguration().save(f);
        } catch (IOException e) {
            Main.pl.getLogger().severe("Could not save locations config!");
            throw new RuntimeException(e);
        }
    }

    protected YamlConfiguration getYamlConfiguration() {
        return yamlConfiguration;
    }
}
