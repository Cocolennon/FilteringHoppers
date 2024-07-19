package me.cocolennon.filteringhoppers.utils;

import me.cocolennon.filteringhoppers.Main;
import org.bukkit.Bukkit;
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

    public void createFilterMenu(List<ItemStack> filter, Player player, Block block) {
        Hopper hopper = (Hopper) block.getState();
        Inventory inv = Bukkit.createInventory(hopper, 27, "§5Filtering Hoppers§f: §dFilter Menu");

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
        NamespacedKey buttonAction = new NamespacedKey(Main.getInstance(), "buttonAction");
        PersistentDataContainer pdc = itM.getPersistentDataContainer();
        pdc.set(buttonAction, PersistentDataType.STRING, "filler");
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
