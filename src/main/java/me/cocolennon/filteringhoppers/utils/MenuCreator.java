package me.cocolennon.filteringhoppers.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuCreator {
    private static final MenuCreator instance = new MenuCreator();

    public void createFilterMenu(ItemStack[] filter, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§5Filtering Hoppers§f: §dFilter Menu");

        if(filter != null) {
            for (ItemStack itemStack : filter) {
                inv.addItem(itemStack);
            }
        }

        fillEmpty(inv, getItem());

        player.openInventory(inv);
        player.sendMessage("§d[§5Filtering Hoppers§d] Successfully opened the filter menu.");
    }

    private void fillEmpty(Inventory inv, ItemStack item){
        for(int i = 18; i < inv.getSize(); i++){
            if(inv.getItem(i) != null) break;
            inv.setItem(i, item);
        }
    }

    private ItemStack getItem(){
        ItemStack it = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta itM = it.getItemMeta();
        assert itM != null;
        itM.setDisplayName(" ");
        itM.setLocalizedName("filler");
        it.setItemMeta(itM);
        return it;
    }

    public static MenuCreator getInstance() { return instance; }
}
