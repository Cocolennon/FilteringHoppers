package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.MenuCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        if(!event.getView().getTitle().equals("§5Filtering Hoppers§f: §dFilter Menu")) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        Inventory inv = event.getInventory();
        Inventory getClickedInv = event.getClickedInventory();
        if(current == null) return;
        NamespacedKey buttonAction = new NamespacedKey(Main.getInstance(), "buttonAction");
        if(current.hasItemMeta() && current.getItemMeta().getPersistentDataContainer().has(buttonAction) && current.getItemMeta().getPersistentDataContainer().get(buttonAction, PersistentDataType.STRING).equals("filler")) event.setCancelled(true);
        else {
            event.setCancelled(true);
            int slot = MenuCreator.getInstance().getFirstFreeSlot(inv);
            if(getClickedInv.equals(inv)) {
                inv.setItem(event.getSlot(), new ItemStack(Material.AIR));
                return;
            }
            if(slot == 2001) {
                player.sendMessage("§d[§5Filtering Hoppers§d] §cThe filter is full!");
                return;
            }
            ItemStack newItem = current.clone();
            newItem.setAmount(1);
            if(Arrays.stream(inv.getContents()).toList().contains(newItem)) {
                player.sendMessage("§d[§5Filtering Hoppers§d] §cThis item is already in the filter!");
                return;
            }
            inv.setItem(slot, newItem);
        }
    }
}
