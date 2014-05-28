package com.applecraftserver.plugins.mrcore.user;

import com.applecraftserver.plugins.mrcore.commands.ICmd;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

/*
 * This, and frankly, most of the configuration, user handling, and cross-plugin talk is how Essentials works.
 * Credit to them for making something that works great in this situation/this purpose.
 */

public interface IUser {

    boolean authorized(String thing);
    boolean authorized(ICmd cmd);

    public String getName();
    Player getPlayer();

    boolean isAfk();
    String getAfkReason();

    long getLastActivity();

    void setConfigProperty(String node, Object object);
    Set<String> getConfigKeys();
    Map<String, Object> getConfigMap();
    Map<String, Object> getConfigMap(String node);

}
