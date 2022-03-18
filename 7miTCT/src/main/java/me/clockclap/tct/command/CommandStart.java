package me.clockclap.tct.command;

import me.clockclap.tct.NanamiTct;
import me.clockclap.tct.api.Reference;
import me.clockclap.tct.game.GameState;
import me.clockclap.tct.game.data.profile.TCTPlayerProfile;
import me.clockclap.tct.game.role.GameTeams;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStart implements CommandExecutor {

    private final NanamiTct plugin;

    public CommandStart(NanamiTct plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            TCTPlayerProfile profile = plugin.getGame().getReference().PLAYERDATA.get(p.getUniqueId()).getProfile();
            boolean isAdmin = profile.isAdmin();
//              if(plugin.getTctConfig().getConfig().getStringList("admin").contains("op")) {
//                  if(p.isOp()) {
//                      isAdmin = true;
//                  }
//              }
//              if(isAdmin == false) {
//                 for (String str : plugin.getTctConfig().getConfig().getStringList("admin")) {
//                      if (NanamiTct.utilities.resetColor(p.getName()).equalsIgnoreCase(str)) {
//                          isAdmin = true;
//                          break;
//                      }
//                  }
//              }
            if (!isAdmin) {
                p.sendMessage(Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_ERROR_PERMISSION);
                return true;
            }
            if(plugin.getGame().getReference().getGameState() == GameState.GAMING || plugin.getGame().getReference().getGameState() == GameState.STARTING) {
                stopGame();
                return true;
            }
            boolean success = plugin.getGame().preStart(p.getLocation());
            if (!success) {
                p.sendMessage(Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_ERROR_PLAYERS_NEEDED);
                p.sendMessage(Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_NEEDED_PLAYERS.replaceAll("%COUNT_A%", String.valueOf(plugin.getGame().getNeededPlayers())).replaceAll("%COUNT_B%", String.valueOf(plugin.getGame().getNeededPlayers() - Bukkit.getOnlinePlayers().size())));
            }
            return true;
        }
        sender.sendMessage(Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_PLAYER_ONLY);
        return true;
    }

    private void stopGame() {
        if(plugin.getGame().getReference().getGameState() == GameState.STARTING) {
            plugin.getGame().getPreTimer().cancel();
        } else if(plugin.getGame().getReference().getGameState() == GameState.GAMING) {
            plugin.getGame().getTimer().cancel();
        }
        plugin.getGame().stop(GameTeams.NONE);
        Bukkit.broadcastMessage(Reference.TCT_CHATPREFIX + " " + Reference.TCT_CHAT_SYSTEM_STOPPED_GAME);
    }

}
