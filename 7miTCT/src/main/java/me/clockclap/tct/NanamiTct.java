package me.clockclap.tct;

import com.google.common.base.Charsets;
import me.clockclap.tct.api.*;
import me.clockclap.tct.api.event.ArmorListener;
import me.clockclap.tct.api.sql.MySQLConnection;
import me.clockclap.tct.api.sql.MySQLPlayerStats;
import me.clockclap.tct.api.sql.MySQLStatus;
import me.clockclap.tct.command.*;
import me.clockclap.tct.event.*;
import me.clockclap.tct.game.Game;
import me.clockclap.tct.game.TCTGame;
import me.clockclap.tct.game.data.PlayerData;
import me.clockclap.tct.game.data.PlayerStat;
import me.clockclap.tct.game.data.TCTPlayerData;
import me.clockclap.tct.game.data.TCTPlayerStat;
import me.clockclap.tct.game.death.Killer;
import me.clockclap.tct.game.role.CustomRoles;
import me.clockclap.tct.game.role.GameRoles;
import me.clockclap.tct.game.role.CustomTeams;
import me.clockclap.tct.inventory.CustomInventory;
import me.clockclap.tct.item.CustomItems;
import me.clockclap.tct.plugin.TctPluginLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public final class NanamiTct extends JavaPlugin {

    public static NanamiTct plugin;
    public static TctUtilities utilities;
    public static CustomTeams teamRegisterer;
    public static CustomRoles roleRegisterer;
    public static MySQLConnection sqlConnection;
    public static MySQLPlayerStats playerStats;
    public static boolean isLoaded = false;
    private static TctPluginLoader loader;

    private TCTGame game;
    private ITctConfiguration configuration;
    private CustomInventory customInventory;
    public Plugin[] loadedPlugins;

    public static TctPluginLoader getTctPluginLoader() {
        return loader;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        NanamiTctApi.plugin = plugin;
        loader = new TctPluginLoader(NanamiTct.class);
        loadedPlugins = new Plugin[0];
        SimplePluginManager pluginManager = (SimplePluginManager) Bukkit.getServer().getPluginManager();
        utilities = new TctUtilities(this);
        NanamiTctApi.utilities = utilities;
        teamRegisterer = new CustomTeams();
        NanamiTctApi.teamRegistry = teamRegisterer;
        roleRegisterer = new CustomRoles();
        NanamiTctApi.roleRegistry = roleRegisterer;
        game = new Game(this);
        NanamiTctApi.game = game;
        configuration = new TctConfiguration(this);
        try {
            configuration.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NanamiTctApi.config = configuration;
        sqlConnection = new MySQLConnection(configuration.getConfig().getString("mysql.hostname", "localhost"),
                configuration.getConfig().getInt("mysql.port", 3306),
                configuration.getConfig().getString("mysql.database", ""),
                configuration.getConfig().getString("mysql.username", ""),
                configuration.getConfig().getString("mysql.password", ""),
                configuration.getConfig().getString("mysql.option", "?allowPublicKeyRetrieval=true&useSSL=false"));

        try {
            sqlConnection.openConnection();
            MySQLStatus.setSqlEnabled(true);
        } catch (SQLException ex) {
            MySQLStatus.setSqlEnabled(false);
            ex.printStackTrace();
        }
        NanamiTctApi.connection = sqlConnection;

        if (MySQLStatus.isSqlEnabled()) {
            playerStats = new MySQLPlayerStats(sqlConnection, game);
            try {
                playerStats.createTable();
            } catch (SQLException throwables) {
                MySQLStatus.setSqlEnabled(false);
                throwables.printStackTrace();
            }
        }
        NanamiTctApi.playerStats = playerStats;

        getLogger().info("Starting up...");

        // Register Events
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new ChatListener(this), this);
        //pluginManager.registerEvents(new CancelHunger(this), this);
        pluginManager.registerEvents(new ItemListener(this), this);
        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new DamageListener(this), this);
        pluginManager.registerEvents(new InventoryListener(this), this);
        pluginManager.registerEvents(new ArmorListener(Reference.TCT_BLOCKED), this);

        // Add Commands
        utilities.addCommand("abouttct", getName(), "", "7mi-TCT??????????????????????????????????????????", new ArrayList<>(), new CommandAboutTCT(this));
        utilities.addCommand("gmc", getName(), "", "?????????????????????????????????????????????????????????????????????????????????", new ArrayList<>(), new CommandGameModeCreative());
        utilities.addCommand("gms", getName(), "", "???????????????????????????????????????????????????????????????????????????", new ArrayList<>(), new CommandGameModeSurvival());
        utilities.addCommand("gmsall", getName(), "", "???????????????????????????????????????????????????????????????????????????", new ArrayList<>(), new CommandGameModeSurvivalAll());
        utilities.addCommand("tctreload", getName(), "", "??????????????????????????????????????????", new ArrayList<>(), new CommandTctReload(this));
        utilities.addCommand("barrier", getName(), "", "??????????????????????????????????????????", Arrays.asList("b", "gb"), new CommandBarrier());
        utilities.addCommand("start", getName(), "", "??????????????????????????????", new ArrayList<>(), new CommandStart(this));
        utilities.addCommand("startloc", getName(), "", "??????????????????????????????????????????????????????", new ArrayList<>(), new CommandStartLoc(this));
        utilities.addCommand("stopgame", getName(), "", "???????????????????????????????????????", new ArrayList<>(), new CommandStopGame(this));
        utilities.addCommand("item", getName(), "", "?????????????????????????????????", Arrays.asList("i"), new CommandItem(this));
        utilities.addCommand("shop", getName(), "", "????????????????????????????????????", Arrays.asList("s"), new CommandShop(this));
        utilities.addCommand("stat", getName(), "", "??????????????????????????????", new ArrayList<>(), new CommandStat());
        //utilities.addCommand("tctplugins", getName(), "", "?????????????????????????????????TCT?????????????????????????????????????????????", Arrays.asList("tctpl"), new CommandTctPlugin());

        // Register items
        CustomItems.register();

        // Initialize TCT log
        game.getLog().initialize();

        // Register boss bar
        BossBar bar = Bukkit.getServer().createBossBar(Reference.TCT_BOSSBAR_FORMAT_WAITING, BarColor.RED, BarStyle.SOLID);
        bar.setVisible(true);
        bar.setProgress(1.0);
        this.game.setBar(bar);

        // Initialize player data
        if (Bukkit.getOnlinePlayers().size() > 0) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p != null) {
                    String name = utilities.resetColor(p.getName());
                    PlayerData data = new TCTPlayerData(this, GameRoles.SPEC, name);
                    PlayerWatcher watcher = new PlayerWatcher(plugin.getGame(), p);
                    data.setSpectator(true);
                    data.setWatcher(watcher);
                    data.getWatcher().startWatch();
                    boolean isAdmin = false;
                    FileConfiguration config = getTctConfig().getConfig();
                    if (config.getStringList("admin").contains(name)) {
                        isAdmin = true;
                    } else if (config.getStringList("admin").contains("op") && p.isOp()) {
                        isAdmin = true;
                    }
                    data.getProfile().modify().setBoolean("admin", isAdmin).save();
                    getGame().getReference().PLAYERDATA.put(p.getUniqueId(), data);
                    p.setFoodLevel(20);
                    p.setPlayerListName(ChatColor.GREEN + name);
                    if (MySQLStatus.isSqlEnabled() && NanamiTct.sqlConnection != null && NanamiTct.playerStats != null && NanamiTct.sqlConnection.getConnection() != null) {
                        PlayerStat stat = new TCTPlayerStat(p.getUniqueId());
                        try {
                            NanamiTct.playerStats.insert(stat);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            MySQLStatus.setSqlEnabled(false);
                        }
                    }
                    bar.addPlayer(p);
                    TctUtilities utilities = NanamiTct.utilities;
                    utilities.modifyName(p, ChatColor.GREEN + name);
                    for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                        if (pl != null) {
                            pl.hidePlayer(plugin, p);
                            pl.showPlayer(plugin, p);
                        }
                    }
                    utilities.modifyName(p, name);
                    data.setSponge(false);
                    data.setKilledBy(new Killer("AIR", GameRoles.NONE, Killer.KillerCategory.AIR));
                }
            }
        }

        customInventory = new CustomInventory(game);
        customInventory.initialize();

        //loader.enablePlugins();
    }

    public TCTGame getGame() {
        return this.game;
    }

    public ITctConfiguration getTctConfig() {
        return this.configuration;
    }

    public CustomInventory getCustomInventory() {
        return this.customInventory;
    }

    private void reload() {
        FileConfiguration newConfig = YamlConfiguration.loadConfiguration(plugin.getTctConfig().getConfigFile());

        final InputStream defConfigStream = plugin.getResource("config.yml");
        if (defConfigStream == null) {
            return;
        }

        newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        try {
            plugin.getTctConfig().getConfig().load(plugin.getTctConfig().getConfigFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != null) {
                String name = NanamiTct.utilities.resetColor(p.getName());
                PlayerData data = plugin.getGame().getReference().PLAYERDATA.get(p.getUniqueId());
                if (data == null) {
                    continue;
                }
                boolean isAdmin = false;
                FileConfiguration config = plugin.getTctConfig().getConfig();
                if (config.getStringList("admin").contains(name)) {
                    isAdmin = true;
                } else if (config.getStringList("admin").contains("op") && p.isOp()) {
                    isAdmin = true;
                }
                data.getProfile().modify().setBoolean("admin", isAdmin).save();
            }
        }
        isLoaded = true;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Shutting down...");

        // Unregister items
        CustomItems.unregister();

        // Unregister boss bar
        getGame().getBar().removeAll();
    }
}