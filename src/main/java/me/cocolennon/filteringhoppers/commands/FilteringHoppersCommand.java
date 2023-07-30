package me.cocolennon.filteringhoppers.commands;

import me.cocolennon.filteringhoppers.Main;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class FilteringHoppersCommand implements TabExecutor {
    private final List<String> autoComplete = Arrays.asList("info", "reload", "set-max-hopper");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        if(args.length == 0) {
            sender.sendMessage("§d[§5Filtering Hoppers§d] §cUsage: /" + label + " </info/reload/set-max-hopper>\n§c<> = Required.");
            return false;
        }

        switch (args[0]) {
            case "info" -> {
                sendInfo(sender);
                return true;
            }
            case "reload" -> {
                if(!sender.hasPermission("filteringhoppers.reload")) {
                    sender.sendMessage("§d[§5Filtering Hoppers§d] §cYou don't have the permission to do that!");
                    return false;
                }
                Main.getInstance().reloadConfig();
                sender.sendMessage("§d[§5Filtering Hoppers§d] Configuration reloaded!");
                return true;
            }
            case "set-max-hopper" -> {
                if(!sender.hasPermission("filteringhoppers.set-max-hopper")) {
                    sender.sendMessage("§d[§5Filtering Hoppers§d] §cYou don't have the permission to do that!");
                    return false;
                }
                if(!StringUtils.isNumeric(args[1])) {
                    sender.sendMessage("§d[§5Filtering Hoppers§d] §cYou must provide a valid number!");
                    return false;
                }
                Main.getInstance().getConfig().set("max-hopper-per-chunk", Integer.parseInt(args[1]));
                Main.getInstance().saveConfig();
                return true;
            }
            default -> {
                sender.sendMessage("§d[§5Filtering Hoppers§d] §cUsage: /" + label + " </info/reload/set-max-hopper>\n§c<> = Required.");
                return false;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return null;
        if(args.length == 1) return autoComplete;
        List<String> hundred = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            hundred.add(String.valueOf(i));
        }
        if(args.length == 2) return hundred;
        return null;
    }

    private void sendInfo(CommandSender sender){
        List<String> info = new LinkedList<>();
        info.add("§d§l=========================");
        info.add("§5§lFiltering Hoppers §5" + getPlugin(Main.class).getDescription().getVersion());
        if(Main.getInstance().getUsingOldVersion()){
            info.add("§dAn update is available!");
        }else{
            info.add("§5You're using the latest version");
        }
        info.add("§5Made with §c❤ §5by Cocolennon");
        info.add("§d§l=========================");

        info.forEach(sender::sendMessage);
    }
}
