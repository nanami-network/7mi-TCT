package me.clockclap.tct.command;

import me.clockclap.tct.NanamiTct;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CommandTctPlugin implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(NanamiTct.getTctPluginLoader().getLoadedPlugins() != null) {
            sender.sendMessage("Plugins " + getPluginList());
        } else {
            sender.sendMessage("Plugins (0):");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }

    private String getPluginList() {
        TreeMap<String, ChatColor> plugins = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (Plugin plugin : NanamiTct.getTctPluginLoader().getLoadedPlugins()) {
            plugins.put(plugin.getDescription().getName(), plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
        }

        StringBuilder pluginList = new StringBuilder();
        for (Map.Entry<String, ChatColor> entry : plugins.entrySet()) {
            if (pluginList.length() > 0) {
                pluginList.append(ChatColor.WHITE);
                pluginList.append(", ");
            }
            pluginList.append(entry.getValue());
            pluginList.append(entry.getKey());
        }

        return "(" + plugins.size() + "): " + pluginList;
    }
}
