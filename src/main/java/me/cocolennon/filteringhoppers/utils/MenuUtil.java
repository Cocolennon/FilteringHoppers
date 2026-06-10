package me.cocolennon.filteringhoppers.utils;

import me.cocolennon.filteringhoppers.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class MenuUtil {
    public static NamespacedKey buttonAction = new NamespacedKey(Main.getInstance(), "buttonAction");

    public static void createFilterMenu(TileState hopperState, List<ItemStack> filter, Player player, Block block) {
        FilterInventoryHolder invHolder = new FilterInventoryHolder(Main.getInstance(), 27, Localization.get(player, "menu-title", false), block);
        Inventory inv = invHolder.getInventory();
        if(filter != null) for(ItemStack itemStack : filter) invHolder.addItem(itemStack);
        boolean shouldDestroy = Helper.shouldDestroy(hopperState);
        boolean isWhitelist = Helper.isWhitelist(hopperState);
        boolean isFilterType = Helper.isFilterType(hopperState);
        boolean isFilterMeta = Helper.isFilterMeta(hopperState);
        boolean isFilterEnchanted = Helper.isFilterEnchanted(hopperState);
        invHolder.setItem(19, getItem(player, isFilterType ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE, isFilterType ? "filter-type" : "ignore-type", "toggleType"));
        invHolder.setItem(20, getItem(player, isFilterMeta ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE, isFilterMeta ? "filter-meta" : "ignore-meta", "toggleMeta"));
        invHolder.setItem(21, getItem(player, isFilterEnchanted ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE, isFilterEnchanted ? "filter-enchanted" : "ignore-enchanted", "toggleEnchanted"));
        invHolder.setItem(24, getItem(player, shouldDestroy ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE, shouldDestroy ? "destroy" : "keep", "toggleDestroy"));
        invHolder.setItem(25, getItem(player, isWhitelist ? Material.WHITE_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, isWhitelist ? "whitelist" : "blacklist", "toggleMode"));
        invHolder.fillEmpty(18, getFillerItem());
        player.playSound(player.getLocation(), Sound.BLOCK_BARREL_OPEN, 1.0f, 1.0f);
        player.openInventory(inv);
        player.sendMessage(Localization.get(player, "filter.open", true));
    }

    public static ItemStack getItem(Player player, Material material, String mode, String buttonAction) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Localization.get(player, "filter.mode-item." + mode + ".name", false).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Localization.get(player, "filter.mode-item." + mode + ".hint", false).decoration(TextDecoration.ITALIC, false)));
        setButtonAction(meta, buttonAction);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getFillerItem(){
        ItemStack item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.displayName(Component.text(" "));
        setButtonAction(meta, "filler");
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        return item;
    }

    public static String getButtonAction(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(buttonAction, PersistentDataType.STRING);
    }

    public static boolean hasButtonAction(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.has(buttonAction, PersistentDataType.STRING);
    }

    public static void setButtonAction(ItemMeta meta, String action) {
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(buttonAction, PersistentDataType.STRING, action);
    }
}
