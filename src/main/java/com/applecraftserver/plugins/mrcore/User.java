package com.applecraftserver.plugins.mrcore;

import com.applecraftserver.plugins.mrcore.economy.UserAccount;
import org.bukkit.entity.Player;

/*
 * This, and frankly, most of the configuration, user handling, and cross-plugin talk is how Essentials works.
 * Credit to them for making something that works great in this situation/this purpose.
 */

public class User {
    private UserAccount bankAccount;
    private MPlayer base;

    public User(final Player player) {
        this.base = new MPlayer(player);
    }

    public UserAccount getBankAccount() {
        return (bankAccount != null) ? bankAccount : null;
    }

    public void setBankAccount(UserAccount bankAccount) {
        this.bankAccount = bankAccount;
    }
}
