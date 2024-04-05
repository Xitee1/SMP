package de.xite.smp.database.statement;

import de.xite.smp.database.Database;
import de.xite.smp.main.Main;
import org.bukkit.Chunk;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChunkStatement {
    public static int chunkInsertPSBatches = 0;
    public static int chunkUpdatePSBatches = 0;

    public static void insert(Chunk chunk, String dateCreated, String versionCreated) {
        PreparedStatement ps = Database.chunkInsertPS;
        try {
            ps.setString(1, chunk.getWorld().getName());
            ps.setInt(2, chunk.getX());
            ps.setInt(3, chunk.getZ());
            ps.setString(4, versionCreated);
            ps.setString(5, dateCreated);
            ps.setString(6, "none");
            ps.setString(7, "none");
            ps.addBatch();

            chunkInsertPSBatches += 1;
            if(chunkInsertPSBatches % 1000 == 0) {
                ps.executeBatch();
                if(Main.debug) {
                    Main.pl.getLogger().info("Executed " + chunkInsertPSBatches + " batched chunk inserts.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void update(Chunk chunk, String dateModified, String versionModified) {
        PreparedStatement ps = Database.chunkUpdatePS;
        try {
            ps.setString(1, versionModified);
            ps.setString(2, dateModified);
            ps.setString(3, chunk.getWorld().getName());
            ps.setInt(4, chunk.getX());
            ps.setInt(5, chunk.getZ());
            ps.addBatch();

            chunkUpdatePSBatches += 1;
            if (chunkUpdatePSBatches % 1000 == 0) {
                ps.executeBatch();
                if(Main.debug) {
                    Main.pl.getLogger().info("Executed " + chunkUpdatePSBatches + " batched chunk updates.");
                }
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
