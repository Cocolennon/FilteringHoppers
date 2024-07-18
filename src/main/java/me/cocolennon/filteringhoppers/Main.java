package me.cocolennon.filteringhoppers;

import me.cocolennon.filteringhoppers.commands.FilteringHoppersCommand;
import me.cocolennon.filteringhoppers.listeners.*;
import me.cocolennon.filteringhoppers.utils.UpdateChecker;
import me.cocolennon.filteringhoppers.utils.Updater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private String version;
    private boolean usingOldVersion = false;
    private static Main instance;
    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        instance = this;
        checkVersion();
        setUpConfig();
        registerCommandsAndListeners();
        getLogger().info("Plugin enabled!");
    }

    private void checkVersion() {
        new UpdateChecker(this, 111606).getVersion(cVersion -> {
            version = this.getDescription().getVersion();
            if (!getVersion().equals(cVersion)) {
                getLogger().info("You are using an older version of Filtering Hoppers, please update to version " + cVersion);
                usingOldVersion = true;
            }
        });
        if(getConfig().getBoolean("auto-updater-enabled")) {
            Updater updater = new Updater(this, 111606, getFile(), Updater.UpdateType.VERSION_CHECK, true);
            if(updater.getResult().equals(Updater.Result.SUCCESS)) getLogger().info("Update will be applied after next restart!");
        }
    }

    private void setUpConfig(){
        config.addDefault("max-hopper-per-chunk", 5);
        config.addDefault("chunk-collection-enabled", true);
        config.addDefault("auto-updater-enabled", true);
        config.options().copyDefaults(true);
        saveConfig();
    }

    private void registerCommandsAndListeners() {
        getCommand("filteringhoppers").setExecutor(new FilteringHoppersCommand());
        getServer().getPluginManager().registerEvents(new InventoryMoveItemListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryOpenListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), instance);
        getServer().getPluginManager().registerEvents(new PlayerDropItemListener(), instance);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), instance);
        getServer().getPluginManager().registerEvents(new BlockDropItemListener(), instance);
        getServer().getPluginManager().registerEvents(new EntityItemDropListener(), instance);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(), instance);
    }

    public String getVersion() { return instance.version; }
    public boolean getUsingOldVersion() { return instance.usingOldVersion; }
    public static Main getInstance() { return instance; }
}
