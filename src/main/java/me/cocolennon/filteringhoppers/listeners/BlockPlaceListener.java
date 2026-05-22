package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Localization;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockPlaceListener implements Listener {
    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Chunk chunk = block.getChunk();
        List<TileState> tileStates = new ArrayList<>();
        for(BlockState current : chunk.getTileEntities()) {
            if(!(current instanceof TileState)) return;
            TileState currentTileState = (TileState) current;
            tileStates.add(currentTileState);
        }
        if(tileStates.isEmpty()) return;
        int hopperCount = 0;
        for(TileState current : tileStates) {
            if(current instanceof Hopper) hopperCount++;
        }
        int maxHopper = Main.getInstance().config().maxHoppersPerChunk;
        if(hopperCount > maxHopper) {
            player.sendMessage(Localization.get(player, "error.hoppers", true, maxHopper));
            event.setCancelled(true);
        }
    }
}
