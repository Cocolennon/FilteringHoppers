package me.cocolennon.filteringhoppers.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        if(!event.getView().getTitle().equals("§5Filtering Hoppers§f: §dFilter Menu")) return;
        ItemStack current = event.getCurrentItem();
        if(current == null) return;
        if(!current.hasItemMeta()) return;
        if(!current.getItemMeta().hasLocalizedName()) return;
        if(current.getItemMeta().getLocalizedName().equals("filler")) event.setCancelled(true);
    }
}
