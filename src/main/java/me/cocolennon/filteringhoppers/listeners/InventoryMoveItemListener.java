package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Config;
import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class InventoryMoveItemListener implements Listener {
    @EventHandler(priority =  EventPriority.HIGH)
    public void inventoryMoveItem(InventoryMoveItemEvent event) {
        Inventory dest = event.getDestination();
        if(dest.getType() != InventoryType.HOPPER) return;
        Block block = dest.getLocation().getBlock();
        BlockState blockState = block.getState();
        if(block.getType() != Material.HOPPER || !(blockState instanceof TileState tileState)) return;
        List<ItemStack> filter = Helper.getHopperFilter(tileState);
        if(filter == null || filter.isEmpty()) return;
        Config config = Main.getInstance().config();
        Inventory source = event.getSource();
        for(int slot = 0; slot < source.getSize(); slot++) {
            ItemStack itemStack = source.getItem(slot);
            if(itemStack == null) continue;
            if(!Helper.shouldMoveItem(tileState, itemStack, filter)) {
                if(Helper.shouldDestroy(tileState)) itemStack.setAmount(0);
                continue;
            }
            if(getFirstOccupiedSlot(source) == slot) return;
            ItemStack cloned = itemStack.clone();
            cloned.setAmount(config.hopperRate);
            HashMap<Integer, ItemStack> remainder = dest.addItem(cloned);
            if(!remainder.isEmpty()) itemStack.setAmount(itemStack.getAmount() - (config.hopperRate - remainder.values().iterator().next().getAmount()));
            source.setItem(slot, itemStack);
            break;
        }
        event.setCancelled(true);
    }

    private static int getFirstOccupiedSlot(Inventory inventory) {
        for(int i = 0; i < inventory.getSize(); i++) if(inventory.getItem(i) != null) return i;
        return 0;
    }
}
