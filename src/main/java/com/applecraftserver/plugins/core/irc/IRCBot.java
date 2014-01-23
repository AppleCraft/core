package com.applecraftserver.plugins.core.irc;

import com.applecraftserver.plugins.core.Core;
import static com.applecraftserver.plugins.core.IPlugin.$;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

public class IRCBot extends ListenerAdapter {
	public PircBotX pbot;
	private Configuration.Builder config;
	
	public IRCBot() {
		config = new Configuration.Builder()
				.setName(Core.instance.getConfig().getString("irc.nick"))
				.setLogin(Core.instance.getConfig().getString("irc.user"))
				.setServer(Core.instance.getConfig().getString("irc.server"), Core.instance.getConfig().getInt("irc.port"), Core.instance.getConfig().getString("irc.serverpass"))
				.setNickservPassword(Core.instance.getConfig().getString("irc.nickservpass"))
				.setAutoReconnect(Core.instance.getConfig().getBoolean("irc.autoreconnect"))
				.addAutoJoinChannel(Core.instance.getConfig().getString("irc.staffchannel"))
				.setVersion("Java IRC stuff")
				.setCapEnabled(true)
				.addListener(this);
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
