package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.FilterInventoryHolder;
import me.cocolennon.filteringhoppers.utils.Helper;
import me.cocolennon.filteringhoppers.utils.Localization;
import me.cocolennon.filteringhoppers.utils.MenuCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.TileState;
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
        if(!(inv.getHolder() instanceof FilterInventoryHolder invHolder)) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        Inventory inventory = event.getClickedInventory();
        if(current == null) return;
        NamespacedKey buttonAction = new NamespacedKey(Main.getInstance(), "buttonAction");
        if(current.hasItemMeta() && current.getItemMeta().getPersistentDataContainer().has(buttonAction)) {
            if(current.getItemMeta().getPersistentDataContainer().get(buttonAction, PersistentDataType.STRING).equals("toggleMode")) {
                TileState hopperState = Helper.getHopperState(invHolder.getBlock());
                Helper.setWhitelistMode(hopperState);
                inv.setItem(25, MenuCreator.getInstance().getModeItem(player, Helper.isWhitelist(hopperState)));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }else{
            int slot = MenuCreator.getInstance().getFirstFreeSlot(inv);
            if(inventory.equals(inv)) {
                inv.setItem(event.getSlot(), new ItemStack(Material.AIR));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                return;
            }
            if(slot == 2001) {
                player.sendMessage(Localization.get(player, "filter.full", true));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            ItemStack newItem = current.clone();
            newItem.setAmount(1);
            if(Arrays.stream(inv.getContents()).toList().contains(newItem)) {
                player.sendMessage(Localization.get(player, "filter.duplicate", true));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            inv.setItem(slot, newItem);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        }
        event.setCancelled(true);
    }
}
