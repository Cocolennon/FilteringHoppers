package me.cocolennon.filteringhoppers.commands;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import me.cocolennon.filteringhoppers.utils.Localization;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

public class ItemCollectionCommand {
    public static boolean execute(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(Localization.get(player, "error.invalid.option", true));
            return false;
        }
        Main main = Main.getInstance();
        switch(args[1]) {
            case "enabled" -> {
                if(!Helper.hasPermission(player, "filteringhoppers.set.item-collection.enabled")) return false;
                if(args.length < 3 || !Helper.isBoolean(args[2])) {
                    player.sendMessage(Localization.get(player, "error.invalid.boolean", true));
                    return false;
                }
                boolean toggle = BooleanUtils.toBoolean(args[2]);
                configSet(main, "item-collection.enabled", toggle);
                player.sendMessage(Localization.get(player, "success.item-collection.toggle." + (toggle ? "enabled" : "disabled"), true));
                return true;
            }
            case "mode" -> {
                if(!Helper.hasPermission(player, "filteringhoppers.set.item-collection.mode")) return false;
                if(args.length < 3 || (!args[2].equalsIgnoreCase("chunk") && !args[2].equalsIgnoreCase("radius"))) {
                    player.sendMessage(Localization.get(player, "error.invalid.mode", true));
                    return false;
                }
                configSet(main, "item-collection.mode", args[2]);
                player.sendMessage(Localization.get(player, "success.item-collection.toggle." + args[2].toLowerCase(), true));
                return true;
            }
            case "radius" -> {
                if(!Helper.hasPermission(player, "filteringhoppers.set.item-collection.radius")) return false;
                if(args.length < 3 || !StringUtils.isNumeric(args[2])) {
                    player.sendMessage(Localization.get(player, "error.invalid.radius", true));
                    return false;
                }
                int radius = Integer.parseInt(args[2]);
                configSet(main, "item-collection.radius", radius);
                player.sendMessage(Localization.get(player, "success.item-collection.radius", true, radius));
                return true;
            }
            default -> {
                player.sendMessage(Localization.get(player, "error.invalid.option", true));
                return false;
            }
        }
    }

    private static void configSet(Main main, String node, Object value) {
        main.getConfig().set(node, value);
        main.saveConfig();
        main.loadConfig(true);
    }
}
