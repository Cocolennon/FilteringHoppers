package me.cocolennon.filteringhoppers.utils;

import me.cocolennon.filteringhoppers.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MenuUtil {
    public static void createFilterMenu(List<ItemStack> filter, Player player, Block block, boolean whitelist, boolean shouldDestroy) {
        Hopper hopper = (Hopper) block.getState();
        FilterInventoryHolder invHolder = new FilterInventoryHolder(Main.getInstance(), 27, Localization.get(player, "menu-title", false), block);
        Inventory inv = invHolder.getInventory();
        if(filter != null) for(ItemStack itemStack : filter) invHolder.addItem(itemStack);
        invHolder.setItem(24, getDestroyItem(player, shouldDestroy));
        invHolder.setItem(25, getModeItem(player, whitelist));
        invHolder.fillEmpty(18, getFillerItem());
        player.playSound(player.getLocation(), Sound.BLOCK_BARREL_OPEN, 1.0f, 1.0f);
        player.openInventory(inv);
        player.sendMessage(Localization.get(player, "filter.open", true));
    }

    public static ItemStack getDestroyItem(Player player, boolean shouldDestroy) {
        ItemStack item = new ItemStack(shouldDestroy ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.displayName(Localization.get(player, "filter.mode-item." + (shouldDestroy ? "destroy" : "keep") + ".name", false));
        meta.lore(List.of(Localization.get(player, "filter.mode-item." + (shouldDestroy ? "destroy" : "keep") + ".hint", false)));
        Helper.setButtonAction(meta, "toggleDestroy");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getModeItem(Player player, boolean isWhitelist) {
        ItemStack item = new ItemStack(isWhitelist ? Material.WHITE_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.displayName(Localization.get(player, "filter.mode-item." + (isWhitelist ? "whitelist" : "blacklist") + ".name", false));
        meta.lore(List.of(Localization.get(player, "filter.mode-item." + (isWhitelist ? "whitelist" : "blacklist") + ".hint", false)));
        Helper.setButtonAction(meta, "toggleMode");
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getFillerItem(){
        ItemStack item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.displayName(Component.text(" "));
        Helper.setButtonAction(meta, "filler");
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        return item;
    }

    public static int getFirstFreeSlot(Inventory inv) {
        int result = 2001;
        for(int i = 0; i < inv.getSize(); i++) {
            if(inv.getItem(i) == null) {
                result = i;
                break;
            }
        }
        return result;
    }
}
