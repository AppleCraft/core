package com.applecraftserver.plugins.core;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@SuppressWarnings("unused")
public class Main extends JavaPlugin {
    private Main instance;
    private Logger logger = this.getLogger();
    private PluginDescriptionFile description;

    public void onEnable() {
        description = this.getDescription();
        instance = this;
        _("Fully enabled v" + description.getVersion());
    }

    public void _(String msg) {
        logger.info(msg);
    }
}
