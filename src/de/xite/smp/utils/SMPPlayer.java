package de.xite.smp.utils;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.xite.smp.main.Main;
import de.xite.smp.database.Database;

import static de.xite.smp.main.Main.pl;

public class SMPPlayer {
    public static HashMap<UUID, SMPPlayer> players = new HashMap<>();
    public static int maxTrustLevel = 6;

    String name, banReason;
    UUID uuid;
    int trustlevel = 1;
    long playTime = 0;
    Timestamp firstJoined = null, lastJoined = null;
    Location logoutLocaction;
	boolean dataLoadingFailed = false;

    private SMPPlayer(UUID uuid) {
        this.uuid = uuid;
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM `" + Database.prefix + "players` WHERE `uuid`='" + uuid + "'");
            if (rs.next()) {
                this.name = rs.getString("name");
                this.trustlevel = rs.getInt("trustLevel");
                this.firstJoined = rs.getTimestamp("firstJoined");
                this.lastJoined = rs.getTimestamp("lastJoined");
                this.playTime = rs.getInt("playtime");
                this.banReason = rs.getString("banReason");
                String[] location = rs.getString("logoutLocation").split("%");
                this.logoutLocaction = new Location(Bukkit.getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]),
                        Float.parseFloat(location[4]), Float.parseFloat(location[5]));
            }
            rs.close();
        } catch (SQLException e1) {
			this.dataLoadingFailed = true;
            pl.getLogger().severe("Could not load player with UUID " + uuid + "!");
            e1.printStackTrace();
        }
    }

    public static SMPPlayer getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            SMPPlayer smpPlayer = new SMPPlayer(uuid);
            players.put(uuid, smpPlayer);
        }

        return players.get(uuid);
    }

    public static void unloadSMPPlayer(UUID uuid) {
        players.remove(uuid);
    }

    public static UUID nameToUUID(String name) {
        String uuid = Database.getString("players", "uuid", "`name`='" + name + "'");
        if (uuid == null)
            return null;
        return UUID.fromString(uuid);
    }

    // ------------- //
    // General utils //
    // ------------- //
    public static Boolean isLVLmaxOnline() {
        for (Player all : Bukkit.getOnlinePlayers())
            if (SMPPlayer.getPlayer(all.getUniqueId()).getTrustLevel() == maxTrustLevel)
                return true;
        return false;
    }


    // ---------------- //
    // SMPPlayer object //
    // ---------------- //
    public UUID getUUID() {
        return this.uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    // Player name
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Trust Level
    public Integer getTrustLevel() {
        return this.trustlevel;
    }

    public void setTrustLevel(int level) {
        this.trustlevel = level;
    }

    // First joined
    public Timestamp getFirstJoined() {
        return this.firstJoined;
    }

    public void setFirstJoined(Timestamp date) {
        this.firstJoined = date;
    }

    // Last joined
    public Timestamp getLastJoined() {
        return this.lastJoined;
    }

    public void setLastJoined(Timestamp date) {
        this.lastJoined = date;
    }

    // Location
    public Location getLogoutLocation() {
        return this.logoutLocaction;
    }

    public void setLogoutLocation(Location loc) {
        this.logoutLocaction = loc;
    }

    // Playtime
    public long getPlayTime() {
        return this.playTime;
    }

    public void setPlayTime(long millis) {
        this.playTime = millis;
    }

    public void countPlayTime() {
        this.playTime += 1;
    }

    // Ban
    public String getBanReason() {
        return this.banReason;
    }

    public Boolean isBanned() {
        return this.getBanReason() != null;
    }

    public void setBanReason(String reason) {
        this.banReason = reason;
    }

	public boolean getDataLoadingFailed() {
		return this.dataLoadingFailed;
	}

    public void persist() {
        if (uuid == null)
            return;

        final String table = Database.prefix + "players";

        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            try {
                PreparedStatement st = Database.getConnection().prepareStatement(
                        "INSERT INTO `" + table + "`" +
                            "(uuid, name, trustlevel, firstJoined, lastJoined, logoutLocation, playTime, banReason)" +
                            "VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, ?, NULL)" +
                            "ON CONFLICT(uuid) DO UPDATE SET" +
                            "`name` = ?," +
                            "`trustlevel` = ?," +
                            "`firstJoined` = ?," +
                            "`lastJoined` = ?," +
                            "`logoutLocation` = ?," +
                            "`playTime` = ?," +
                            "`banReason` = ?" +
                            "WHERE uuid=?"
                );
                // insert
                st.setString(1, this.getUUID().toString());
                st.setString(2, this.getName());
                st.setInt(3, this.getTrustLevel());
                st.setLong(4, this.getPlayTime());

                // update
                Location loc = this.getLogoutLocation();
                String locString = null;
                if (loc != null) {
                    locString = loc.getWorld().getName() +
                            "%" + loc.getX() +
                            "%" + loc.getY() +
                            "%" + loc.getZ() +
                            "%" + loc.getYaw() +
                            "%" + loc.getPitch();
                }
                st.setString(5, this.getName());
                st.setInt(6, this.getTrustLevel());
                st.setTimestamp(7, this.getFirstJoined());
                st.setTimestamp(8, this.getLastJoined());
                st.setString(9, locString);
                st.setLong(10, this.getPlayTime());
                st.setString(11, this.getBanReason());
                st.setString(12, this.getUUID().toString());

                st.executeUpdate();
                st.close();

                if(Main.debug) {
                    pl.getLogger().info("Persisted SMPPlayer "+this.getName());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
