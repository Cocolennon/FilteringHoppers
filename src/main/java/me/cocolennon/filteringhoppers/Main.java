package me.cocolennon.filteringhoppers;

import me.cocolennon.filteringhoppers.commands.FilteringHoppersCommand;
import me.cocolennon.filteringhoppers.listeners.*;
import me.cocolennon.filteringhoppers.utils.MetricsUtil;
import me.cocolennon.filteringhoppers.utils.UpdateChecker;
import me.cocolennon.filteringhoppers.utils.Updater;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private String version;
    private boolean usingOldVersion = false;
    private static Main instance;
    private Config config;

    @Override
    public void onEnable() {
        instance = this;
        loadConfig(false);
        registerCommands();
        registerListeners();
        checkVersion();
        MetricsUtil.register(instance);
        getLogger().info("Plugin enabled!");
    }

    private void checkVersion() {
        new UpdateChecker(this, 111606).getVersion(cVersion -> {
            version = this.getPluginMeta().getVersion();
            if (!getVersion().equals(cVersion)) {
                getLogger().info("You are using an older version of Filtering Hoppers, please update to version " + cVersion);
                usingOldVersion = true;
            }
        });
        if(config.autoUpdaterEnabled) {
            Updater updater = new Updater(this, 111606, getFile(), Updater.UpdateType.VERSION_CHECK, true);
            if(updater.getResult().equals(Updater.Result.SUCCESS)) getLogger().info("Update will be applied after next restart!");
        }
    }

    public void loadConfig(boolean reload) {
        if(!reload) {
            saveDefaultConfig();
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        reloadConfig();
        config = new Config(this);
    }

    private void registerCommands() {
        getCommand("filteringhoppers").setExecutor(new FilteringHoppersCommand());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new InventoryMoveItemListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryOpenListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(), instance);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), instance);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), instance);
        getServer().getPluginManager().registerEvents(new BlockDropItemListener(), instance);
        getServer().getPluginManager().registerEvents(new ItemDropListener(), instance);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(), instance);
    }

    public Config config() { return config; }
    public String getVersion() { return instance.version; }
    public boolean getUsingOldVersion() { return instance.usingOldVersion; }
    public static Main getInstance() { return instance; }
}
