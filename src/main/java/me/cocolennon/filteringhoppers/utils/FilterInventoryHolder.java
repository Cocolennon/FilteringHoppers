package me.cocolennon.filteringhoppers.utils;

import me.cocolennon.filteringhoppers.Main;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FilterInventoryHolder implements InventoryHolder {
    Inventory inventory;
    Block block;

    public FilterInventoryHolder(Main main, int size, String title, Block block) {
        this.inventory = main.getServer().createInventory(this, size, Main.getMiniMessage().deserialize(title));
        this.block = block;
    }

    public ItemStack getItem(int slot) { return inventory.getItem(slot); }
    public void setItem(int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
    }
    public void addItem(ItemStack itemStack) { inventory.addItem(itemStack); }

    public void fillEmpty(int start, ItemStack item) {
        for(int i = start; i < inventory.getSize(); i++){
            if(inventory.getItem(i) == null) inventory.setItem(i, item);
        }
    }

    public void fillEmpty(int start, int end, ItemStack item) {
        for(int i = start; i <= end; i++){
            if(inventory.getItem(i) == null) inventory.setItem(i, item);
        }
    }

    public Block getBlock() { return block; }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
