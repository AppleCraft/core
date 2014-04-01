package com.applecraftserver.plugins.core.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

/**
 * Created by aprx on 2/1/14.
 */
public abstract class ModeratorAction extends Event {
    protected OfflinePlayer reciever;
    protected CommandSender sender;
    protected long stamp = System.currentTimeMillis();
    protected String information;

    public CommandSender getSender() {
        return sender;
    }

    public OfflinePlayer getReciever() {
        return reciever;
    }

    public long getStamp() {
        return stamp;
    }

    public String getInformation() {
        return information;
    }
}
