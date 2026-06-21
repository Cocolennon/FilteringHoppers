package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import org.bukkit.Bukkit;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class ItemDropListener implements Listener {
    @EventHandler
    public void entityItemDrop(EntityDropItemEvent event) {
        itemDrop(event.getItemDrop());
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        itemDrop(event.getItemDrop());
    }

    private void itemDrop(Item item) {
        Main main = Main.getInstance();
        if(!main.config().itemCollection.enabled) return;
        Bukkit.getRegionScheduler().execute(main, item.getLocation(), () -> {
            List<TileState> tileStates = Helper.getHopperStates(item.getLocation());
            if(tileStates.isEmpty()) return;
            ItemStack itemStack = item.getItemStack();
            for(TileState tileState : tileStates) {
                List<ItemStack> filter = Helper.getHopperFilter(tileState);
                if(filter.isEmpty() || Helper.shouldMoveItem(tileState, itemStack, filter)) {
                    if(Helper.hopperIsFull(tileState.getLocation(), itemStack)) continue;
                    int moveAmount = Math.min(itemStack.getAmount(), main.config().hopperRate);
                    HashMap<Integer, ItemStack> remainder = Helper.addItemToHopper(itemStack, tileState.getLocation());
                    if(!remainder.isEmpty()) itemStack.setAmount(itemStack.getAmount() - (moveAmount - remainder.values().iterator().next().getAmount()));
                    else item.remove();
                    break;
                }else if(Helper.shouldDestroy(tileState)) itemStack.setAmount(0);
            }
        });
    }
}
