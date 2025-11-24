package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.FilterInventoryHolder;
import me.cocolennon.filteringhoppers.utils.MenuCreator;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if(!(inv.getHolder() instanceof FilterInventoryHolder)) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        Inventory getClickedInv = event.getClickedInventory();
        if(current == null) return;
        NamespacedKey buttonAction = new NamespacedKey(Main.getInstance(), "buttonAction");
        if(current.hasItemMeta() && current.getItemMeta().getPersistentDataContainer().has(buttonAction) && current.getItemMeta().getPersistentDataContainer().get(buttonAction, PersistentDataType.STRING).equals("filler")) event.setCancelled(true);
        else {
            MiniMessage miniMessage = Main.getMiniMessage();
            event.setCancelled(true);
            int slot = MenuCreator.getInstance().getFirstFreeSlot(inv);
            if(getClickedInv.equals(inv)) {
                inv.setItem(event.getSlot(), new ItemStack(Material.AIR));
                return;
            }
            if(slot == 2001) {
                player.sendMessage(miniMessage.deserialize("<#FF55FF>[<#AA00AA>Filtering Hoppers<#FF55FF>] <#FF5555>The filter is full!"));
                return;
            }
            ItemStack newItem = current.clone();
            newItem.setAmount(1);
            if(Arrays.stream(inv.getContents()).toList().contains(newItem)) {
                player.sendMessage(miniMessage.deserialize("<#FF55FF>[<#AA00AA>Filtering Hoppers<#FF55FF>] <#FF5555>This item is already in the filter!"));
                return;
            }
            inv.setItem(slot, newItem);
        }
    }
}
