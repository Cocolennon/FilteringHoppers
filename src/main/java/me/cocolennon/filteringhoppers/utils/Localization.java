package me.cocolennon.filteringhoppers.utils;

import me.cocolennon.filteringhoppers.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public final class Localization {
    private static Main main;
    private static String fallback;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final Map<String, FileConfiguration> locales = new HashMap<>();

    public static void init(Main instance, String defaultLocale) {
        main = instance;
        fallback = defaultLocale;
        File langFolder = new File(main.getDataFolder(), "lang");
        if(!langFolder.exists()) langFolder.mkdirs();
        saveLangFile("en-US.yml");
        saveLangFile("fr-FR.yml");
        loadLocales(langFolder);
    }

    private static void saveLangFile(String fileName) {
        File file = new File(main.getDataFolder(), "lang/" + fileName);
        if(!file.exists()) {
            main.saveResource("lang/" + fileName, false);
            return;
        }
        InputStream stream = main.getResource("lang/" + fileName);
        YamlConfiguration currentLocale =  YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
        YamlConfiguration oldLocale = YamlConfiguration.loadConfiguration(file);
        boolean changed = false;
        for(String key : currentLocale.getKeys(true)) {
            if(!oldLocale.contains(key)) {
                oldLocale.set(key, currentLocale.getString(key));
                changed = true;
            }
        }
        if(changed) {
            try {
                oldLocale.save(file);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void loadLocales(File folder) {
        locales.clear();
        File[] files = folder.listFiles();
        if(files == null) return;

        for(File file : files) {
            if(!file.getName().endsWith(".yml")) continue;
            String locale = file.getName().replace(".yml", "");
            locales.put(locale, YamlConfiguration.loadConfiguration(file));
        }
    }

    public static Component get(Player player, String path, boolean prefixed, Object... values) {
        String localeTag = normalizeLocale(player.locale().toString());
        FileConfiguration locale =  locales.get(localeTag);
        if(locale == null) locale = locales.get(fallback);
        String prefix = prefixed ? getLocalizedString(locale, "prefix") + " " : "";
        String message = getLocalizedString(locale, path);
        return miniMessage.deserialize(prefix + message.formatted(values));
    }

    public static Component get(String localeString, String path, boolean prefixed, Object... values) {
        String localeTag = normalizeLocale(localeString);
        FileConfiguration locale =  locales.get(localeTag);
        if(locale == null) locale = locales.get(fallback);
        String prefix = prefixed ? getLocalizedString(locale, "prefix") + " " : "";
        String message = getLocalizedString(locale, path);
        return miniMessage.deserialize(prefix + message.formatted(values));
    }

    public static String console(String path, Object... values) {
        String localeTag = normalizeLocale(fallback);
        FileConfiguration locale =  locales.get(localeTag);
        return getLocalizedString(locale, path).formatted(values);
    }

    private static String normalizeLocale(String locale) {
        locale = locale.replace("_", "-");
        String[] split = locale.split("-");
        if(split.length != 2) return fallback;
        return split[0].toLowerCase() + "-" + split[1].toUpperCase();
    }

    private static String getLocalizedString(FileConfiguration locale, String path) {
        if(locale == null) {
            FileConfiguration fallbackLocale = locales.get(fallback);
            return fallbackLocale != null ? fallbackLocale.getString(path) : path;
        }
        return locale.getString(path);
    }
}