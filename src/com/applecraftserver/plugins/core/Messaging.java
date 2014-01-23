package com.applecraftserver.plugins.core;

//import com.applecraftserver.plugins.irc.*;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;

public class Messaging {

	public static final Map<String, String> privMsgs = new HashMap();

	public static void broadcast(String msg) {
		Bukkit.broadcastMessage(MessageColor.NEUTRAL + "[!] " + MessageColor.RESET + msg);
	}
	
	public static void playerAdminMessage(String who, String message) {
		adminMessage(MessageColor.POSITIVE + who + " " + MessageColor.RESET + message);
	}

	public static void adminMessage(String msg) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!p.isOp()/* && !Merinium.perms.playerHas(p, Nodes.ADMIN_SEEMESSAGES)*/) {
				return;
			}
			send(p, MessageColor.NEUTRAL + "-ADMIN- " + MessageColor.RESET + msg.trim());
		}
	}

	public static void adminMessage(String[] msgs) {
		for (String s : msgs) {
			adminMessage(s);
		}
	}

	public static void adminAlert(String msg) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!p.isOp()/* && !Merinium.perms.playerHas(p, Nodes.ADMIN_SEEMESSAGES)*/) {
				return;
			}
			send(p, MessageColor.NEUTRAL + "[A] " + MessageColor.NEGATIVE + msg.trim());
		}
	}

	public static void adminAlert2(String msg) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!p.isOp()/* && !Merinium.perms.playerHas(p, Nodes.ADMIN_SEEMESSAGES)*/) {
				return;
			}
			send(p, MessageColor.NEGATIVE + "[A] " + msg.trim());
		}
	}

	public static void noPerms(CommandSender cs) {
		send(cs, Messages.LACK_PERMISSION.toString());
	}

	public static void privMsg(CommandSender sender, CommandSender recipient, String message) {
		if (sender == recipient) {
			send(sender, MessageColor.NEGATIVE + "Yeah, that's real useful.");
			return;
		}
		Validate.notEmpty(message);
		synchronized (privMsgs) {
			privMsgs.put(sender.getName(), recipient.getName());
			privMsgs.put(recipient.getName(), sender.getName());
		}
		if (sender instanceof ConsoleCommandSender) {
			send(sender, MessageColor.NEUTRAL + "[ You to " + recipient.getName() + " ] : " + MessageColor.RESET + message);
			send(recipient, MessageColor.NEUTRAL + "[ " + Messages.CONSOLE_NAME + " to you ] : " + MessageColor.RESET + message);
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasMetadata("messageSpy")) {
					send(p, MessageColor.NEUTRAL + "[ " + Messages.CONSOLE_NAME + " to " + recipient.getName() + " ] : " + MessageColor.RESET + message);
				}
			}
		} else if (recipient instanceof ConsoleCommandSender) {
			send(sender, MessageColor.NEUTRAL + "[ You to " + Messages.CONSOLE_NAME + " ] : " + MessageColor.RESET + message);
			send(recipient, MessageColor.NEUTRAL + "[ " + sender.getName() + " to you ] : " + MessageColor.RESET + message);
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasMetadata("messageSpy")) {
					send(p, MessageColor.NEUTRAL + "[ " + Messages.CONSOLE_NAME + " to " + recipient.getName() + " ] : " + MessageColor.RESET + message);
				}
			}
		} else {
			send(sender, MessageColor.NEUTRAL + "[ You to " + recipient.getName() + " ] : " + MessageColor.RESET + message);
			send(recipient, MessageColor.NEUTRAL + "[ " + sender.getName() + " to you ] : " + MessageColor.RESET + message);
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasMetadata("messageSpy")) {
					send(p, MessageColor.NEUTRAL + "[ " + sender.getName() + " to " + recipient.getName() + " ] : " + MessageColor.RESET + message);
				}
			}
		}
	}

	public static void send(CommandSender who, String template, Object... args) {
		who.sendMessage(String.format(template, args));
	}

	public static void sendRaw(Conversable who, String template, Object... args) {
		who.sendRawMessage(String.format(template, args));
	}

	public static void broadf(String template, Object... args) {
		broadcast(String.format(template, args));
	}

	public static void broadfLater(String template, Object... args) {
		Bukkit.getScheduler().runTaskLater(Core.instance, new MessageTask(template, args), 3l);
	}

	public enum MessageColor {

		POSITIVE(Core.instance.getConfig().getString("colors.positive"), ChatColor.GREEN),
		NEUTRAL(Core.instance.getConfig().getString("colors.neutral"), ChatColor.GRAY),
		NEGATIVE(Core.instance.getConfig().getString("colors.negative"), ChatColor.RED),
		BOT_PREFIX(Core.instance.getConfig().getString("colors.botprefix"), ChatColor.AQUA),
		RESET("RESET", ChatColor.RESET);
		private ChatColor c;

		MessageColor(String custom, ChatColor def) {
			try {
				c = ChatColor.valueOf(custom.toUpperCase());
			} catch (IllegalArgumentException e) {
				c = def;
			}
		}

		@Override
		public String toString() {
			return c.toString();
		}

		public ChatColor getChatColor() {
			return c;
		}
	}

	public enum Messages {

		LACK_PERMISSION(MessageColor.NEGATIVE + "You don't have permission to do that!"),
		ADMIN_PREFIX("[ADMIN] "),
		ADMIN_ALERT_PREFIX("[A] "),
		ALERT_PREFIX("[!] "),
		BROADCAST_PREFIX("[BC] "),
		CONSOLE_NAME("CONSOLE"),
		BOT_NAME("Wheatley"),
		USAGEHEADING("\u00A7eUsage:");

		Messages(String message) {
		}
	}

	private static final class MessageTask implements Runnable {

		private String m;
		private Object[] args;

		public MessageTask(String m, Object... args) {
			this.m = m;
			this.args = args;
		}

		@Override
		public void run() {
			Messaging.broadf(m, args);
		}
	}
}
