package me.clockclap.tct.item.items;

import me.clockclap.tct.NanamiTctApi;
import me.clockclap.tct.game.role.GameRole;
import me.clockclap.tct.game.role.GameRoles;
import me.clockclap.tct.item.CustomSpecialItem;
import me.clockclap.tct.item.ItemIndex;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class TctItemHealerSword implements CustomSpecialItem {

    private ItemStack item;
    private Material material;
    private String name;
    private String displayName;
    private String title;
    private String description;
    private boolean attackable;
    private boolean quickchat;

    private final GameRole role;
    private final boolean isdefault;
    private final int index;

    public TctItemHealerSword() {
        this.index = ItemIndex.DEFAULT_ITEM_SLOT_HEALER;
        this.isdefault = false;
        this.material = Material.IRON_SWORD;
        this.name = "HEALER_SWORD";
        this.displayName = "Healer Sword";
        this.title = "Healer Sword";
        this.description = ChatColor.LIGHT_PURPLE + "Healer Item";
        this.role = GameRoles.HEALER;
        this.attackable = false;
        this.quickchat = false;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + displayName);
        List<String> lore = new ArrayList<>();
        lore.add(description);
        meta.setLore(lore);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        this.item = item;
    }

    @Override
    public boolean isQuickChatItem() {
        return this.quickchat;
    }

    @Override
    public void onAttackPlayer(Player attacker, Player target) {
        int tick = 20;
        int level;
        FileConfiguration config = NanamiTctApi.config.getConfig();
        try {
            if (config.getString("effect.healer-regeneration.duration").endsWith("t")) {
                String str = config.getString("effect.healer-regeneration.duration");
                str = str.substring(0, str.length() - 1);
                try {
                    tick = Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    tick = 20;
                }
            } else if (config.getString("effect.healer-regeneration.duration").endsWith("s")) {
                String str = config.getString("effect.healer-regeneration.duration");
                str = str.substring(0, str.length() - 1);
                try {
                    tick = Integer.parseInt(str) * 20;
                } catch (NumberFormatException e) {
                    tick = 20;
                }
            }
        } catch(NullPointerException e) {
            tick = 20;
        }
        try {
            level = config.getInt("effect.healer-regeneration.level");
        } catch(Exception e) {
            level = 3;
        }
        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, tick, level));
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public ItemStack getItemStack() {
        return this.item;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public GameRole getRole() {
        return this.role;
    }

    @Override
    public boolean isDefault() {
        return this.isdefault;
    }

    @Override
    public boolean isAttackable() {
        return this.attackable;
    }

    @Override
    public void setAttackable(boolean value) {
        this.attackable = value;
    }

    @Override
    public void setItemStack(ItemStack item) {
        this.item = item;
    }

    @Override
    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setDisplayName(String name) {
        this.displayName = name;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

}
