package com.applecraftserver.plugins.core.permissions;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by aeperiox on 2/7/14.
 */
public class PermissionHolder {
    protected final UUID uuid;
    public String name;
    public PermissionMeta meta;
    public ArrayList<String> temporaryPermissions = new ArrayList<String>();

    protected PermissionHolder(UUID uuid, String name, PermissionMeta meta) {
        this.uuid = uuid;
        this.name = name;
        this.meta = meta;
        init();
    }

    public boolean hasPermission(String permission) {

    }

    private void init() {

    }

    protected class PermissionMeta {
        private String prefix, suffix;
        private boolean prefixOverrides, suffixOverrides;

        private PermissionMeta(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.prefixOverrides = false;
            this.suffixOverrides = false;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setPrefixOverrides(boolean prefixOverrides) {
            this.prefixOverrides = prefixOverrides;
        }

        public void setSuffixOverrides(boolean suffixOverrides) {
            this.suffixOverrides = suffixOverrides;
        }

        public boolean doesPrefixOverride() {
            return prefixOverrides;
        }

        public boolean doesSuffixOverrides() {
            return suffixOverrides;
        }
    }
}
