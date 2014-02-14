package com.applecraftserver.plugins.core.permissions;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by aeperiox on 2/7/14.
 */
public abstract class PermissionHolder {
	protected final UUID uuid;
	protected final String name;
	protected String prefix, suffix;
	protected ArrayList<String> permissions = new ArrayList();

	protected PermissionHolder(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		init();
	}

	private void init() {

	}

}
