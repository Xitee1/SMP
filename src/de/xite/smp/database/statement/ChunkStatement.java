package de.xite.smp.database.statement;

import de.xite.smp.database.Database;
import org.bukkit.Chunk;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChunkStatement {
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
        } catch (SQLException e){
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
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
