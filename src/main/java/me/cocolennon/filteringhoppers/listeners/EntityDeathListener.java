package me.cocolennon.filteringhoppers.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.cocolennon.filteringhoppers.Main;
import org.bukkit.Chunk;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityDeathListener implements Listener {
    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        if(!Main.getInstance().getConfig().getBoolean("chunk-collection-enabled")) return;
        List<ItemStack> items = event.getDrops();
        LivingEntity entity = event.getEntity();
        Chunk itemLocation = entity.getLocation().getChunk();

        List<TileState> tileStates = new ArrayList<>();
        for(BlockState current : itemLocation.getTileEntities()) {
            if(!(current instanceof TileState)) return;
            TileState currentTileState = (TileState) current;
            if(tileStates.contains(currentTileState)) return;
            tileStates.add(currentTileState);
        }
        if(tileStates.isEmpty()) return;
        for(TileState current : tileStates) {
            PersistentDataContainer container = current.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "hopperFilter");
            List<ItemStack> filter = Arrays.asList(container.get(key, DataType.ITEM_STACK_ARRAY));
            for(ItemStack currentItemInDrops : items) {
                if(filter == null || filter.size() == 0 || filter.contains(currentItemInDrops)) {
                    try {
                        Hopper hopper = (Hopper) current.getLocation().getBlock().getState();
                        hopper.getSnapshotInventory().addItem(currentItemInDrops);
                        items.remove(currentItemInDrops);
                        hopper.update();
                        return;
                    } catch (ClassCastException exception) {
                        break;
                    }
                }
            }
        }
    }
}
