package de.xite.smp.database.migrations;

import de.xite.smp.database.Database;

import java.sql.SQLException;
import java.sql.Statement;

import static de.xite.smp.main.Main.pl;

public class Migrate_v3 {
    public static void migrate() {
        try (Statement st = Database.getConnection().createStatement()) {
            // Step 1: Create a new table with the desired schema
            st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + Database.prefix + "players_new` " +
                    "(`uuid` VARCHAR(50) NOT NULL UNIQUE," +
                    "`name` VARCHAR(16) NOT NULL," +
                    "`trustlevel` INT NOT NULL," +
                    "`firstJoined` DATETIME," +
                    "`lastJoined` DATETIME," +
                    "`logoutLocation` VARCHAR(255)," +
                    "`playTime` INT NOT NULL," +
                    "`banReason` VARCHAR(255));");

            // Step 2: Copy data from the old table to the new table
            st.executeUpdate("INSERT INTO `" + Database.prefix + "players_new` " +
                    "SELECT `uuid`, `name`, `trustlevel`, `firstJoined`, `lastJoined`, `logoutLocation`, `playTime`, `banReason` " +
                    "FROM `" + Database.prefix + "players`;");
            st.executeUpdate("UPDATE `" + Database.prefix + "players_new` SET banReason=NULL WHERE banReason=`none`;");

            // Step 3: Drop the old table
            st.executeUpdate("DROP TABLE `" + Database.prefix + "players`;");

            // Step 4: Rename the new table to match the original table's name
            st.executeUpdate("ALTER TABLE `" + Database.prefix + "players_new` " +
                    "RENAME TO `" + Database.prefix + "players`;");

            pl.getLogger().info("Migrated database to v3.");
        } catch (SQLException e) {
            pl.getLogger().severe("Could not migrate database to v3!");
            throw new RuntimeException(e);
        }
    }
}
