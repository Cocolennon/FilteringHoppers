package me.cocolennon.filteringhoppers;

import me.cocolennon.filteringhoppers.commands.FilteringHoppersCommand;
import me.cocolennon.filteringhoppers.listeners.InventoryClickListener;
import me.cocolennon.filteringhoppers.listeners.InventoryCloseListener;
import me.cocolennon.filteringhoppers.listeners.InventoryMoveItemListener;
import me.cocolennon.filteringhoppers.listeners.InventoryOpenListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        registerCommandsAndListeners();
        getLogger().info("Plugin enabled!");
    }

    private void registerCommandsAndListeners() {
        getCommand("filteringhoppers").setExecutor(new FilteringHoppersCommand());
        getServer().getPluginManager().registerEvents(new InventoryMoveItemListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryOpenListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), instance);
    }

    public static Main getInstance() { return instance; }
}
