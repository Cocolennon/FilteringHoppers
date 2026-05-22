package me.cocolennon.filteringhoppers.utils;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.cocolennon.filteringhoppers.Config;
import me.cocolennon.filteringhoppers.Main;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class Helper {
    private static Config config = Main.getInstance().config();
    private static NamespacedKey filterKey = new NamespacedKey(Main.getInstance(), "hopperFilter");
    private static NamespacedKey modeKey = new NamespacedKey(Main.getInstance(), "hopperMode");
    private static NamespacedKey buttonAction = new NamespacedKey(Main.getInstance(), "buttonAction");

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

    public static List<TileState> getHopperStates(Location location) {
        return switch (config.itemCollection.mode.toLowerCase()) {
            case "chunk" -> getHopperStates(location.getChunk());
            case "radius" -> getHopperStates(location, config.itemCollection.radius);
            default -> null;
        };
    }

    private static List<TileState> getHopperStates(Chunk chunk) {
        List<TileState> tileStates = new ArrayList<>();
        for(BlockState current : chunk.getTileEntities()) {
            if(!(current instanceof TileState tileState)) continue;
            if(current.getBlock().getType() != Material.HOPPER) continue;
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
                    if(current.getBlock().getType() != Material.HOPPER) continue;
                    Location currentLoc = current.getLocation().clone();
                    if(config.itemCollection.ignoreY) currentLoc.setY(0);
                    if(currentLoc.distanceSquared(centerLoc) <= radiusSquared) tileStates.add(tileState);
                }
            }
        }
        return tileStates;
    }

    public static boolean isBoolean(String s) {
        return "true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s);
    }

    public static boolean hasPermission(Player player, String permission) {
        boolean allowed = player.hasPermission(permission);
        if(!allowed) player.sendMessage(Localization.get(player, "error.permission", true));
        return allowed;
    }

    public static int getHopperRate() {
        File spigotFile = new File(Bukkit.getServer().getWorldContainer().getParentFile(), "spigot.yml");
        YamlConfiguration spigotConfig = YamlConfiguration.loadConfiguration(spigotFile);
        return spigotConfig.getInt("world-settings.default.hopper-amount", 1);
    }

    public static boolean isWhitelist(TileState hopper) {
        PersistentDataContainer container = hopper.getPersistentDataContainer();
        return container.get(modeKey, DataType.BOOLEAN);
    }

    public static void setButtonAction(ItemMeta meta, String action) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(buttonAction, PersistentDataType.STRING, action);
    }
}
