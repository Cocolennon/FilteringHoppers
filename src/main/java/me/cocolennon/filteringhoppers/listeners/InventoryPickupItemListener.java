package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.utils.Helper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryPickupItemListener implements Listener {
    @EventHandler
    public void inventoryPickupItem(InventoryPickupItemEvent event) {
        if(itemPickup(event.getInventory(), event.getItem().getItemStack())) event.setCancelled(true);
    }

    private boolean itemPickup(Inventory dest, ItemStack item) {
        if(dest.getType() != InventoryType.HOPPER) return false;
        Block block = dest.getLocation().getBlock();
        BlockState blockState = block.getState();
        if(block.getType() != Material.HOPPER || !(blockState instanceof TileState tileState)) return false;
        List<ItemStack> filter = Helper.getHopperFilter(tileState);
        if(filter == null || filter.isEmpty()) return false;
        if(Helper.shouldMoveItem(item, filter, Helper.isWhitelist(tileState))) return true;
        return false;
    }
}
