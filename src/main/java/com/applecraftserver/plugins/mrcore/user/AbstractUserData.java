package com.applecraftserver.plugins.mrcore.user;

import com.applecraftserver.plugins.mrcore.api.ICore;
import com.applecraftserver.plugins.mrcore.config.IConfig;
import com.applecraftserver.plugins.mrcore.config.UserConfiguration;
import com.applecraftserver.plugins.mrcore.economy.AbstractBank;
import org.bukkit.entity.Player;

import java.io.File;

public abstract class AbstractUserData extends MPlayer implements IConfig {
    protected final transient ICore core;
    private final UserConfiguration configuration;
    private final File folder;

    protected AbstractUserData(Player base, ICore core) {
        super(base);
        this.core = core;
        folder = new File(core.getDataFolder(), "userdata");
        if (!folder.exists())
            folder.mkdirs();

        String filename;
        filename = base.getUniqueId().toString();

        configuration = new UserConfiguration(base.getName(), base.getUniqueId(), new File(folder, filename + ".yml"));
        reloadConfig();
    }

    public final void nuke() {
        configuration.forceSave();
        configuration.getFile().delete();
        // TODO: ess.getUserMap().removeUser(this.getBase().getName());
    }

    @Override
    public final void reloadConfig() {
        configuration.load();

        isAfk = retrieveIsAfk();
        afkReason = retrieveAfkReason();

        lastActivity = retrieveLastActivity();

    }

    private boolean isAfk;
    private String afkReason;

    public boolean retrieveIsAfk() {
        return configuration.getBoolean("afk.is", false);
    }
    public String retrieveAfkReason() {
        return configuration.getString("afk.reason", "");
    }

    private long lastActivity;

    private long retrieveLastActivity() {
        return configuration.getLong("last.activity");
    }
}
