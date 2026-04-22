package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BlockDropItemListener implements Listener {
    @EventHandler
    public void blockDropItem(BlockDropItemEvent event) {
        if(!Main.getInstance().getConfig().getBoolean("chunk-collection-enabled")) return;
        List<Item> items = event.getItems();
        List<TileState> tileStates = Helper.getHopperStates(event.getBlock().getLocation());
        if(tileStates.isEmpty()) return;
        hopperLoop:for(TileState current : tileStates) {
            List<ItemStack> filter = Helper.getHopperFilter(current);
            Iterator<Item> dropIterator = items.iterator();
            while(dropIterator.hasNext()) {
                Item currentInDrops = dropIterator.next();
                ItemStack itemStack = currentInDrops.getItemStack();
                if(filter == null || filter.isEmpty() || filter.stream().anyMatch(f -> f.isSimilar(itemStack))) {
                    if(Helper.hopperIsFull(current.getLocation(), itemStack)) continue hopperLoop;
                    HashMap<Integer, ItemStack> remainder = Helper.addItemToHopper(itemStack, current.getLocation());
                    if(!remainder.isEmpty()) itemStack.setAmount(remainder.get(0).getAmount());
                    else dropIterator.remove();
                }
            }
        }
    }
}
