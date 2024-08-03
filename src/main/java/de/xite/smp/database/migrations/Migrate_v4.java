package de.xite.smp.database.migrations;

import de.xite.smp.database.Database;

import java.sql.SQLException;
import java.sql.Statement;

import static de.xite.smp.main.Main.pl;

public class Migrate_v4 {
    public static void migrate() {
        try (Statement st = Database.getConnection().createStatement()) {
            // Step 1: Create a new table with the desired schema
            st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + Database.prefix + "chunks_new` " +
                    "(`world` VARCHAR(255) NOT NULL," +
                    "`loc_x` INT NOT NULL," +
                    "`loc_z` INT NOT NULL," +
                    "`version_created` VARCHAR(255) NOT NULL," +
                    "`date_created` VARCHAR(255) NOT NULL," +
                    "`version_modified` VARCHAR(255)," +
                    "`date_modified` VARCHAR(255)," +
                    "CONSTRAINT unique_chunk UNIQUE(world, loc_x, loc_z));");

            // Step 2: Copy data from the old table to the new table
            st.executeUpdate("DELETE FROM `"+Database.prefix+"chunks` WHERE ROWID IN (SELECT ROWID FROM `"+Database.prefix+"chunks` WHERE world || '_' || loc_x || '_' || loc_z IN (SELECT world || '_' || loc_x || '_' || loc_z FROM `"+Database.prefix+"chunks` GROUP BY world, loc_x, loc_z HAVING COUNT(*) > 1));");
            st.executeUpdate("INSERT INTO `" + Database.prefix + "chunks_new` " +
                    "SELECT `world`, `loc_x`, `loc_z`, `version_created`, `date_created`, `version_modified`, `date_modified` " +
                    "FROM `" + Database.prefix + "chunks`;");
            st.executeUpdate("UPDATE `" + Database.prefix + "chunks_new` SET `version_modified`=NULL WHERE `version_modified`='none';");
            st.executeUpdate("UPDATE `" + Database.prefix + "chunks_new` SET `date_modified`=NULL WHERE `date_modified`='none';");

            // Step 3: Drop the old table
            st.executeUpdate("DROP TABLE `" + Database.prefix + "chunks`;");

            // Step 4: Rename the new table to match the original table's name
            st.executeUpdate("ALTER TABLE `" + Database.prefix + "chunks_new` " +
                    "RENAME TO `" + Database.prefix + "chunks`;");

            pl.getLogger().info("Migrated database to v4.");
        } catch (SQLException e) {
            pl.getLogger().severe("Could not migrate database to v4!");
            throw new RuntimeException(e);
        }
    }
}
