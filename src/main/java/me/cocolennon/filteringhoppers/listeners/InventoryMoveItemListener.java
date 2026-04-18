package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import org.bukkit.Material;
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

import java.util.List;

public class InventoryMoveItemListener implements Listener {
    NamespacedKey key = new NamespacedKey(Main.getInstance(), "hopperFilter");

    @EventHandler
    public void inventoryMoveItem(InventoryMoveItemEvent event) {
        if(itemMove(event.getDestination(), event.getItem())) event.setCancelled(true);
    }

    @EventHandler
    public void inventoryPickupItem(InventoryPickupItemEvent event) {
        if(itemMove(event.getInventory(), event.getItem().getItemStack())) event.setCancelled(true);
    }

    private boolean itemMove(Inventory dest, ItemStack item) {
        if(dest.getType() != InventoryType.HOPPER) return false;
        Block block = dest.getLocation().getBlock();
        BlockState blockState = block.getState();
        if(blockState.getBlock().getType() != Material.HOPPER) return false;
        if(!(blockState instanceof TileState)) return false;
        List<ItemStack> filter = Helper.getHopperFilter((TileState) blockState);
        if(filter == null || filter.isEmpty()) return false;
        if(filter.stream().noneMatch(f -> f.isSimilar(item))) return true;
        return false;
    }
}
