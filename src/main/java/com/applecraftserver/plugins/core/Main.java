package com.applecraftserver.plugins.core;

import java.util.logging.Logger;

public class Main {
    private Main instance;
    private Logger logger = this.getLogger();

    public void onEnable() {
        instance = this;
    }
}
