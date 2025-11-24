package me.cocolennon.filteringhoppers.commands;

import me.cocolennon.filteringhoppers.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    private MiniMessage miniMessage = Main.getMiniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        if(args.length == 0) {
            sender.sendMessage(miniMessage.deserialize("<#FF55FF>[<#AA00AA>Filtering Hoppers<#FF55FF>] <#FF5555>Usage: /" + label + " </info/reload/set-max-hopper>\n<#FF5555><> = Required."));
            return false;
        }

        switch (args[0]) {
            case "info" -> {
                sendInfo(sender);
                return true;
            }
            case "reload" -> {
                if(!sender.hasPermission("filteringhoppers.reload")) {
                    sender.sendMessage(miniMessage.deserialize("<#FF55FF>[<#AA00AA>Filtering Hoppers<#FF55FF>] <#FF5555>You don't have the permission to do that!"));
                    return false;
                }
                Main.getInstance().reloadConfig();
                sender.sendMessage(miniMessage.deserialize("<#FF55FF>[<#AA00AA>Filtering Hoppers<#FF55FF>] Configuration reloaded!"));
                return true;
            }
            case "set-max-hopper" -> {
                if(!sender.hasPermission("filteringhoppers.set-max-hopper")) {
                    sender.sendMessage(miniMessage.deserialize("<#FF55FF>[<#AA00AA>Filtering Hoppers<#FF55FF>] <#FF5555>You don't have the permission to do that!"));
                    return false;
                }
                if(!StringUtils.isNumeric(args[1])) {
                    sender.sendMessage(miniMessage.deserialize("<#FF55FF>[<#AA00AA>Filtering Hoppers<#FF55FF>] <#FF5555>You must provide a valid number!"));
                    return false;
                }
                Main.getInstance().getConfig().set("max-hopper-per-chunk", Integer.parseInt(args[1]));
                Main.getInstance().saveConfig();
                return true;
            }
            default -> {
                sender.sendMessage(miniMessage.deserialize("<#FF55FF>[<#AA00AA>Filtering Hoppers<#FF55FF>] <#FF5555>Usage: /" + label + " </info/reload/set-max-hopper>\n<#FF5555><> = Required."));
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
        List<Component> info = new LinkedList<>();
        info.add(miniMessage.deserialize("<#FF55FF><bold>========================="));
        info.add(miniMessage.deserialize("<#AA00AA><bold>Filtering Hoppers <#AA00AA>" + getPlugin(Main.class).getPluginMeta().getVersion()));
        if(Main.getInstance().getUsingOldVersion()){
            info.add(miniMessage.deserialize("<#FF55FF>An update is available!"));
        }else{
            info.add(miniMessage.deserialize("<#AA00AA>You're using the latest version"));
        }
        info.add(miniMessage.deserialize("<#AA00AA>Made with <#FF5555>❤ <#AA00AA>by Cocolennon"));
        info.add(miniMessage.deserialize("<#FF55FF><bold>========================="));

        info.forEach(sender::sendMessage);
    }
}
