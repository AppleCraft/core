package com.applecraftserver.plugins.core;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class IPlugin extends JavaPlugin {

    @Override
    public abstract void onLoad();

    public static void $(String prefix, String msg, Object... args) {
        Logger log = Logger.getLogger("Minecraft");
        log.log(Level.INFO, " -{0}- {1}", new Object[]{prefix, String.format(msg, args)});
    }
}
