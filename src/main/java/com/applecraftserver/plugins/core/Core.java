package com.applecraftserver.plugins.core;

import com.applecraftserver.plugins.core.irc.IRCBot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin implements Listener, Plugin {

	public static Core instance;
	public static final Logger logger = Logger.getLogger("Minecraft");
	public Connection connection;
	MySQLConnectionPool pool;
	public Permissions permissions;
	public IRCBot ircbot;

	@Override
	public void onLoad() {
		instance = this;

		FileConfigurationOptions options = getConfig().options();
		options.copyDefaults(true);
		options.copyHeader(true);
		saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);

		try {
			pool = new MySQLConnectionPool("localhost", "root", "password", "server");
			connection = pool.getConnection();

			PreparedStatement testStatement = connection.prepareStatement("SELECT COUNT(id) FROM players");
			ResultSet testResultSet = testStatement.executeQuery();
			if (testResultSet.next()) {
				IPlugin.$("core", "Database successfully linked!");
				IPlugin.$("core", "Database loaded with " + testResultSet.getInt(1) + " player data rows");
			}

			permissions = new Permissions(this);

		} catch (Exception ex) {
			logger.severe("JDBC not found! Something's seriously screwed up");
			ex.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
            this.setEnabled(false);
			return;
		}

		try {
			ircbot = new IRCBot();
			IPlugin.$("core", "IRC bot initialized!");
		} catch (Exception e) {
			logger.severe("Couldn't start IRC Bot!");
			e.printStackTrace();
			ircbot = null;
		}

		logger.info("Initialized core");
	}

	public Connection getConnection() throws SQLException {
		return pool.getConnection();
	}

	@EventHandler
	public void nonModulePlugins(PluginEnableEvent e) {
		Class clazz = e.getPlugin().getClass();
		if (!Arrays.asList(clazz.getClasses()).contains(IPlugin.class)) {
			logger.warning("Non-modular plugin " + e.getPlugin().getName() + " found!");
		}
	}
}
