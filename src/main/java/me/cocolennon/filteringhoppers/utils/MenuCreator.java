package me.cocolennon.filteringhoppers.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuCreator {
    private static final MenuCreator instance = new MenuCreator();
    private final ItemStack filler = getItem(Material.BLACK_STAINED_GLASS_PANE, " ", "filler");

    public void createFilterMenu(ItemStack[] filter, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§5Filtering Hoppers§f: §dFilter Menu");

        if(filter != null) {
            for (int i = 0; i < filter.length; i++) {
                inv.addItem(filter[i]);
            }
        }

        fillEmpty(inv, filler, 18);

        player.openInventory(inv);
        player.sendMessage("§d[§5Filtering Hoppers§d] Successfully opened the filter menu.");
    }

    private void fillEmpty(Inventory inv, ItemStack item, int start){
        for(int i = start; i < inv.getSize(); i++){
            if(inv.getItem(i) != null) break;
            inv.setItem(i, item);
        }
    }

    private ItemStack getItem(Material material, String newName, String localizedName){
        ItemStack it = new ItemStack(material, 1);
        ItemMeta itM = it.getItemMeta();
        assert itM != null;
        if(newName != null) itM.setDisplayName(newName);
        if(localizedName != null) itM.setLocalizedName(localizedName);
        it.setItemMeta(itM);
        return it;
    }

    public static MenuCreator getInstance() { return instance; }
}
