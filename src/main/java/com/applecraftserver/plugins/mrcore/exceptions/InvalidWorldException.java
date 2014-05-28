package com.applecraftserver.plugins.mrcore.exceptions;

public class InvalidWorldException extends Exception {
    private final String worldname;

    public InvalidWorldException(String invalidworld) {
        super("Invalid world name " + invalidworld);
        this.worldname = invalidworld;
    }

    public String getWorld() {
        return worldname;
    }
}
