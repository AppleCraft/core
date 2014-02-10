package com.applecraftserver.plugins.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractPlugin extends JavaPlugin {

	@Override
	public abstract void onLoad();

	public static void $(String prefix, String msg, Object... args) {
		Logger log = Logger.getLogger("Minecraft");
		log.log(Level.INFO, " -{0}- {1}", new Object[]{prefix, String.format(msg, args)});
	}
}
