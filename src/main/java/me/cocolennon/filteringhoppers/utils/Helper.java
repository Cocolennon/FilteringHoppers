package me.cocolennon.filteringhoppers.utils;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.cocolennon.filteringhoppers.Config;
import me.cocolennon.filteringhoppers.Main;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class Helper {
    private static NamespacedKey filterKey = new NamespacedKey(Main.getInstance(), "hopperFilter");
    private static NamespacedKey modeKey = new NamespacedKey(Main.getInstance(), "hopperMode");
    private static NamespacedKey filterTypeKey = new NamespacedKey(Main.getInstance(), "filterType");
    private static NamespacedKey filterMetaKey = new NamespacedKey(Main.getInstance(), "filterMeta");
    private static NamespacedKey filterEnchantedKey = new NamespacedKey(Main.getInstance(), "filterEnchanted");
    private static NamespacedKey destroyItems = new NamespacedKey(Main.getInstance(), "destroyItems");

    public static boolean shouldMoveItem(TileState hopperState, ItemStack itemStack, List<ItemStack> filter) {
        boolean useType = isFilterType(hopperState);
        boolean useMeta = isFilterMeta(hopperState);
        boolean useEnchants = isFilterEnchanted(hopperState);
        boolean matches = filter.stream().anyMatch(filterItem -> {
            if (!useType && !useMeta && !useEnchants) return filterItem.isSimilar(itemStack);
            if (useType && !matchType(filterItem, itemStack)) return false;
            if (useMeta && !matchMeta(filterItem, itemStack)) return false;
            if (useEnchants && !matchEnchanted(filterItem, itemStack)) return false;
            return true;
        });
        boolean shouldMove = isWhitelist(hopperState) == matches;
        if(shouldMove) MetricsUtil.allowedItems.incrementAndGet();
        else MetricsUtil.deniedItems.incrementAndGet();
        return shouldMove;
    }

    private static boolean matchType(ItemStack filterItem, ItemStack itemStack) {
        return filterItem.getType().equals(itemStack.getType());
    }

    private static boolean matchMeta(ItemStack filterItem, ItemStack itemStack) {
        if(!filterItem.hasItemMeta()) return !itemStack.hasItemMeta();
        if(!itemStack.hasItemMeta()) return false;
        return filterItem.getItemMeta().equals(itemStack.getItemMeta());
    }

    private static boolean matchEnchanted(ItemStack filterItem, ItemStack itemStack) {
        if(!filterItem.hasItemMeta()) return !itemStack.hasItemMeta();
        if(!itemStack.hasItemMeta()) return false;
        return filterItem.getItemMeta().hasEnchants() && itemStack.getItemMeta().hasEnchants();
    }

    public static List<ItemStack> getHopperFilter(TileState hopper) {
        PersistentDataContainer container = hopper.getPersistentDataContainer();
        ItemStack[] rawFilter = container.get(filterKey, DataType.ITEM_STACK_ARRAY);
        return rawFilter == null ? null : Arrays.asList(rawFilter);
    }

    public static void saveFilter(TileState hopperState, Inventory filterInventory) {
        PersistentDataContainer container = hopperState.getPersistentDataContainer();
        List<ItemStack> filter = new LinkedList<>();
        for (int i = 0; i < 18; i++) {
            ItemStack item = filterInventory.getItem(i);
            if (item == null) continue;
            filter.add(item);
        }
        ItemStack[] arrayFilter = filter.toArray(new ItemStack[0]);
        container.set(filterKey, DataType.ITEM_STACK_ARRAY, arrayFilter);
        hopperState.update();
    }

    public static HashMap<Integer, ItemStack> addItemToHopper(ItemStack itemStack, Location hopperLocation) {
        try {
            Hopper hopper = (Hopper) hopperLocation.getBlock().getState();
            HashMap<Integer, ItemStack> remainder = hopper.getSnapshotInventory().addItem(itemStack);
            hopper.update();
            return remainder;
        } catch (ClassCastException | ConcurrentModificationException ignored) {}
        return new HashMap<>();
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

    public static List<TileState> getHopperStates(Location location) {
        Config config = Main.getInstance().config();
        return switch (config.itemCollection.mode.toLowerCase()) {
            case "chunk" -> getHopperStates(location.getChunk());
            case "radius" -> getHopperStates(location, config.itemCollection.radius);
            default -> Collections.emptyList();
        };
    }

    public static List<TileState> getHopperStates(Chunk chunk) {
        List<TileState> tileStates = new ArrayList<>();
        for(BlockState current : chunk.getTileEntities()) {
            if(!(current instanceof TileState tileState)) continue;
            if(!(current instanceof Hopper hopper)) continue;
            if(hopper.getBlock().getType() != Material.HOPPER) continue;
            tileStates.add(tileState);
        }
        return tileStates;
    }

    private static List<TileState> getHopperStates(Location center, int radius) {
        Config config = Main.getInstance().config();
        List<TileState> tileStates = new ArrayList<>();
        World world = center.getWorld();
        if(world == null) return tileStates;
        int minChunkX = (center.getBlockX() - radius) >> 4;
        int maxChunkX = (center.getBlockX() + radius) >> 4;
        int minChunkZ = (center.getBlockZ() - radius) >> 4;
        int maxChunkZ = (center.getBlockZ() + radius) >> 4;
        double radiusSquared =  radius * radius;
        Location centerLoc = center.clone();
        if(config.itemCollection.ignoreY) centerLoc.setY(0);
        for(int cx = minChunkX; cx <= maxChunkX; cx++) {
            for(int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                if(!world.isChunkLoaded(cx, cz)) continue;
                Chunk chunk = world.getChunkAt(cx, cz);
                for(BlockState current : chunk.getTileEntities()) {
                    if(!(current instanceof TileState tileState)) continue;
                    if(!(current instanceof Hopper hopper)) continue;
                    if(hopper.getBlock().getType() != Material.HOPPER) continue;
                    Location currentLoc = current.getLocation().clone();
                    if(config.itemCollection.ignoreY) currentLoc.setY(0);
                    if(currentLoc.distanceSquared(centerLoc) <= radiusSquared) tileStates.add(tileState);
                }
            }
        }
        return tileStates;
    }

    public static boolean isWhitelist(TileState hopper) {
        PersistentDataContainer container = hopper.getPersistentDataContainer();
        return container.has(modeKey) ? container.get(modeKey, DataType.BOOLEAN) : true;
    }

    public static void toggleWhitelistMode(TileState hopper) {
        PersistentDataContainer container = hopper.getPersistentDataContainer();
        container.set(modeKey, DataType.BOOLEAN, !isWhitelist(hopper));
        hopper.update();
    }

    public static boolean shouldDestroy(TileState hopper) {
        PersistentDataContainer container = hopper.getPersistentDataContainer();
        return container.has(destroyItems) ? container.get(destroyItems, PersistentDataType.BOOLEAN) : false;
    }

    public static void toggleDestroy(TileState hopper) {
        PersistentDataContainer container = hopper.getPersistentDataContainer();
        container.set(destroyItems, PersistentDataType.BOOLEAN, !shouldDestroy(hopper));
        hopper.update();
    }

    public static boolean isFilterType(TileState hopper) {
        PersistentDataContainer container = hopper.getPersistentDataContainer();
        return container.has(filterTypeKey) ? container.get(filterTypeKey, DataType.BOOLEAN) : true;
    }

    public static void toggleFilterType(TileState hopper) {
        PersistentDataContainer container = hopper.getPersistentDataContainer();
        container.set(filterTypeKey, DataType.BOOLEAN, !isFilterType(hopper));
        hopper.update();
    }

    public static boolean isFilterMeta(TileState hopper) {
        PersistentDataContainer container = hopper.getPersistentDataContainer();
        return container.has(filterMetaKey) ? container.get(filterMetaKey, DataType.BOOLEAN) : false;
    }

    public static void toggleFilterMeta(TileState hopper) {
        PersistentDataContainer container = hopper.getPersistentDataContainer();
        container.set(filterMetaKey, DataType.BOOLEAN, !isFilterMeta(hopper));
        hopper.update();
    }

    public static boolean isFilterEnchanted(TileState hopper) {
        PersistentDataContainer container = hopper.getPersistentDataContainer();
        return container.has(filterEnchantedKey) ? container.get(filterEnchantedKey, DataType.BOOLEAN) : false;
    }

    public static void toggleFilterEnchanted(TileState hopper) {
        PersistentDataContainer container = hopper.getPersistentDataContainer();
        container.set(filterEnchantedKey, DataType.BOOLEAN, !isFilterEnchanted(hopper));
        hopper.update();
    }
}
