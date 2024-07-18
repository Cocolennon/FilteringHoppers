package me.cocolennon.filteringhoppers.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.cocolennon.filteringhoppers.Main;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Arrays;
import java.util.List;

public class InventoryMoveItemListener implements Listener {
    NamespacedKey key = new NamespacedKey(Main.getInstance(), "hopperFilter");

    @EventHandler
    public void inventoryMoveItem(InventoryMoveItemEvent event) {
        Inventory dest = event.getDestination();
        ItemStack item = event.getItem();
        if(dest.getType() != InventoryType.HOPPER) return;
        Block block = dest.getLocation().getBlock();
        BlockState blockState = block.getState();
        if(!(blockState instanceof TileState)) return;
        TileState tileState = (TileState) blockState;
        PersistentDataContainer container = tileState.getPersistentDataContainer();
        List<ItemStack> filter = Arrays.asList(container.get(key, DataType.ITEM_STACK_ARRAY));
        if(filter == null || filter.isEmpty()) return;
        if(!filter.contains(item)) event.setCancelled(true);
    }

    @EventHandler
    public void inventoryPickupItem(InventoryPickupItemEvent event) {
        Inventory dest = event.getInventory();
        ItemStack item = event.getItem().getItemStack();
        if(dest.getType() != InventoryType.HOPPER) return;
        Block block = dest.getLocation().getBlock();
        BlockState blockState = block.getState();
        if(!(blockState instanceof TileState)) return;
        TileState tileState = (TileState) blockState;
        PersistentDataContainer container = tileState.getPersistentDataContainer();
        ItemStack[] filter = container.get(key, DataType.ITEM_STACK_ARRAY);
        if(filter == null) return;
        for (ItemStack current : filter) {
            if (current.hasItemMeta() && item.hasItemMeta()) {
                if (current.getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName()) {
                    if (!current.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()) && current.getType().equals(item.getType()))
                        event.setCancelled(true);
                }
            } else if (!current.getType().equals(item.getType())) event.setCancelled(true);
        }
    }
}
