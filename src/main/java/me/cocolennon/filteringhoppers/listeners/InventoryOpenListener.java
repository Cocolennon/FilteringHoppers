package me.cocolennon.filteringhoppers.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.MenuCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

public class InventoryOpenListener implements Listener {
    @EventHandler
    public void inventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        if(inventory.getType() != InventoryType.HOPPER) return;
        if(!player.isSneaking()) return;
        Block block = inventory.getLocation().getBlock();
        if(block.getType() != Material.HOPPER) return;
        BlockState blockState = block.getState();
        if(!(blockState instanceof TileState)) return;
        TileState tileState = (TileState) blockState;
        PersistentDataContainer container = tileState.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "hopperFilter");
        ItemStack[] filter = container.get(key, DataType.ITEM_STACK_ARRAY);
        MenuCreator.getInstance().createFilterMenu(filter, player, block);
        event.setCancelled(true);
    }
}
