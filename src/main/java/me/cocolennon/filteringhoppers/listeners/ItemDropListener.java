package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
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
        if(!Main.getInstance().getConfig().getBoolean("chunk-collection-enabled")) return;
        ItemStack itemStack = item.getItemStack();
        List<TileState> tileStates = Helper.getHopperStates(item.getLocation().getChunk());
        if(tileStates.isEmpty()) return;
        for(TileState current : tileStates) {
            List<ItemStack> filter = Helper.getHopperFilter(current);
            if(filter == null || filter.isEmpty() || filter.stream().anyMatch(f -> f.isSimilar(itemStack))) {
                if(Helper.hopperIsFull(current.getLocation(), itemStack)) continue;
                HashMap<Integer, ItemStack> remainder = Helper.addItemToHopper(itemStack, current.getLocation());
                if(!remainder.isEmpty()) item.getItemStack().setAmount(remainder.get(0).getAmount());
                else item.remove();
                break;
            }
        }
    }
}
