package com.applecraftserver.plugins.mrcore;

import com.applecraftserver.plugins.mrcore.user.IUser;
import com.applecraftserver.plugins.mrcore.user.User;
import com.applecraftserver.plugins.mrcore.user.UserMap;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class MRCore extends JavaPlugin implements com.applecraftserver.plugins.mrcore.api.ICore {
    private Permission perms;
    private Chat chat;
    private Economy economy;

    @Override
    public void onEnable() {
        Plugin vault = getServer().getPluginManager().getPlugin("Vault");
        if (vault == null || !vault.isEnabled()) {
            getLogger().severe("Vault seems to not be enabled. Welp. That's a problem. Shutting down...");
            this.setEnabled(false);
            return;
        }

        if (!setupChat()) getLogger().info("Couldn't set up Vault chat!");
        if (!setupEconomy()) getLogger().info("Couldn't set up Vault economy!");
        if (!setupPermissions()) getLogger().info("Couldn't set up Vault permissions!");

    }

    // Generic Vault methods
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            perms = permissionProvider.getProvider();
        }
        return (perms != null);
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }


    @Override
    public User getUser(UUID id) {
        return null;
    }

    @Override
    public User getUser(String name) {
        return null;
    }

    @Override
    public User getUser(Player player) {
        return null;
    }

    @Override
    public UserMap getUsers() {
        return null;
    }

    @Override
    public void broadcastMessage(String... msgs) {

    }

    @Override
    public void broadcastMessage(IUser sender, String... msgs) {

    }

    @Override
    public void broadcastMessage(String perm, String... msgs) {

    }

    @Override
    public BukkitTask runTask(boolean async, Runnable task) {
        if (async) {
            return Bukkit.getScheduler().runTaskAsynchronously(this, task);
        }
        return Bukkit.getScheduler().runTask(this, task);
    }

    @Override
    public BukkitTask runTaskLater(boolean async, Runnable task, long timeout) {
        return null;
    }

    @Override
    public BukkitTask runTaskLater(boolean async, Runnable task, long timeout, long repeat) {
        return null;
    }
}
