package me.cocolennon.filteringhoppers.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.cocolennon.filteringhoppers.Main;
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
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Arrays;
import java.util.List;

public class InventoryMoveItemListener implements Listener {
    NamespacedKey key = new NamespacedKey(Main.getInstance(), "hopperFilter");

    @EventHandler
    public void inventoryMoveItem(InventoryMoveItemEvent event) {
        Inventory dest = event.getDestination();
        Inventory init = event.getInitiator();
        Inventory src = event.getSource();
        ItemStack item = event.getItem();
        if(dest.getType() != InventoryType.HOPPER) return;
        Block block = dest.getLocation().getBlock();
        BlockState blockState = block.getState();
        if(blockState.getBlock().getType() != Material.HOPPER) return;
        if(!(blockState instanceof TileState)) return;
        TileState tileState = (TileState) blockState;
        PersistentDataContainer container = tileState.getPersistentDataContainer();
        ItemStack[] arrayFilter = container.get(key, DataType.ITEM_STACK_ARRAY);
        if(arrayFilter == null) return;
        List<ItemStack> filter = Arrays.asList(arrayFilter);
        if(filter == null || filter.isEmpty()) return;
        for(ItemStack filterItem : filter) {
            if(filterItem.isSimilar(item)) return;
            /*if(!src.contains(filterItem.getType())) continue;
            int slot = src.first(filterItem.getType());
            src.getItem(slot).setAmount(src.getItem(slot).getAmount()-1);
            init.addItem(new ItemStack(filterItem.getType(), 1));
            event.setCancelled(true);
            return;*/
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void inventoryPickupItem(InventoryPickupItemEvent event) {
        Inventory dest = event.getInventory();
        ItemStack item = event.getItem().getItemStack();
        if(dest.getType() != InventoryType.HOPPER) return;
        Block block = dest.getLocation().getBlock();
        BlockState blockState = block.getState();
        if(blockState.getBlock().getType() != Material.HOPPER) return;
        if(!(blockState instanceof TileState)) return;
        TileState tileState = (TileState) blockState;
        PersistentDataContainer container = tileState.getPersistentDataContainer();
        ItemStack[] arrayFilter = container.get(key, DataType.ITEM_STACK_ARRAY);
        if(arrayFilter == null) return;
        List<ItemStack> filter = Arrays.asList(arrayFilter);
        if(filter == null || filter.isEmpty()) return;
        for(ItemStack filterItem : filter) if(!filterItem.isSimilar(item)) {
            event.setCancelled(true);
            break;
        }
    }
}
