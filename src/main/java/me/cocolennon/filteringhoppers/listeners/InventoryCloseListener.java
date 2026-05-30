package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.utils.FilterInventoryHolder;
import me.cocolennon.filteringhoppers.utils.Helper;
import me.cocolennon.filteringhoppers.utils.Localization;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class InventoryCloseListener implements Listener {
    @EventHandler
    public void inventoryClose(InventoryCloseEvent event) {
        Inventory filterInventory = event.getInventory();
        if(!(filterInventory.getHolder() instanceof FilterInventoryHolder inventoryHolder)) return;
        Block block = inventoryHolder.getBlock();
        if(block == null || block.getType() != Material.HOPPER) return;
        BlockState blockState = block.getState();
        if(!(blockState instanceof TileState hopperState)) return;
        Helper.saveFilter(hopperState, filterInventory);
        Player player = (Player) event.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_BARREL_CLOSE, 1.0f, 1.0f);
        player.sendMessage(Localization.get(player, "filter.save", true));
    }
}
