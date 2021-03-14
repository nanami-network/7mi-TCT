package me.clockclap.tct;

import me.clockclap.tct.api.Utilities;
import me.clockclap.tct.command.CommandAboutTCT;
import me.clockclap.tct.event.ChatEvent;
import me.clockclap.tct.event.PlayerConnectionEvent;
import me.clockclap.tct.event.ProtectWorld;
import me.clockclap.tct.game.GameReference;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NanamiTct extends JavaPlugin {

    public static NanamiTct plugin;
    public static Utilities utilities;

    private GameReference gameReference;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        utilities = new Utilities(this);
        gameReference = new GameReference(this);

        plugin = this;
        getLogger().info("Starting up...");
        PluginManager pm = Bukkit.getServer().getPluginManager();

        //Register Events
        pm.registerEvents(new PlayerConnectionEvent(this), this);
        pm.registerEvents(new ChatEvent(), this);
        pm.registerEvents(new ProtectWorld(), this);

        //Add Commands
        utilities.addCommand("abouttct", new CommandAboutTCT());

    }

    public GameReference getGameReference() {
        return this.gameReference;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Shutting down...");
    }
}
