package com.applecraftserver.plugins.core.mplayer;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MPlayer implements Comparable<MPlayer> {
    private final Player player;
    private final OfflinePlayer offlinePlayer;
	private final UUID id;
	private final String name;
	private boolean afk, op;

	public MPlayer(Player p) {
        this(Bukkit.getOfflinePlayer(p.getName()));
    }

    public MPlayer(OfflinePlayer p) {
        this.player = p.isOnline() ? p.getPlayer() : null;
        this.offlinePlayer = p;
        this.id = p.getPlayer().getUniqueId();
		this.name = p.getName();
	}

	public boolean isOnline() {
        return this.offlinePlayer.isOnline();
    }

    public String getName() {
		return this.name;
	}

	public boolean isFirstTimePlaying() {
		return offlinePlayer.hasPlayedBefore();
	}

	public long getLastSeen() {
		return offlinePlayer.getLastPlayed();
	}

	public long getFirstJoined() {
		return offlinePlayer.getFirstPlayed();
	}

    protected UUID getUuid() {
        return offlinePlayer.getPlayer().getUniqueId();
    }

    @Override
    public int compareTo(MPlayer o) {
        return getName().compareTo(o.getName());
	}
}
