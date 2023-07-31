package me.cocolennon.filteringhoppers.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.cocolennon.filteringhoppers.Main;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.List;

public class BlockPlaceListener implements Listener {
    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Chunk chunk = block.getChunk();
        ItemStack itemStack = event.getItemInHand();

        List<TileState> tileStates = new ArrayList<>();
        for(BlockState current : chunk.getTileEntities()) {
            if(!(current instanceof TileState)) return;
            TileState currentTileState = (TileState) current;
            tileStates.add(currentTileState);
        }
        if(tileStates.size() == 0) return;

        int hopperCount = 0;
        for(TileState current : tileStates) {
            if(current instanceof Hopper) hopperCount++;
        }

        int maxHopper = (int) Main.getInstance().getConfig().get("max-hopper-per-chunk");
        if(hopperCount > maxHopper) {
            player.sendMessage("§d[§5Filtering Hoppers§d] §cYou can only have a maximum of " + Main.getInstance().getConfig().get("max-hopper-per-chunk") + " hoppers per chunk!");
            event.setCancelled(true);
        }else{
            if(!itemStack.hasItemMeta() && !itemStack.getItemMeta().hasLore()) return;
            for(TileState current : tileStates) {
                if(!(current instanceof Hopper)) return;
                PersistentDataContainer container = current.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(Main.getInstance(), "lore");
                String[] lore = itemStack.getItemMeta().getLore().toArray(new String[0]);
                container.set(key, DataType.STRING_ARRAY, lore);
                current.update();
            }
        }
    }
}
