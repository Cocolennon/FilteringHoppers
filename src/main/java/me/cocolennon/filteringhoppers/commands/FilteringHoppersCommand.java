package me.cocolennon.filteringhoppers.commands;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import me.cocolennon.filteringhoppers.utils.Localization;
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
        if(!(sender instanceof Player player)) return false;
        if(args.length == 0) {
            sender.sendMessage(Localization.get(player, "error.usage", true));
            return false;
        }
        switch (args[0]) {
            case "info" -> {
                return sendInfo(player);
            }
            case "reload" -> {
                return reloadConfig(player);
            }
            case "max-hoppers-per-chunk" -> {
                return setMaxHoppers(player, args);
            }
            case "item-collection" -> {
                return ItemCollectionCommand.execute(player, args);
            }
            default -> {
                sender.sendMessage(Localization.get(player, "error.usage", true));
                return false;
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

    private boolean sendInfo(Player player){
        MiniMessage miniMessage = MiniMessage.miniMessage();
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
        info.forEach(player::sendMessage);
        return true;
    }

    private boolean reloadConfig(Player player) {
        if(!Helper.hasPermission(player, "filteringhoppers.reload")) return false;
        Main.getInstance().loadConfig();
        player.sendMessage(Localization.get(player, "success.reload", true));
        return true;
    }

    private boolean setMaxHoppers(Player player, String[] args) {
        if(!Helper.hasPermission(player, "filteringhoppers.set.max-hoppers-per-chunk")) return false;
        if(args.length < 2 || !StringUtils.isNumeric(args[1])) {
            player.sendMessage(Localization.get(player, "error.invalid.number", true));
            return false;
        }
        Main main = Main.getInstance();
        main.getConfig().set("max-hoppers-per-chunk", Integer.parseInt(args[1]));
        main.saveConfig();
        main.loadConfig();
        player.sendMessage(Localization.get(player, "success.max-hoppers", true, args[1]));
        return true;
    }
}
