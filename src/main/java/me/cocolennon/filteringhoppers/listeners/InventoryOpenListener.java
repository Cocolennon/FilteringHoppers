package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.utils.Helper;
import me.cocolennon.filteringhoppers.utils.MenuUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class InventoryOpenListener implements Listener {
    @EventHandler
    public void inventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        if(inventory.getType() != InventoryType.HOPPER) return;
        Player player = (Player) event.getPlayer();
        if(!player.isSneaking()) return;
        Block block = inventory.getLocation().getBlock();
        if(block.getType() != Material.HOPPER) return;
        event.setCancelled(true);
        TileState hopperState = (TileState) block.getState();
        MenuUtil.createFilterMenu(Helper.getHopperFilter(hopperState), player, block, Helper.isWhitelist(hopperState), Helper.shouldDestroy(hopperState));
    }
}