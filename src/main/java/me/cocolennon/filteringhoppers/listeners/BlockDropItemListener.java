package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import org.bukkit.Bukkit;
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
        Main main = Main.getInstance();
        if(!main.config().itemCollection.enabled) return;
        List<Item> items = new ArrayList<>(event.getItems());
        Bukkit.getRegionScheduler().execute(main, event.getBlock().getLocation(), () -> {
            List<TileState> tileStates = Helper.getHopperStates(event.getBlock().getLocation());
            if(tileStates.isEmpty()) return;
            hopperLoop:for(TileState tileState : tileStates) {
                List<ItemStack> filter = Helper.getHopperFilter(tileState);
                for(Item drop : items) {
                    ItemStack itemStack = drop.getItemStack();
                    if(filter.isEmpty() || Helper.shouldMoveItem(tileState, itemStack, filter)) {
                        if(Helper.hopperIsFull(tileState.getLocation(), itemStack)) continue hopperLoop;
                        int moveAmount = Math.min(itemStack.getAmount(), main.config().hopperRate);
                        HashMap<Integer, ItemStack> remainder = Helper.addItemToHopper(itemStack, tileState.getLocation());
                        if(!remainder.isEmpty()) itemStack.setAmount(itemStack.getAmount() - (moveAmount - remainder.values().iterator().next().getAmount()));
                        else drop.remove();
                    }else if (Helper.shouldDestroy(tileState)) drop.remove();
                }
            }
        });
    }
}
