package me.cocolennon.filteringhoppers.commands;

import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

public class ItemCollectionCommand {
    public static boolean execute(CommandSender sender, String[] args) {
        if(args.length < 2) return Helper.sendMessage(sender, "<#FF5555>Please provide an option to set!", false);
        switch(args[1]) {
            case "enabled" -> {
                if(!Helper.hasPermission(sender, "filteringhoppers.set.item-collection.enabled")) return false;
                if(args.length < 3) return Helper.sendMessage(sender, "<#FF5555>You must provide a boolean to toggle this feature!", false);
                if(!Helper.isBoolean(args[2])) Helper.sendMessage(sender, "<#FF5555>You must provide a valid boolean!", false);
                boolean toggle = BooleanUtils.toBoolean(args[2]);
                Main.getInstance().getConfig().set("item-collection.enabled", toggle);
                Main.getInstance().saveConfig();
                return Helper.sendMessage(sender, "Item collection is now " + (toggle ? "enabled!" : "disabled!"), true);
            }
            case "mode" -> {
                if(!Helper.hasPermission(sender, "filteringhoppers.set.item-collection.mode")) return false;
                if(args.length < 3) return Helper.sendMessage(sender, "<#FF5555>You must provide a mode!", false);
                String arg = args[2].toLowerCase();
                if(!arg.equals("chunk") && !arg.equals("radius")) return Helper.sendMessage(sender, "<#FF5555>You must provide a valid mode!", false);
                Main.getInstance().getConfig().set("item-collection.mode", arg);
                Main.getInstance().saveConfig();
                return Helper.sendMessage(sender, "Item collection is now in " + arg + " mode!", true);
            }
            case "radius" -> {
                if(!Helper.hasPermission(sender, "filteringhoppers.set.item-collection.radius")) return false;
                if(args.length < 3) return Helper.sendMessage(sender, "<#FF5555>You must provide a radius!", false);
                if(!StringUtils.isNumeric(args[2])) return Helper.sendMessage(sender, "<#FF5555>You must provide a valid radius!", false);
                Main.getInstance().getConfig().set("item-collection.radius", args[2]);
                Main.getInstance().saveConfig();
                return Helper.sendMessage(sender, "Item collection radius for radius mode is now " + args[2] + " blocks!", true);
            }
            default -> {
                return Helper.sendMessage(sender, "<#FF5555>Please provide a valid option to set!", false);
            }
        }
    }
}
