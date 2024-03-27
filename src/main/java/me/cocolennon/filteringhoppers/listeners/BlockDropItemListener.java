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
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockDropItemListener implements Listener {
    @EventHandler
    public void blockDropItem(BlockDropItemEvent event) {
        List<Item> items = event.getItems();

        List<TileState> tileStates = new ArrayList<>();
        for(Item item : items) {
            ItemStack itemStack = item.getItemStack();
            if(itemStack.getType().equals(Material.HOPPER)) {
                BlockState blockState = event.getBlockState();
                if(blockState instanceof TileState) {
                    TileState tileState = (TileState) blockState;
                    if(tileState instanceof Hopper) {
                        PersistentDataContainer container = tileState.getPersistentDataContainer();
                        NamespacedKey key = new NamespacedKey(Main.getInstance(), "lore");
                        if(container.has(key, DataType.STRING_ARRAY)) {
                            String[] lore = container.get(key, DataType.STRING_ARRAY);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setLore(Arrays.stream(lore).toList());
                            itemStack.setItemMeta(itemMeta);
                        }
                    }
                }
            }
            if(!Main.getInstance().getConfig().getBoolean("chunk-collection-enabled")) return;
            Chunk itemLocation = item.getLocation().getChunk();
            for(BlockState current : itemLocation.getTileEntities()) {
                if(!(current instanceof TileState)) return;
                TileState currentTileState = (TileState) current;
                if(tileStates.contains(currentTileState)) return;
                tileStates.add(currentTileState);
            }
        }
        if(tileStates.isEmpty()) return;
        for(TileState current : tileStates) {
            PersistentDataContainer container = current.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "hopperFilter");
            ItemStack[] filter = container.get(key, DataType.ITEM_STACK_ARRAY);
            if(filter == null || filter.length == 0) {
                for(Item currentItemInDrops : items) {
                    try {
                        ItemStack itemStack = currentItemInDrops.getItemStack();
                        Hopper hopper = (Hopper) current.getLocation().getBlock().getState();
                        hopper.getSnapshotInventory().addItem(itemStack);
                        currentItemInDrops.remove();
                        hopper.update();
                        return;
                    } catch (ClassCastException exception) {
                        break;
                    }
                }
            }
            for (ItemStack currentItem : filter) {
                for (Item currentItemInDrops : items) {
                    ItemStack itemStack = currentItemInDrops.getItemStack();
                    if (currentItem.hasItemMeta() && itemStack.hasItemMeta()) {
                        if (currentItem.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasDisplayName()) {
                            if (currentItem.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName()) && currentItem.getType().equals(itemStack.getType())) {
                                try {
                                    Hopper hopper = (Hopper) current.getLocation().getBlock().getState();
                                    hopper.getSnapshotInventory().addItem(itemStack);
                                    currentItemInDrops.remove();
                                    hopper.update();
                                    return;
                                } catch (ClassCastException exception) {
                                    break;
                                }
                            }
                        }
                    } else if (currentItem.getType().equals(itemStack.getType())) {
                        try {
                            Hopper hopper = (Hopper) current.getLocation().getBlock().getState();
                            hopper.getSnapshotInventory().addItem(itemStack);
                            currentItemInDrops.remove();
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
}
