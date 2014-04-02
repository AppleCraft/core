package com.applecraftserver.plugins.core;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin {
    private Logger logger = this.getLogger();
    private PluginDescriptionFile description;
    private Configuration config;

    public void onEnable() {
        description = this.getDescription();
        config = new Configuration(this);
        __("Configuration initialized");
        _("Fully enabled v" + description.getVersion());
    }

    public void _(String msg) {
        logger.info(msg);
    }

    public void __(String msg) {
        if (Configuration.debug)
            logger.fine(msg);
    }
}
