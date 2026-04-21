package me.cocolennon.filteringhoppers.utils;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.cocolennon.filteringhoppers.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

public class Helper {
    private static MiniMessage miniMessage = Main.getMiniMessage();
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
            if(!(current instanceof TileState tileState)) continue;
            if(current.getBlock().getType() != Material.HOPPER) continue;
            tileStates.add(tileState);
        }
        return tileStates;
    }

    public static List<TileState> getHopperStates(Location center, int radius) {
        List<TileState> tileStates = new ArrayList<>();
        World world = center.getWorld();
        if(world == null) return tileStates;
        int minChunkX = (center.getBlockX() - radius) >> 4;
        int maxChunkX = (center.getBlockX() + radius) >> 4;
        int minChunkZ = (center.getBlockZ() - radius) >> 4;
        int maxChunkZ = (center.getBlockZ() + radius) >> 4;
        double radiusSquared =  radius * radius;
        for(int cx = minChunkX; cx <= maxChunkX; cx++) {
            for(int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                if(!world.isChunkLoaded(cx, cz)) continue;
                Chunk chunk = world.getChunkAt(cx, cz);
                for(BlockState current : chunk.getTileEntities()) {
                    if(!(current instanceof TileState tileState)) continue;
                    if(current.getBlock().getType() != Material.HOPPER) continue;
                    if(current.getLocation().distanceSquared(center) <= radiusSquared) tileStates.add(tileState);
                }
            }
        }
        return tileStates;
    }

    public static boolean isBoolean(String s) {
        return "true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s);
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(miniMessage.deserialize("<#FF55FF>[<#AA00AA>Filtering Hoppers<#FF55FF>] " + message));
    }

    public static boolean sendMessage(CommandSender sender, String message, boolean returned) {
        sender.sendMessage(miniMessage.deserialize("<#FF55FF>[<#AA00AA>Filtering Hoppers<#FF55FF>] " + message));
        return returned;
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        boolean allowed = sender.hasPermission(permission);
        if(!allowed) sendMessage(sender, "<#FF5555>You don't have the permission to do that!");
        return allowed;
    }
}
