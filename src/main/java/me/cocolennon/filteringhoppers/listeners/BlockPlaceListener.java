package me.cocolennon.filteringhoppers.listeners;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import me.cocolennon.filteringhoppers.utils.Localization;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class BlockPlaceListener implements Listener {
    private static NamespacedKey tooltipShown = new NamespacedKey(Main.getInstance(), "tooltipShown");

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Chunk chunk = block.getChunk();
        List<TileState> hopperStates = Helper.getHopperStates(chunk);
        if(hopperStates.isEmpty()) return;
        int maxHopper = Main.getInstance().config().maxHoppersPerChunk;
        if(hopperStates.size() >= maxHopper) { // correct max amount, was 1 over
            player.sendMessage(Localization.get(player, "error.hoppers", true, maxHopper));
            event.setCancelled(true);
            return;
        }
        if(!hasTooltipShown(player)) player.sendMessage(Localization.get(player, "tooltips.hopper-placed", true, true));
    }

    private static boolean hasTooltipShown(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        boolean shown = container.has(tooltipShown, PersistentDataType.BOOLEAN);
        if(!shown) container.set(tooltipShown, PersistentDataType.BOOLEAN, true);
        return shown;
    }
}
