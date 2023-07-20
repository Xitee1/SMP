package de.xite.smp.main;

public class Logger {
    public static void error(String msg) {
        Main.pl.getLogger().severe(msg);
    }

    public static void warning(String msg) {
        Main.pl.getLogger().warning(msg);
    }

    public static void info(String msg) {
        Main.pl.getLogger().info(msg);
    }
}
