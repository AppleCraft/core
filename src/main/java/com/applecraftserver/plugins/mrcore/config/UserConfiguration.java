package com.applecraftserver.plugins.mrcore.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

/*
 * This, and frankly, most of the configuration, user handling, and cross-plugin talk is how Essentials works.
 * Credit to them for making something that works great in this situation/this purpose.
 */

public class UserConfiguration extends MConfiguration {
    final String username;
    final UUID id;

    public UserConfiguration(final String name, final UUID id, final File conf) {
        super(conf);
        this.username = name;
        this.id = id;
    }

    private File getAltFile() {
        final UUID fn = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username.toLowerCase(Locale.ENGLISH)).getBytes(Charsets.UTF_8));
        return new File(configFile.getParentFile(), fn.toString() + ".yml");
    }

    @Override
    public boolean altFileExists() {
        if (username.equals(username.toLowerCase())) {
            return false;
        }
        return getAltFile().exists();
    }

    @Override
    public void convertAltFile() {
        try {
            Files.move(getAltFile(), new File(configFile.getParentFile(), id + ".yml"));
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to migrate user: " + username, ex);
        }
    }



}
