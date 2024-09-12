package de.xite.smp.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginConfig extends BaseConfig {
    private Map<String, String> pluginDescriptions;

    @Override
    public void init() {
        // load plugin descriptions
        pluginDescriptions = new HashMap<>();

//        for (Map<?, ?> pluginMap : getYamlConfiguration().getMapList("plugins")) {
//            for (Map.Entry<?, ?> entry : pluginMap.entrySet()) {
//                String pluginName = (String) entry.getKey();
//                Map<?, ?> pluginData = (Map<?, ?>) entry.getValue();
//                String description = (String) pluginData.get("description");
//                pluginDescriptions.put(pluginName, description);
//            }
//        }
    }

    @Override
    public String getConfigFileName() {
        return "config.yaml";
    }

    //-----------//
    //--- SQL ---//
    //-----------//
    public String getSqlType() {
        return getYamlConfiguration().getString("sql.type");
    }

    public String getSqlTablePrefix() {
        return getYamlConfiguration().getString("sql.table-prefix");
    }

    public String getSqlitePath() {
        return getYamlConfiguration().getString("sql.sqlite.path");
    }

    public String getMysqlHost() {
        return getYamlConfiguration().getString("sql.mysql.host");
    }

    public int getMysqlPort() {
        return getYamlConfiguration().getInt("sql.mysql.port");
    }

    public String getMysqlUser() {
        return getYamlConfiguration().getString("sql.mysql.user");
    }

    public String getMysqlPassword() {
        return getYamlConfiguration().getString("sql.mysql.password");
    }

    public String getMysqlDatabase() {
        return getYamlConfiguration().getString("sql.mysql.database");
    }

    public boolean useSslForMysqlConnection() {
        return getYamlConfiguration().getBoolean("sql.mysql.useSSL");
    }


    //---------------//
    //--- Discord ---//
    //---------------//

    /**
     * @return true if the Discord API has been enabled.
     */
    public boolean isDiscordEnabled() {
        return getYamlConfiguration().getBoolean("discord", false);
    }

    /**
     * @return the discord API token
     */
    public String getDiscordToken() {
        return getYamlConfiguration().getString("discord.token");
    }

    /**
     * @return the channel that should be used for syncing the minecraft chat.
     */
    public String getDiscordChatChannel() {
        return getYamlConfiguration().getString("discord.chatChannel");
    }


    //------------------//
    //--- TrustLevel ---//
    //------------------//

    /**
     *
     * @param trustlevel the TrustLevel to get the permissions for
     * @return a list of permissions that users which the specified TrustLevel should get
     */
    public List<String> getTrustLevelPermissions(int trustlevel) {
        return getYamlConfiguration().getStringList("trustlevel."+trustlevel+".perms");
    }

    /**
     * @return a list with all blocks that TL 1 has access to.
     */
    public List<String> getTrustLevelBaseInteractBlocks() {
        return getYamlConfiguration().getStringList("interactAllowedTrustLevel1");
    }

    /**
     * @return a list of blocks that are dangerous (e.g. lava, tnt, ..)
     */
    public List<String> getDangerousBlocks() {
        return getYamlConfiguration().getStringList("dangerousBlocks");
    }

    //---------------------//
    //--- Miscellaneous ---//
    //---------------------//
    public List<Map<?, ?>> getPluginDescriptions() {
        return getYamlConfiguration().getMapList("plugins");
    }


    //-------------//
    //--- Debug ---//
    //-------------//

    public boolean isDebugEnabled() {
        return getYamlConfiguration().getBoolean("debug");
    }


    //-----------------------//
    //--- DatabaseVersion ---//
    //-----------------------//

    public int getDatabaseVersion() {
        return getYamlConfiguration().getInt("databaseVersion");
    }

    public void setDatabaseVersion(int databaseVersion) {
        getYamlConfiguration().set("databaseVersion", databaseVersion);
        save();
    }
}
