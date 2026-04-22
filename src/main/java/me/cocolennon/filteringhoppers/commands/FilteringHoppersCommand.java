package me.cocolennon.filteringhoppers.commands;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FilteringHoppersCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) return Helper.sendMessage(sender, "<#FF5555>Usage: /" + label + " <info/reload/max-hoppers-per-chunk/item-collection>\n<#FF5555><> = Required.", false);
        switch (args[0]) {
            case "info" -> {
                return sendInfo(sender);
            }
            case "reload" -> {
                return reloadConfig(sender);
            }
            case "max-hoppers-per-chunk" -> {
                return setMaxHoppers(sender, args);
            }
            case "item-collection" -> {
                return ItemCollectionCommand.execute(sender, args);
            }
            default -> {
                return Helper.sendMessage(sender, "<#FF5555>Usage: /" + label + " <info/reload/max-hoppers-per-chunk/item-collection>\n<#FF5555><> = Required.", false);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return null;
        if(args.length == 1) return List.of("info", "reload", "max-hoppers-per-chunk", "item-collection");
        if(args.length == 2) {
            return switch(args[0]) {
                case "max-hoppers-per-chunk" -> {
                    List<String> hundred = new ArrayList<>();
                    for(int i = 0; i < 100; i++) {
                        hundred.add(String.valueOf(i));
                    }
                    yield hundred;
                }
                case "item-collection" -> List.of("enabled", "mode", "radius");
                default -> List.of();
            };
        }
        if(args.length == 3) {
            return switch(args[1]) {
                case "enabled" -> List.of("true", "false");
                case "mode" -> List.of("Chunk", "Radius");
                case "radius" -> {
                    List<String> hundred = new ArrayList<>();
                    for(int i = 0; i < 100; i++) {
                        hundred.add(String.valueOf(i));
                    }
                    yield hundred;
                }
                default -> List.of();
            };
        }
        return null;
    }

    private boolean sendInfo(CommandSender sender){
        MiniMessage miniMessage = Main.getMiniMessage();
        List<Component> info = new LinkedList<>();
        info.add(miniMessage.deserialize("<#FF55FF><bold>========================="));
        info.add(miniMessage.deserialize("<#AA00AA><bold>Filtering Hoppers <#AA00AA>" + Main.getInstance().getVersion()));
        if(Main.getInstance().getUsingOldVersion()){
            info.add(miniMessage.deserialize("<#FF55FF>An update is available!"));
        }else{
            info.add(miniMessage.deserialize("<#AA00AA>You're using the latest version"));
        }
        info.add(miniMessage.deserialize("<#AA00AA>Made with <#FF5555>❤ <#AA00AA>by Cocolennon"));
        info.add(miniMessage.deserialize("<#FF55FF><bold>========================="));
        info.forEach(sender::sendMessage);
        return true;
    }

    private boolean reloadConfig(CommandSender sender) {
        if(!Helper.hasPermission(sender, "filteringhoppers.reload")) return false;
        Main.getInstance().reloadConfig();
        return Helper.sendMessage(sender, "Configuration reloaded!", true);
    }

    private boolean setMaxHoppers(CommandSender sender, String[] args) {
        if(!Helper.hasPermission(sender, "filteringhoppers.set.max-hoppers-per-chunk")) return false;
        if(args.length < 2) return Helper.sendMessage(sender, "<#FF5555>You must provide a number!", false);
        if(!StringUtils.isNumeric(args[1])) Helper.sendMessage(sender, "<#FF5555>You must provide a valid number!", false);
        Main.getInstance().getConfig().set("max-hopper-per-chunk", Integer.parseInt(args[1]));
        Main.getInstance().saveConfig();
        return Helper.sendMessage(sender, "Maximum hoppers per chunk is now " + args[1] + "!", true);
    }
}
