package com.applecraftserver.plugins.mrcore.user;

import com.applecraftserver.plugins.mrcore.api.ICore;
import com.applecraftserver.plugins.mrcore.economy.UserBank;
import org.bukkit.entity.Player;

import java.net.InetAddress;

/*
 * This, and frankly, most of the configuration, user handling, and cross-plugin talk is how Essentials works.
 * Credit to them for making something that works great in this situation/this purpose.
 */

public class User extends AbstractUserData {
    private UserBank bankAccount;
    private MPlayer base;
    private transient long lastlogin;
    private transient boolean afk = false;
    private long firstJoin;
    private InetAddress hostname;
    private String ip;

    public User(final Player player, final ICore core) {
        super(player, core);

        if (base.getPlayer().isOnline())
            lastlogin = System.currentTimeMillis();
        firstJoin = player.getFirstPlayed();
        hostname = player.getAddress().getAddress();
        ip = hostname.toString().replace("/", "");
    }

    public UserBank getBankAccount() {
        return (bankAccount != null) ? bankAccount : null;
    }

    public void setBankAccount(UserBank bankAccount) {
        this.bankAccount = bankAccount;
    }
}
