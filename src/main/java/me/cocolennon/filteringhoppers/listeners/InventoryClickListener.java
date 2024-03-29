package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.MenuCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        if(!event.getView().getTitle().equals("§5Filtering Hoppers§f: §dFilter Menu")) return;
        Player player = event.getWhoClicked().getKiller();
        ItemStack current = event.getCurrentItem();
        Inventory inv = event.getInventory();
        Inventory getClickedInv = event.getClickedInventory();
        ClickType click = event.getClick();
        if(current == null) return;
        if(current.hasItemMeta() && current.getItemMeta().hasLocalizedName() && current.getItemMeta().getLocalizedName().equals("filler")) event.setCancelled(true);
        else {
            event.setCancelled(true);
            if(click.isLeftClick()) {
                int slot = MenuCreator.getInstance().getFirstFreeSlot(inv);
                if(slot == 2001) {
                    player.sendMessage("§d[§5Filtering Hoppers§d] §cThe filter is full!");
                    return;
                }
                if(getClickedInv.equals(inv)) return;
                inv.setItem(slot, current);
            }else if(click.isRightClick()) {
                if(!getClickedInv.equals(inv)) return;
                inv.setItem(event.getSlot(), new ItemStack(Material.AIR));
            }
        }
    }
}
