package com.applecraftserver.plugins.core.event;

import com.applecraftserver.plugins.core.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Created by aprx on 2/1/14.
 */
public abstract class CoreBanEvent extends ModeratorAction {
    protected final OfflinePlayer banned;
    protected final CommandSender banner;
    protected final long stamp;
    protected String information;

    public CoreBanEvent(CommandSender banner, String cmd) {
        this.banner = banner;

        cmd = cmd.trim();

        String[] splitcmd = cmd.split("\\s");

        this.banned = Bukkit.getOfflinePlayer(splitcmd[0]); //TODO mplayer

        this.stamp = System.currentTimeMillis();

        this.information = StringUtils.toProperCase(StringUtils.getJoinedArgsAfter(Arrays.asList(splitcmd), 0));
    }

    @Override
    public OfflinePlayer getReciever() {
        return this.banned;
    }

    @Override
    public CommandSender getSender() {
        return this.banner;
    }

    @Override
    public String getInformation() {
        return information;
    }

    @Override
    public long getStamp() {
        return stamp;
    }
}
