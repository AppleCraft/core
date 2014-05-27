package com.applecraftserver.plugins.mrcore;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MRCore extends JavaPlugin {
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

}
