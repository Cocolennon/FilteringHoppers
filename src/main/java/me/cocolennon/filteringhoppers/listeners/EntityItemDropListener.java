package me.cocolennon.filteringhoppers.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.cocolennon.filteringhoppers.Main;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityItemDropListener implements Listener {
    @EventHandler
    public void entityItemDrop(EntityDropItemEvent event) {
        if(!Main.getInstance().getConfig().getBoolean("chunk-collection-enabled")) return;
        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();
        Chunk itemLocation = item.getLocation().getChunk();

        List<TileState> tileStates = new ArrayList<>();
        for(BlockState current : itemLocation.getTileEntities()) {
            if(!(current instanceof TileState)) return;
            TileState currentTileState = (TileState) current;
            tileStates.add(currentTileState);
        }
        if(tileStates.isEmpty()) return;
        for(TileState current : tileStates) {
            PersistentDataContainer container = current.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "hopperFilter");
            List<ItemStack> filter = Arrays.asList(container.get(key, DataType.ITEM_STACK_ARRAY));
            if(filter == null || filter.isEmpty() || filter.contains(itemStack)) {
                try {
                    Hopper hopper = (Hopper) current.getLocation().getBlock().getState();
                    hopper.getSnapshotInventory().addItem(itemStack);
                    item.remove();
                    hopper.update();
                    break;
                }catch(ClassCastException exception) {
                    break;
                }
            }
        }
    }
}
