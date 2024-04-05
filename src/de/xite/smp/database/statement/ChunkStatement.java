package de.xite.smp.database.statement;

import de.xite.smp.database.Database;
import de.xite.smp.main.Main;
import org.bukkit.Chunk;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChunkStatement {
    public static int chunkInsertPSBatches = 0;
    public static int chunkUpdatePSBatches = 0;

    /**
     * @param chunk the MC chunk
     * @param dateCreated the creation date as string
     * @param versionCreated the creation version as string
     */
    public static void insert(Chunk chunk, String dateCreated, String versionCreated) {
        PreparedStatement ps = Database.chunkInsertPS;
        try {
            ps.setString(1, chunk.getWorld().getName());
            ps.setInt(2, chunk.getX());
            ps.setInt(3, chunk.getZ());
            ps.setString(4, versionCreated);
            ps.setString(5, dateCreated);
            ps.addBatch();

            chunkInsertPSBatches += 1;
            if(chunkInsertPSBatches % 500 == 0) {
                ps.executeBatch();
                if(Main.debug) {
                    Main.pl.getLogger().info("Executed " + chunkInsertPSBatches + " batched chunk inserts.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param chunk the MC chunk
     * @param dateModified the modify date as string
     * @param versionModified the modify version as string
     */
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
            if (chunkUpdatePSBatches % 500 == 0) {
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
