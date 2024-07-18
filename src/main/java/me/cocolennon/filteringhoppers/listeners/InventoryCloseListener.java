package me.cocolennon.filteringhoppers.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.cocolennon.filteringhoppers.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.LinkedList;
import java.util.List;

public class InventoryCloseListener implements Listener {
    @EventHandler
    public void inventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        if(!event.getView().getTitle().equals("§5Filtering Hoppers§f: §dFilter Menu")) return;
        Block block = inventory.getLocation().getBlock();
        if(block == null) return;
        if(block.getType() != Material.HOPPER) return;
        BlockState blockState = block.getState();
        if(!(blockState instanceof TileState)) return;
        TileState tileState = (TileState) blockState;
        PersistentDataContainer container = tileState.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "hopperFilter");
        List<ItemStack> filter = new LinkedList<>();
        for (int i = 0; i < 18; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) continue;
            filter.add(item);
        }
        ItemStack[] arrayFilter = filter.toArray(new ItemStack[0]);
        container.set(key, DataType.ITEM_STACK_ARRAY, arrayFilter);
        tileState.update();
        player.sendMessage("§d[§5Filtering Hoppers§d] Successfully closed and saved the filter menu.");
    }
}
