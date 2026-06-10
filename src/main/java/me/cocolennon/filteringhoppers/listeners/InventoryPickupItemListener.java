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
        Inventory dest = event.getInventory();
        if(dest.getType() != InventoryType.HOPPER) return;
        Block block = dest.getLocation().getBlock();
        BlockState blockState = block.getState();
        if(block.getType() != Material.HOPPER || !(blockState instanceof TileState tileState)) return;
        List<ItemStack> filter = Helper.getHopperFilter(tileState);
        if(filter == null || filter.isEmpty()) return;
        ItemStack itemStack = event.getItem().getItemStack();
        if(Helper.shouldMoveItem(itemStack, filter, Helper.isWhitelist(tileState))) event.setCancelled(true);
    }
}
