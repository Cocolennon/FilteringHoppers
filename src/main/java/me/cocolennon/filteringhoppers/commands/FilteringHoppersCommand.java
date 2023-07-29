package me.cocolennon.filteringhoppers.commands;

import me.cocolennon.filteringhoppers.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;
import java.util.List;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class FilteringHoppersCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sendInfo(sender);
        return true;
    }

    private void sendInfo(CommandSender sender){
        List<String> info = new LinkedList<>();
        info.add("§d§l=========================");
        info.add("§5§lFiltering Hoppers §5" + getPlugin(Main.class).getDescription().getVersion());
        info.add("§5Made with §c❤ §5by Cocolennon");
        info.add("§d§l=========================");

        info.forEach(sender::sendMessage);
    }
}
