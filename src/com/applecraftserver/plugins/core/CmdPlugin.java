package com.applecraftserver.plugins.core;

import com.applecraftserver.plugins.core.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CmdPlugin implements CommandExecutor {

    private Core plugin;

    public CmdPlugin(Core pl) {
        this.plugin = pl;
    }

    private void unregisterAllPluginCommands(String pluginName) {
        try {
            Object result = Util.getPrivateField(plugin.getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = Util.getPrivateField(commandMap, "knownCommands");
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            final List<Command> commands = new ArrayList(commandMap.getCommands());
            for (Command c : commands) {
                if (!(c instanceof PluginCommand)) {
                    continue;
                }
                final PluginCommand pc = (PluginCommand) c;
                if (!pc.getPlugin().getName().equals(pluginName)) {
                    continue;
                }
                knownCommands.remove(pc.getName());
                for (String alias : pc.getAliases()) {
                    if (knownCommands.containsKey(alias)) {
                        final Command ac = knownCommands.get(alias);
                        if (!(ac instanceof PluginCommand)) {
                            continue;
                        }
                        final PluginCommand apc = (PluginCommand) ac;
                        if (!apc.getPlugin().getName().equals(pluginName)) {
                            continue;
                        }
                        knownCommands.remove(alias);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removePluginFromList(Plugin p) {
        try {
            final List<Plugin> plugins = (List<Plugin>) Util.getPrivateField(plugin.getServer().getPluginManager(), "plugins");
            plugins.remove(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String l, String[] args) {
        if (l.equalsIgnoreCase("plugin")) {
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }

            String subcmd = args[0];
            final PluginManager pm = plugin.getServer().getPluginManager();

            if (subcmd.equalsIgnoreCase("")) {

            }

        }
        return false;
    }
}
