package me.cocolennon.filteringhoppers.utils;

import me.cocolennon.filteringhoppers.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class MenuCreator {
    private static final MenuCreator instance = new MenuCreator();

    public void createFilterMenu(List<ItemStack> filter, Player player, Block block, boolean whitelist) {
        Hopper hopper = (Hopper) block.getState();
        FilterInventoryHolder invHolder = new FilterInventoryHolder(Main.getInstance(), 27, Localization.get(player, "menu-title", false), block);
        Inventory inv = invHolder.getInventory();
        if(filter != null) for(ItemStack itemStack : filter) invHolder.addItem(itemStack);
        invHolder.fillEmpty(18, getFillerItem());
        player.openInventory(inv);
        player.sendMessage(Localization.get(player, "filter.open", true));
    }

    public boolean isItemFiller(Inventory inv, int slot) {
        ItemStack item = inv.getItem(slot);
        if(item == null || !item.hasItemMeta()) return false;
        NamespacedKey buttonAction = new NamespacedKey(Main.getInstance(), "buttonAction");
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.get(buttonAction, PersistentDataType.STRING).equals("filler");
    }

    private ItemStack getFillerItem(){
        ItemStack it = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta itM = it.getItemMeta();
        assert itM != null;
        itM.displayName(Component.text(" "));
        NamespacedKey buttonAction = new NamespacedKey(Main.getInstance(), "buttonAction");
        PersistentDataContainer pdc = itM.getPersistentDataContainer();
        pdc.set(buttonAction, PersistentDataType.STRING, "filler");
        itM.setHideTooltip(true);
        it.setItemMeta(itM);
        return it;
    }

    public int getFirstFreeSlot(Inventory inv) {
        int result = 2001;
        for(int i = 0; i < inv.getSize(); i++) {
            if(inv.getItem(i) == null) {
                result = i;
                break;
            }
        }
        return result;
    }

    public static MenuCreator getInstance() { return instance; }
}
