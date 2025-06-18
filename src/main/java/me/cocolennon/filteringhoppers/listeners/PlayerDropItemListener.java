package me.cocolennon.filteringhoppers.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.cocolennon.filteringhoppers.Main;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

public class PlayerDropItemListener implements Listener {
    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        if(!Main.getInstance().getConfig().getBoolean("chunk-collection-enabled")) return;
        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();
        Chunk itemLocation = item.getLocation().getChunk();

        List<TileState> tileStates = new ArrayList<>();
        for(BlockState current : itemLocation.getTileEntities()) {
            if(!(current instanceof TileState)) continue;
            if(current.getBlock().getType() != Material.HOPPER) continue;
            TileState currentTileState = (TileState) current;
            if(tileStates.contains(currentTileState)) continue;
            tileStates.add(currentTileState);
        }
        if(tileStates.isEmpty()) return;
        for(TileState current : tileStates) {
            PersistentDataContainer container = current.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "hopperFilter");
            ItemStack[] arrayFilter = container.get(key, DataType.ITEM_STACK_ARRAY);
            if(arrayFilter == null) return;
            List<ItemStack> filter = Arrays.asList(arrayFilter);
            boolean filterEmpty = false;
            if(filter == null || filter.isEmpty()) filterEmpty = true;
            for(ItemStack filterItem : filter) {
                if(filterEmpty || filterItem.isSimilar(itemStack)) {
                    try {
                        Hopper hopper = (Hopper) current.getLocation().getBlock().getState();
                        hopper.getSnapshotInventory().addItem(itemStack);
                        item.remove();
                        hopper.update();
                    } catch (ClassCastException | ConcurrentModificationException ignored) {}
                }
            }
        }
    }
}
