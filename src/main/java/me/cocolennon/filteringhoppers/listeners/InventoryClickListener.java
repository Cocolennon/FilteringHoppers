package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.utils.FilterInventoryHolder;
import me.cocolennon.filteringhoppers.utils.Helper;
import me.cocolennon.filteringhoppers.utils.Localization;
import me.cocolennon.filteringhoppers.utils.MenuUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;
        Inventory filterInventory = event.getInventory();
        if(!(filterInventory.getHolder() instanceof FilterInventoryHolder filterHolder)) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        if(clickedItem.hasItemMeta() && MenuUtil.hasButtonAction(clickedItem)) buttonClicked(player, filterHolder, clickedItem);
        else itemClicked(player, filterInventory, clickedInventory, clickedItem, event.getSlot());
    }

    private static void buttonClicked(Player player, FilterInventoryHolder invHolder, ItemStack clickedItem) {
        Inventory inv = invHolder.getInventory();
        String action = MenuUtil.getButtonAction(clickedItem);
        TileState hopperState = getHopperState(invHolder.getBlock());
        switch(action) {
            case "toggleMode" -> {
                Helper.toggleWhitelistMode(hopperState);
                boolean isWhitelist = Helper.isWhitelist(hopperState);
                inv.setItem(34, MenuUtil.getItem(player, isWhitelist ? Material.WHITE_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, isWhitelist ? "whitelist" : "blacklist", "toggleMode"));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
            case "toggleDestroy" -> {
                Helper.toggleDestroy(hopperState);
                boolean shouldDestroy = Helper.shouldDestroy(hopperState);
                inv.setItem(33, MenuUtil.getItem(player, shouldDestroy ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE, shouldDestroy ? "destroy" : "keep", "toggleDestroy"));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
            case "toggleType" -> {
                Helper.toggleFilterType(hopperState);
                boolean isFilterType = Helper.isFilterType(hopperState);
                invHolder.setItem(28, MenuUtil.getItem(player, isFilterType ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE, isFilterType ? "filter-type" : "ignore-type", "toggleType"));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
            case "toggleMeta" -> {
                Helper.toggleFilterMeta(hopperState);
                boolean isFilterMeta = Helper.isFilterMeta(hopperState);
                invHolder.setItem(29, MenuUtil.getItem(player, isFilterMeta ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE, isFilterMeta ? "filter-meta" : "ignore-meta", "toggleMeta"));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
            case "toggleEnchanted" -> {
                Helper.toggleFilterEnchanted(hopperState);
                boolean isFilterEnchanted = Helper.isFilterEnchanted(hopperState);
                invHolder.setItem(30, MenuUtil.getItem(player, isFilterEnchanted ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE, isFilterEnchanted ? "filter-enchanted" : "ignore-enchanted", "toggleEnchanted"));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }
    }

    private static void itemClicked(Player player, Inventory filterInventory, Inventory clickedInventory, ItemStack clickedItem, int clickedSlot) {
        int slot = getFirstFreeSlot(filterInventory);
        if(clickedInventory.equals(filterInventory)) {
            filterInventory.setItem(clickedSlot, new ItemStack(Material.AIR));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            return;
        }
        if(slot == 2001) {
            player.sendMessage(Localization.get(player, "filter.full", true));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        ItemStack newItem = clickedItem.clone();
        newItem.setAmount(1);
        if(Arrays.stream(filterInventory.getContents()).toList().contains(newItem)) {
            player.sendMessage(Localization.get(player, "filter.duplicate", true));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        filterInventory.setItem(slot, newItem);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    private static TileState getHopperState(Block block) {
        if(block == null || block.getType() != Material.HOPPER) return null;
        BlockState blockState = block.getState();
        if(!(blockState instanceof TileState tileState)) return null;
        return tileState;
    }

    private static int getFirstFreeSlot(Inventory inv) {
        for(int i = 0; i < inv.getSize(); i++) if(inv.getItem(i) == null) return i;
        return 2001;
    }
}
