package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import org.bukkit.Bukkit;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EntityDeathListener implements Listener {
    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        if(!Main.getInstance().config().itemCollection.enabled) return;
        Bukkit.getRegionScheduler().execute(Main.getInstance(), event.getEntity().getLocation(), () -> {
            List<ItemStack> items = new ArrayList<>(event.getDrops());
            Iterator<ItemStack> iterator = items.iterator();
            List<TileState> tileStates = Helper.getHopperStates(event.getEntity().getLocation());
            if(tileStates.isEmpty()) return;
            itemLoop:while(iterator.hasNext()) {
                ItemStack itemStack = iterator.next();
                for(TileState tileState : tileStates) {
                    List<ItemStack> filter = Helper.getHopperFilter(tileState);
                    if(filter.isEmpty() || Helper.shouldMoveItem(tileState, itemStack, filter)) {
                        if(Helper.hopperIsFull(tileState.getLocation(), itemStack)) continue;
                        HashMap<Integer, ItemStack> remainder = Helper.addItemToHopper(itemStack, tileState.getLocation());
                        if(!remainder.isEmpty()) itemStack.setAmount(remainder.values().iterator().next().getAmount());
                        else {
                            items.remove(itemStack);
                            itemStack.setAmount(0);
                        }
                    }else if(Helper.shouldDestroy(tileState)) {
                        items.remove(itemStack);
                        itemStack.setAmount(0);
                    }
                    continue itemLoop;
                }
            }
        });
    }
}
