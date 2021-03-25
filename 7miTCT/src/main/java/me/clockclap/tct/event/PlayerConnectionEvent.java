package me.clockclap.tct.event;

import me.clockclap.tct.NanamiTct;
import me.clockclap.tct.api.PlayerWatcher;
import me.clockclap.tct.api.Reference;
import me.clockclap.tct.game.GameState;
import me.clockclap.tct.game.data.PlayerData;
import me.clockclap.tct.game.data.TctPlayerData;
import me.clockclap.tct.game.death.Killer;
import me.clockclap.tct.game.death.TctDeathCause;
import me.clockclap.tct.game.role.GameRoles;
import me.clockclap.tct.game.role.GameTeams;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerConnectionEvent implements Listener {

    private NanamiTct plugin;

    public PlayerConnectionEvent(NanamiTct plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage(Reference.TCT_CHAT_JOIN_MESSAGE.replaceAll("%PLAYER%",p.getDisplayName()));
        plugin.getGame().getBar().addPlayer(p);
        PlayerData data = new TctPlayerData(plugin, GameRoles.SPEC, p.getName());
        PlayerWatcher watcher = new PlayerWatcher(plugin.getGame(), p);
        data.setSpectator(true);
        data.setWatcher(watcher);
        data.getWatcher().startWatch();
        plugin.getGame().getReference().PLAYERDATA.put(p.getName(), data);
        p.sendMessage(Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_JOIN_MESSAGE_0.replaceAll("%VERSION%", plugin.getDescription().getVersion()));
        p.sendMessage(Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_JOIN_MESSAGE_1);
        String gameState = "";
        String message = "";
        boolean isAdmin = false;
        if(plugin.getTctConfig().getConfig().getStringList("admin").contains("op")) {
            if(p.isOp()) {
                isAdmin = true;
            }
        }
        if(isAdmin == false) {
            for (String str : plugin.getTctConfig().getConfig().getStringList("admin")) {
                if (p.getName().equalsIgnoreCase(str)) {
                    isAdmin = true;
                    break;
                }
            }
        }
        if(plugin.getGame().getReference().getGameState() == GameState.WAITING) {
            gameState = "Waiting";
            if(isAdmin == true) {
                message = Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_PLEASE_START;
            } else {
                message = Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_PLEASE_WAIT;
            }
        } else if(plugin.getGame().getReference().getGameState() == GameState.GAMING) {
            gameState = "Gaming";
            message = Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_GAME_ALREADY_STARTED;
        } else {
            gameState = "Waiting";
            if(isAdmin == true) {
                message = Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_PLEASE_START;
            } else {
                message = Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_PLEASE_WAIT;
            }
        }
        p.sendMessage(Reference.TCT_CHATPREFIX + " " + Reference.TCT_GAME_STATE.replaceAll("%STATE%", gameState));
        p.sendMessage(message);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage(Reference.TCT_CHAT_QUIT_MESSAGE.replaceAll("%PLAYER%",p.getDisplayName()));
        PlayerData data = plugin.getGame().getReference().PLAYERDATA.get(p.getName());
        if(data.getWatcher() != null) {
            data.getWatcher().cancelPlayerWatcher();
        }
        if(plugin.getGame().getReference().getGameState() == GameState.GAMING) {
            if(!data.isSpectator()) {
                plugin.getGame().removeRemainingPlayers(data, true);
                p.getInventory().clear();
                List<PlayerData> villagers = new ArrayList<>();
                List<PlayerData> wolves = new ArrayList<>();
                List<PlayerData> foxes = new ArrayList<>();
                for (PlayerData d : plugin.getGame().getRemainingPlayers(true)) {
                    if (d.getRole().getTeam() == GameTeams.VILLAGERS) {
                        villagers.add(d);
                        continue;
                    }
                    if (d.getRole().getTeam() == GameTeams.WOLVES) {
                        wolves.add(d);
                        continue;
                    }
                    if (d.getRole().getTeam() == GameTeams.FOXES) {
                        foxes.add(d);
                    }
                }
                if (villagers.size() > 0 && wolves.size() <= 0) {
                    plugin.getGame().getTimer().cancel();
                    if (foxes.size() > 0) {
                        plugin.getGame().stop(GameTeams.FOXES);
                        return;
                    }
                    plugin.getGame().stop(GameTeams.VILLAGERS);
                    return;
                }
                if (wolves.size() > 0 && villagers.size() <= 0) {
                    plugin.getGame().getTimer().cancel();
                    if (foxes.size() > 0) {
                        plugin.getGame().stop(GameTeams.FOXES);
                        return;
                    }
                    plugin.getGame().stop(GameTeams.WOLVES);
                    return;
                }
                if (villagers.size() <= 0 && wolves.size() <= 0) {
                    plugin.getGame().getTimer().cancel();
                    if (foxes.size() > 0) {
                        plugin.getGame().stop(GameTeams.FOXES);
                        return;
                    }
                    plugin.getGame().stop(GameTeams.VILLAGERS);
                    return;
                }
                if(data.getRole() == GameRoles.FOX && data.getWatcher() != null) {
                    data.getWatcher().cancelCountFox();
                }
                data.setKilledBy(new Killer("AIR", GameRoles.NONE, Killer.KillerCategory.AIR));
                data.kill(TctDeathCause.LOST_CONNECTION);
            }
        }
        plugin.getGame().getReference().PLAYERDATA.remove(p.getName());
    }

}
