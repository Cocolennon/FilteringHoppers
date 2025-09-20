package me.cocolennon.filteringhoppers.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.cocolennon.filteringhoppers.Main;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntityDeathListener implements Listener {
    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        if(!Main.getInstance().getConfig().getBoolean("chunk-collection-enabled")) return;
        List<ItemStack> items = event.getDrops();
        List<TileState> tileStates = getTileStates(event);
        if(tileStates.isEmpty()) return;
        for(TileState current : tileStates) {
            PersistentDataContainer container = current.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "hopperFilter");
            ItemStack[] arrayFilter = container.get(key, DataType.ITEM_STACK_ARRAY);
            if(arrayFilter == null) return;
            List<ItemStack> filter = Arrays.asList(arrayFilter);
            boolean filterEmpty = false;
            if(filter == null || filter.isEmpty()) filterEmpty = true;
            Iterator<ItemStack> dropIterator = items.iterator();
            while(dropIterator.hasNext()) {
                ItemStack itemStack = dropIterator.next();
                boolean match = filterEmpty || filter.stream().anyMatch(f -> f.isSimilar(itemStack));
                if(match) {
                    try {
                        Hopper hopper = (Hopper) current.getLocation().getBlock().getState();
                        hopper.getSnapshotInventory().addItem(itemStack);
                        dropIterator.remove();
                        hopper.update();
                    } catch (ClassCastException|ConcurrentModificationException ignored) {}
                }
            }
        }
    }

    @NotNull
    private static List<TileState> getTileStates(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Chunk itemLocation = entity.getLocation().getChunk();

        List<TileState> tileStates = new ArrayList<>();
        for(BlockState current : itemLocation.getTileEntities()) {
            if(!(current instanceof TileState)) continue;
            if(current.getBlock().getType() != Material.HOPPER) continue;
            TileState currentTileState = (TileState) current;
            if(tileStates.contains(currentTileState)) continue;
            tileStates.add(currentTileState);
        }
        return tileStates;
    }
}
