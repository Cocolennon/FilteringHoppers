package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
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

        int maxHopper = (int) Main.getInstance().getConfig().get("max-hopper-per-chunk");
        if(hopperCount > maxHopper) {
            player.sendMessage(Main.getMiniMessage().deserialize("<#FF55FF>[<#AA00AA>Filtering Hoppers<#FF55FF>] <#FF5555>You can only have a maximum of " + Main.getInstance().getConfig().get("max-hopper-per-chunk") + " hoppers per chunk!"));
            event.setCancelled(true);
        }
    }
}
