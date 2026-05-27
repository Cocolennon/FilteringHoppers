package me.cocolennon.filteringhoppers.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.cocolennon.filteringhoppers.Main;
import me.cocolennon.filteringhoppers.utils.Helper;
import me.cocolennon.filteringhoppers.utils.Localization;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class FilteringHoppersCommand {
    public static LiteralCommandNode<CommandSourceStack> register() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("filteringhoppers")
            .then(Commands.literal("info")
                    .requires(sender -> Helper.hasPermission(sender, "filteringhoppers.info"))
                    .executes(FilteringHoppersCommand::sendInfo))
            .then(Commands.literal("reload")
                    .requires(sender -> Helper.hasPermission(sender, "filteringhoppers.reload"))
                    .executes(FilteringHoppersCommand::reloadConfig))
            .then(Commands.literal("max-hoppers-per-chunk")
                    .requires(sender -> Helper.hasPermission(sender, "filteringhoppers.set.max-hoppers-per-chunk"))
                    .then(Commands.argument("hoppers", IntegerArgumentType.integer(0))
                            .executes(FilteringHoppersCommand::setMaxHoppers)));
        return root.build();
    }

    private static int sendInfo(CommandContext<CommandSourceStack> context) {
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
        info.forEach(context.getSource().getSender()::sendMessage);
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Main.getInstance().loadConfig(true);
        player.sendMessage(Localization.get(player, "success.reload", true));
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    private static int setMaxHoppers(CommandContext<CommandSourceStack> context) {
        Player player =  (Player) context.getSource().getSender();
        int hoppers = context.getArgument("hoppers", Integer.class);
        Main main = Main.getInstance();
        main.getConfig().set("max-hoppers-per-chunk", hoppers);
        main.saveConfig();
        main.loadConfig(true);
        player.sendMessage(Localization.get(player, "success.max-hoppers", true, hoppers));
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }
}
