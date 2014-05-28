package com.applecraftserver.plugins.mrcore.user;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

/*
 * This, and frankly, most of the configuration, user handling, and cross-plugin talk is how Essentials works.
 * Credit to them for making something that works great in this situation/this purpose.
 */

public class MPlayer {
    protected Player player;

    public MPlayer(final Player player) {
        this.player = player;
    }

    public final Player getPlayer() {
        return this.player;
    }

    public final Player setPlayer(final Player newplayer) {
        return this.player = newplayer;
    }

    public Server getServer() {
        return getPlayer().getServer();
    }

    public World getWorld() {
        return getPlayer().getWorld();
    }

    public Location getLocation() {
        return getPlayer().getLocation();
    }

}
