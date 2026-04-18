package me.cocolennon.filteringhoppers.utils;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.cocolennon.filteringhoppers.Main;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

public class Helper {
    private static NamespacedKey filterKey = new NamespacedKey(Main.getInstance(), "hopperFilter");

    public static List<ItemStack> getHopperFilter(TileState hopper) {
        PersistentDataContainer container = hopper.getPersistentDataContainer();
        ItemStack[] rawFilter = container.get(filterKey, DataType.ITEM_STACK_ARRAY);
        return rawFilter == null ? null : Arrays.asList(rawFilter);
    }

    public static HashMap<Integer, ItemStack> addItemToHopper(ItemStack itemStack, Location hopperLocation) {
        try {
            Hopper hopper = (Hopper) hopperLocation.getBlock().getState();
            HashMap<Integer, ItemStack> remainder = hopper.getSnapshotInventory().addItem(itemStack);
            hopper.update();
            return remainder;
        } catch (ClassCastException | ConcurrentModificationException ignored) {}
        return null;
    }

    public static boolean hopperIsFull(Location hopperLocation, ItemStack itemStack) {
        try {
            Hopper hopper = (Hopper) hopperLocation.getBlock().getState();
            for(ItemStack inventoryItem : hopper.getSnapshotInventory().getContents()) {
                if(inventoryItem == null) return false;
                if(inventoryItem.isSimilar(itemStack) && inventoryItem.getAmount() < inventoryItem.getMaxStackSize()) return false;
            }
        }catch(ClassCastException | ConcurrentModificationException ignored) {}
        return true;
    }

    public static List<TileState> getHopperStates(Chunk chunk) {
        List<TileState> tileStates = new ArrayList<>();
        for(BlockState current : chunk.getTileEntities()) {
            if(!(current instanceof TileState)) continue;
            if(current.getBlock().getType() != Material.HOPPER) continue;
            TileState currentTileState = (TileState) current;
            tileStates.add(currentTileState);
        }
        return tileStates;
    }
}
