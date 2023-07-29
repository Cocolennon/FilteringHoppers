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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.List;

public class PlayerDropItemListener implements Listener {
    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();
        Chunk itemLocation = item.getLocation().getChunk();

        List<TileState> tileStates = new ArrayList<>();
        for(BlockState current : itemLocation.getTileEntities()) {
            if(!(current instanceof TileState)) return;
            TileState currentTileState = (TileState) current;
            tileStates.add(currentTileState);
        }
        if(tileStates.size() == 0) return;
        for(TileState current : tileStates) {
            PersistentDataContainer container = current.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "hopperFilter");
            ItemStack[] filter = container.get(key, DataType.ITEM_STACK_ARRAY);
            if(filter == null || filter.length == 0) {
                try {
                    Hopper hopper = (Hopper) current.getLocation().getBlock().getState();
                    hopper.getSnapshotInventory().addItem(itemStack);
                    item.remove();
                    hopper.update();
                }catch(ClassCastException exception) {
                    break;
                }
            }
            for(int i = 0; i < filter.length; i++) {
                ItemStack currentItem = filter[i];
                if(currentItem.hasItemMeta() && itemStack.hasItemMeta()) {
                    if(currentItem.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasDisplayName()) {
                        if(currentItem.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName()) && currentItem.getType().equals(itemStack.getType())) {
                            try {
                                Hopper hopper = (Hopper) current.getLocation().getBlock().getState();
                                hopper.getSnapshotInventory().addItem(itemStack);
                                item.remove();
                                hopper.update();
                            }catch(ClassCastException exception) {
                                break;
                            }
                        }
                    }
                }else if(currentItem.getType().equals(itemStack.getType())) {
                    try {
                        Hopper hopper = (Hopper) current.getLocation().getBlock().getState();
                        hopper.getSnapshotInventory().addItem(itemStack);
                        item.remove();
                        hopper.update();
                    }catch(ClassCastException exception) {
                        break;
                    }
                }
            }
        }
    }
}
