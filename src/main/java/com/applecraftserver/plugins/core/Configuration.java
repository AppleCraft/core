package com.applecraftserver.plugins.core;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class Configuration {
    private final Main instance;

    public static boolean debug;
    public static String pcolor, scolor, ncolor;

    public Configuration(Main pl) {
        this.instance = pl;
        File file = new File(instance.getDataFolder(), "config.yml");
        if (!file.exists()) {
            file.mkdirs();
            instance.saveDefaultConfig();
        }
        initialize();
    }

    private void initialize() {
        instance.reloadConfig();
        final FileConfiguration c = instance.getConfig();
        debug = c.getBoolean("debug", false);
        pcolor = c.getString("chat.primary_color", "YELLOW");
        scolor = c.getString("chat.secondary_color", "DARK_GREEN");
        ncolor = c.getString("chat.negative_color", "MAGENTA");
    }

    private ChatColor parseColor(String c) {
        ChatColor color;
        try {
           color = ChatColor.valueOf(c);
        } catch (Exception e) {
            color = ChatColor.WHITE;
        }
        return color;
    }
}
