package com.applecraftserver.plugins.core.irc;

import com.applecraftserver.plugins.core.Core;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

import static com.applecraftserver.plugins.core.AbstractPlugin.$;

public class IRCBot extends ListenerAdapter {
	public PircBotX pbot;
	private Configuration.Builder config;

	public IRCBot() {
		config = new Configuration.Builder();
		config.setName(Core.instance.getConfig().getString("irc.nick"));
		config.setLogin(Core.instance.getConfig().getString("irc.user"));
		config.setServer(Core.instance.getConfig().getString("irc.server"), Core.instance.getConfig().getInt("irc.port"), Core.instance.getConfig().getString("irc.serverpass"));
		config.setNickservPassword(Core.instance.getConfig().getString("irc.nickservpass"));
		config.setAutoReconnect(Core.instance.getConfig().getBoolean("irc.autoreconnect"));
		config.addAutoJoinChannel(Core.instance.getConfig().getString("irc.staffchannel"));
		config.setVersion("Java IRC stuff");
		config.setCapEnabled(true);
		config.addListener(this);
		for (String s : Core.instance.getConfig().getStringList("irc.channels")) {
			config.addAutoJoinChannel(s);
		}
		pbot = new PircBotX(config.buildConfiguration());
		$("irc", "Connecting an IRC bot to " + Core.instance.getConfig().getString("irc.server"));
		try {
			pbot.startBot();
		} catch (Exception e) {
			$("irc", "Couldn't connect to the server:");
			e.printStackTrace();
		}
	}

}
