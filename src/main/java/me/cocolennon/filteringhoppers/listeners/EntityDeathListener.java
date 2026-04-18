package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EntityDeathListener implements Listener {
    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        if(!Main.getInstance().getConfig().getBoolean("chunk-collection-enabled")) return;
        List<ItemStack> items = event.getDrops();
        List<TileState> tileStates = Helper.getHopperStates(event.getEntity().getChunk());
        if(tileStates.isEmpty()) return;
        hopperLoop:for(TileState current : tileStates) {
            List<ItemStack> filter = Helper.getHopperFilter(current);
            Iterator<ItemStack> dropIterator = items.iterator();
            while(dropIterator.hasNext()) {
                ItemStack itemStack = dropIterator.next();
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
