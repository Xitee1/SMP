package de.xite.smp.config;

import org.bukkit.Location;

public class LocationsConfig extends BaseConfig {

    @Override
    public void init() {

    }

    @Override
    public String getConfigFileName() {
        return "locations.yaml";
    }

    /**
     * loads a location by name.
     * @param name the location name
     * @return the location
     */
    private Location getLocation(String name) {
        return getYamlConfiguration().getLocation("locations."+name);
    }

    /**
     * sets a location and saves it to the config.
     * @param name the name of the location
     * @param loc the location
     */
    private void setLocation(String name, Location loc) {
        getYamlConfiguration().set("locations."+name, loc);

        save();
    }


    /**
     * @return the spawn location
     */
    public Location getSpawnLocation() {
        return getLocation("spawn");
    }

    /**
     * Sets the spawn location
     * @param loc the location of the spawn
     */
    public void setSpawnLocation(Location loc) {
        setLocation("spawn", loc);
    }
}
