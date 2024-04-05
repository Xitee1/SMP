package de.xite.smp.utils;

import de.xite.smp.main.Main;
import de.xite.smp.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.sql.*;

public class ChunkInfo {
    private static Main pl = Main.pl;
    private static final String separator = ChatColor.DARK_GRAY+" | ";
    private static final String prefix = ChatColor.GREEN+"ChunkInfo"+separator;
    private static final int actionbarTimeout = 60*5; // 5 minutes

    private PreparedStatement sqlStatement;
    private Player p;
    private int playerLocationLastChunkX;
    private int playerLocationLastChunkZ;
    private String lastChunkInfoMessage;
    private BukkitTask sendChunkInfoToPlayerScheduler;
    private boolean loadingChunkInfo = false;

    public ChunkInfo(Player p) {
        this.p = p;
    }

    public void startSendingChunkInfoToPlayer() {
        sendChunkInfoToPlayerScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(pl, () -> {
            if(!loadingChunkInfo)
                sendChunkInfoToPlayer();
        }, 0, 20);
    }

    public void stopSendingChunkInfoToPlayer() {
        sendChunkInfoToPlayerScheduler.cancel();
        Actionbar.removeActionbar(p, true);
        try {
            sqlStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            sqlStatement = null;
        }
    }

    public void sendChunkInfoToPlayer() {
        Chunk chunk = p.getLocation().getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        if(chunkX != playerLocationLastChunkX || chunkZ != playerLocationLastChunkZ) {
            Actionbar.sendActionBar(p, prefix+ChatColor.GOLD+"Wird geladen..", actionbarTimeout);
            lastChunkInfoMessage = getChunkInfoText(chunk);
            Actionbar.removeActionbar(p, false);
        }

        Actionbar.sendActionBar(p, lastChunkInfoMessage);

        playerLocationLastChunkX = chunkX;
        playerLocationLastChunkZ = chunkZ;
    }

    public String getChunkInfoText(Chunk chunk) {
        if(loadingChunkInfo)
            return null;
        this.loadingChunkInfo = true;

        String message;

        try (ResultSet rs = getChunkData(chunk)) {
            String creationDate = rs.getString("date_created");
            // If date_created there is no information about that chunk
            if(creationDate == null)
                return prefix + ChatColor.RED+"Zu diesem Chunk wurden keine Informationen gespeichert.";
            String creationVersion = rs.getString("version_created");
            String modifiedDate = rs.getString("date_modified");
            String modifiedVersion = rs.getString("version_modified");

            message =
                    prefix+
                    ChatColor.GRAY+"Erstellt: "+ ChatColor.AQUA+creationDate+ // creation date
                    ChatColor.GRAY+" ("+ChatColor.AQUA+creationVersion+ChatColor.GRAY+")"+ // creation version
                    separator;

            if(modifiedDate == null) {
                message += ChatColor.GRAY+"Originalzustand";
            }else {
                message +=
                                ChatColor.GRAY+"Bearbeitet: "+ChatColor.AQUA+modifiedDate+
                                ChatColor.GRAY+" ("+ChatColor.AQUA+modifiedVersion+ChatColor.GRAY+")";
            }
        } catch (SQLException e) {
            pl.getLogger().severe("Could not generate chunk info text. SQL query failed or data is invalid.");
            e.printStackTrace();
            return prefix + ChatColor.RED+"Informationen konnten nicht abgerufen werden. Bitte Admin kontaktieren.";
        } finally {
            this.loadingChunkInfo = false;
        }

        return message;
    }

    private ResultSet getChunkData(Chunk chunk) {
        try {
            PreparedStatement ps = getStatement();
            ps.setString(1, chunk.getWorld().getName());
            ps.setInt(2, chunk.getX());
            ps.setInt(3, chunk.getZ());
            return ps.executeQuery();
        } catch (SQLException e) {
            pl.getLogger().severe("Could not get chunk data.");
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement getStatement() {
        if(sqlStatement == null) {
            Connection c = Database.getConnection();
            try {
                assert c != null : "SQL connection is null";
                sqlStatement = c.prepareStatement("SELECT `date_created`,`version_created`,`date_modified`,`version_modified`" +
                        "FROM `"+ Database.prefix+"chunks` WHERE `world`=? AND `loc_x`=? AND `loc_z`=?");
            } catch (SQLException e) {
                pl.getLogger().severe("Could not create SQL statement for ChunkInfo.");
                throw new RuntimeException(e);
            }
        }

        return sqlStatement;
    }
}
