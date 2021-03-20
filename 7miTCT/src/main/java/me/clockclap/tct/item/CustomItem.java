package me.clockclap.tct.item;

import me.clockclap.tct.game.role.GameRole;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface CustomItem {

    public int getIndex();

    public ItemStack getItemStack();

    public Material getMaterial();

    public String getName();

    public String getDisplayName();

    public String getTitle();

    public String getDescription();

    public GameRole getRole();

    public boolean isDefault();

    public void setItemStack(ItemStack item);

    public void setMaterial(Material material);

    public void setName(String name);

    public void setDisplayName(String name);

    public void setTitle(String title);

    public void setDescription(String description);


}
