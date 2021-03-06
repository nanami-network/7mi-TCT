package me.clockclap.tct.command;

import me.clockclap.tct.NanamiTct;
import me.clockclap.tct.api.Reference;
import me.clockclap.tct.game.GameState;
import me.clockclap.tct.game.data.profile.TctPlayerProfile;
import me.clockclap.tct.game.role.GameTeams;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandStopGame implements CommandExecutor {

    private final NanamiTct plugin;

    public CommandStopGame(NanamiTct plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            TctPlayerProfile profile = plugin.getGame().getReference().PLAYERDATA.get(p.getUniqueId()).getProfile();
            final boolean isAdmin = profile.isAdmin();

            if (!isAdmin) {
                p.sendMessage(Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_ERROR_PERMISSION);
                return true;
            }
            if (plugin.getGame().getReference().getGameState() == GameState.GAMING || plugin.getGame().getReference().getGameState() == GameState.STARTING) {
                process();
                return true;
            }
            p.sendMessage(Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_ERROR_GAME_NOT_STARTED);
            return true;
        }
        if(plugin.getGame().getReference().getGameState() == GameState.GAMING || plugin.getGame().getReference().getGameState() == GameState.STARTING) {
            process();
            return true;
        }
        sender.sendMessage(Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_ERROR_GAME_NOT_STARTED);
        return true;
    }

    private void process() {
        if(plugin.getGame().getReference().getGameState() == GameState.STARTING) {
            plugin.getGame().getPreTimer().cancel();
        } else if(plugin.getGame().getReference().getGameState() == GameState.GAMING) {
            plugin.getGame().getTimer().cancel();
        }
        plugin.getGame().stop(GameTeams.NONE);
        Bukkit.broadcastMessage(Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_SYSTEM_STOPPED_GAME);
    }

}
