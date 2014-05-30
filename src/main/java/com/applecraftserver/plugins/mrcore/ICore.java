package com.applecraftserver.plugins.mrcore;

import com.applecraftserver.plugins.mrcore.user.IUser;
import com.applecraftserver.plugins.mrcore.user.User;
import com.applecraftserver.plugins.mrcore.user.UserMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

/*
 * This, and frankly, most of the configuration, user handling, and cross-plugin talk is how Essentials works.
 * Credit to them for making something that works great in this situation/this purpose.
 */

public interface ICore extends Plugin {
    User getUser(UUID id);
    User getUser(String name);
    User getUser(Player player);

    UserMap getUsers();

    void broadcastMessage(String... msgs);
    void broadcastMessage(IUser sender, String... msgs);
    void broadcastMessage(String perm, String... msgs);

    BukkitTask runTask(boolean async, Runnable task);
    BukkitTask runTaskLater(boolean async, Runnable task, long timeout);
    BukkitTask runTaskLater(boolean async, Runnable task, long timeout, long repeat);

}
