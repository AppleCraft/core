package com.applecraftserver.plugins.mrcore.economy;

import com.applecraftserver.plugins.mrcore.user.User;

public class UserBank extends AbstractBank {
    private final User user;

    public UserBank(final User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
