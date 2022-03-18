package me.clockclap.tct.game.data;

import me.clockclap.tct.NanamiTct;
import me.clockclap.tct.NanamiTctApi;
import me.clockclap.tct.VersionUtils;
import me.clockclap.tct.api.PlayerWatcher;
import me.clockclap.tct.game.data.profile.TCTPlayerProfile;
import me.clockclap.tct.game.death.DeadBody;
import me.clockclap.tct.game.death.Killer;
import me.clockclap.tct.game.death.TctDeathCause;
import me.clockclap.tct.game.role.GameRole;
import me.clockclap.tct.game.role.GameRoles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TCTPlayerData extends TctEntityData implements PlayerData {

    private GameRole co;
    private final String name;
    private boolean spec;
    private int quickchatcooldown;
    private int coin;
    private PlayerWatcher watcher;
    private List<String> boughtItem = new ArrayList<>();
    private Killer killer;
    private int together;
    private int villager;
    private int sus;
    private int wolf;
    private List<String> killedPlayers;
    private final TCTPlayerProfile profile;
    private final Player player;
    private boolean sponge;
    private boolean invisible;
    private boolean clickable;
    private boolean clickableB;
    private boolean clickableE;
    private Location savedLoc;
    private boolean afterSaved;
    private boolean teleporting;

    public TCTPlayerData(NanamiTct plugin, GameRole role, String name) {
        super(plugin, Bukkit.getPlayer(name), role);
        this.name = name;
        this.spec = true;
        this.co = GameRoles.NONE;
        this.quickchatcooldown = 0;
        this.coin = 0;
        together = 0;
        villager = 0;
        sus = 0;
        wolf = 0;
        killedPlayers = new ArrayList<>();
        sponge = false;
        invisible = false;
        profile = new TCTPlayerProfile(name);
        clickable = true;
        clickableB = true;
        clickableE = true;
        afterSaved = false;
        teleporting = false;
        Player p = Bukkit.getPlayer(name);
        this.player = p;
        if (p != null) {
            boolean isAdmin = false;
            if (plugin.getTctConfig().getConfig().getStringList("admin").contains("op")) {
                if (p.isOp()) {
                    isAdmin = true;
                }
            }
            if (!isAdmin) {
                for (String str : plugin.getTctConfig().getConfig().getStringList("admin")) {
                    if (NanamiTct.utilities.resetColor(p.getName()).equalsIgnoreCase(str)) {
                        isAdmin = true;
                        break;
                    }
                }
            }
            if (isAdmin) {
                profile.modify().setBoolean("admin", true).save();
            }
        }
    }

    @Override
    public GameRole getRole() {
        return super.role;
    }

    @Override
    public GameRole getCO() {
        return this.co;
    }

    @Override
    public Entity getEntity() {
        return Bukkit.getPlayer(this.name);
    }

    @Override
    public UUID getUniqueId() {
        return Bukkit.getPlayer(this.name).getUniqueId();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isSpectator() {
        return this.spec;
    }

    @Override
    public int getQuickChatCooldown() {
        return this.quickchatcooldown;
    }

    @Override
    public int getCoin() {
        return this.coin;
    }

    @Override
    public PlayerWatcher getWatcher() {
        return this.watcher;
    }

    @Override
    public Killer getKilledBy() {
        return this.killer;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public Block getTargetBlock(int range) {
        Player p = getPlayer();
        BlockIterator iter = new BlockIterator(p, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }

    @Override
    public List<String> getBoughtItem() {
        return this.boughtItem;
    }

    @Override
    public int getTogether() {
        return together;
    }

    @Override
    public int getVillager() {
        return villager;
    }

    @Override
    public int getSuspicious() {
        return sus;
    }

    @Override
    public int getWolf() {
        return wolf;
    }

    @Override
    public TCTPlayerProfile getProfile() {
        return profile;
    }

    @Override
    public Location getSavedLocation() {
        return savedLoc;
    }

    @Override
    public void saveLocation(Location location) {
        this.savedLoc = location;
    }

    @Override
    public boolean isAfterSaved() {
        return afterSaved;
    }

    @Override
    public void setAfterSaved(boolean afterSaved) {
        this.afterSaved = afterSaved;
    }

    @Override
    public boolean isTeleporting() {
        return teleporting;
    }

    @Override
    public void setTeleporting(boolean teleporting) {
        this.teleporting = teleporting;
    }

    @Override
    public List<String> getKilledPlayers() {
        return this.killedPlayers;
    }

    @Override
    public boolean isClickableItem() {
        return clickable;
    }

    @Override
    public boolean isClickableBlock() {
        return clickableB;
    }

    @Override
    public boolean isClickableEntity() {
        return clickableE;
    }

    @Override
    public void setClickableItem(boolean value) {
        this.clickable = value;
    }

    @Override
    public void setClickableBlock(boolean value) {
        this.clickableB = value;
    }

    @Override
    public void setClickableEntity(boolean value) {
        this.clickableE = value;
    }

    @Override
    public boolean hasSponge() {
        return sponge;
    }

    @Override
    public boolean isInvisible() {
        return invisible;
    }

    @Override
    public void setInvisible(boolean bool, boolean clock) {
        if (this.invisible && clock && !bool && !isSpectator()) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);

            final int max = NanamiTctApi.config.getConfig().getInt("effect.wolf-invisible.playsound-range", 5);

            for (Player target : Bukkit.getOnlinePlayers()) {

                final double distance = target.getLocation().distance(player.getLocation());

                if (max >= distance) {
                    if (VersionUtils.isHigherThanVersion(VersionUtils.V1_12_2)) {
                        target.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, max, 1);
                    } else {
                        target.playSound(player.getLocation(), Sound.valueOf("ENTITY_FIREWORK_LARGE_BLAST"), max, 1);
                    }
                }
            }
        }

        this.invisible = bool;
    }

    @Override
    public void setSponge(boolean bool) {
        this.sponge = bool;
    }

    @Override
    public void addKilledPlayer(String name) {
        this.killedPlayers.add(name);
    }

    @Override
    public void removeKilledPlayer(String name) {
        this.killedPlayers.remove(name);
    }

    @Override
    public void removeKilledPlayer(int index) {
        this.killedPlayers.remove(index);
    }

    @Override
    public void resetKilledPlayers() {
        this.killedPlayers = new ArrayList<>();
    }

    @Override
    public void setKilledPlayers(List<String> list) {
        this.killedPlayers = list;
    }

    @Override
    public void setTogether(int value) {
        this.together = value;
    }

    @Override
    public void setVillager(int value) {
        this.villager = value;
    }

    @Override
    public void setSuspicious(int value) {
        this.sus = value;
    }

    @Override
    public void setWolf(int value) {
        this.wolf = value;
    }

    @Override
    public void setBoughtItem(List<String> list) {
        this.boughtItem = list;
    }

    @Override
    public void resetBoughtItem() {
        this.boughtItem = new ArrayList<>();
    }

    @Override
    public void addBoughtItem(String item) {
        this.boughtItem.add(item);
    }

    @Override
    public void removeBoughtItem(String item) {
        this.boughtItem.remove(item);
    }

    @Override
    public void startQCCCountdown() {
        int defaultSec = plugin.getTctConfig().getConfig().getInt("quickchat-cooldown", 20) + 1;
        setQuickChatCooldown(defaultSec);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(getQuickChatCooldown() > 0) {
                    setQuickChatCooldown(getQuickChatCooldown() - 1);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void setCoin(int coin) {
        this.coin = coin;
    }

    @Override
    public void setRole(GameRole role) {
        super.role = role;
    }

    @Override
    public void setCO(GameRole role) {
        this.co = role;
    }

    @Override
    public void setSpectator(boolean bool) {
        this.spec = bool;
    }

    @Override
    public void setQuickChatCooldown(int second) {
        this.quickchatcooldown = second;
    }

    @Override
    public void setWatcher(PlayerWatcher watcher) {
        this.watcher = watcher;
    }

    @Override
    public void setKilledBy(Killer killer) {
        this.killer = killer;
    }

    @Override
    public void kill(TctDeathCause cause) {
        Location loc = this.getPlayer().getLocation();
        Location blockLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        DeadBody deadBody = new DeadBody(plugin.getGame(), this, cause, blockLoc);
        deadBody.setKilledPlayers(this.getKilledPlayers());
        plugin.getGame().getReference().DEADBODIES.add(deadBody);
        plugin.getGame().removeRemainingPlayers(this, true);
        if(cause == TctDeathCause.KILL) {
            plugin.getGame().setRealRemainingSeconds(plugin.getGame().getRealRemainingSeconds() + plugin.getTctConfig().getConfig().getInt("countdown.addcount.kill", 20));
        }
        deadBody.process();
        this.getPlayer().setGameMode(GameMode.SPECTATOR);
        this.setSpectator(true);
    }

}
