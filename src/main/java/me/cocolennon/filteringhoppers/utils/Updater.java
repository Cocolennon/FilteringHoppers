package me.cocolennon.filteringhoppers.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;

public class Updater {
    private static final String USER_AGENT = "Spigot Plugin Updater";
    private static final String API_BASE = "https://api.modrinth.com/v2/project/";

    private Plugin plugin;
    private String id;
    private File file;
    private UpdateType updateType;
    private boolean logger;

    private File updateFolder;
    private Result result = Result.SUCCESS;
    private String version;
    private String downloadUrl;
    private Thread thread;

    public Updater(Plugin plugin, String id, File file, UpdateType updateType, boolean logger) {
        this.plugin = plugin;
        this.id = id;
        this.file = file;
        this.updateType = updateType;
        this.logger = logger;
        plugin.getServer().getUpdateFolderFile().mkdir();
        this.updateFolder = plugin.getServer().getUpdateFolderFile();
        thread = new Thread(new UpdaterRunnable());
        thread.start();
    }

    public enum UpdateType {
        VERSION_CHECK,
        DOWNLOAD,
        CHECK_DOWNLOAD
    }

    public enum Result {
        UPDATE_FOUND,
        NO_UPDATE,
        SUCCESS,
        FAILED,
        BAD_ID
    }

    public Result getResult() {
        waitThread();
        return result;
    }

    public String getVersion() {
        waitThread();
        return version;
    }

    private void checkUpdate() {
        try {
            URL url = URI.create(API_BASE + id + "/version").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);
            if(connection.getResponseCode() != 200) {
                connection.disconnect();
                result = Result.BAD_ID;
                return;
            }
            JsonArray versions = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonArray();
            connection.disconnect();
            if(versions.isEmpty()) {
                result = Result.BAD_ID;
                return;
            }
            JsonObject latest = versions.get(0).getAsJsonObject();
            version = latest.get("version_number").getAsString();
            downloadUrl = latest.get("files").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
            if(logger) plugin.getLogger().info("Checking for update...");
            boolean updateAvailable = shouldUpdate(version, plugin.getPluginMeta().getVersion());
            switch(updateType) {
                case VERSION_CHECK -> {
                    if(updateAvailable) {
                        result = Result.UPDATE_FOUND;
                        if(logger) plugin.getLogger().info("Update found! New version: " + version);
                    }else{
                        result = Result.NO_UPDATE;
                        if(logger) plugin.getLogger().info("No update found.");
                    }
                }
                case DOWNLOAD -> {
                    if(logger) plugin.getLogger().info("Downloading update (version not checked)...");
                    download();
                }
                case CHECK_DOWNLOAD -> {
                    if(updateAvailable) {
                        if(logger) plugin.getLogger().info("Update found, downloading now...");
                        download();
                    }else{
                        result = Result.NO_UPDATE;
                        if(logger) plugin.getLogger().info("No update found.");
                    }
                }
            }
        }catch(Exception exception) {
            if (logger) plugin.getLogger().log(Level.SEVERE, "Failed to download update: " + exception.getMessage());
            result = Result.FAILED;
        }
    }

    private boolean shouldUpdate(String newVersion, String oldVersion) {
        return !newVersion.equalsIgnoreCase(oldVersion);
    }

    private void download() {
        try (BufferedInputStream in = new BufferedInputStream(URI.create(downloadUrl).toURL().openStream());
             FileOutputStream fout = new FileOutputStream(new File(updateFolder, file.getName()))) {
            byte[] data = new byte[4096];
            int count;
            while ((count = in.read(data, 0, 4096)) != -1) {
                fout.write(data, 0, count);
            }
            result = Result.SUCCESS;
        } catch (Exception exception) {
            if (logger) plugin.getLogger().log(Level.SEVERE, "Failed to download update: " + exception.getMessage());
            result = Result.FAILED;
        }
    }

    private void waitThread() {
        if(thread != null && thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException exception) {
                this.plugin.getLogger().log(Level.SEVERE, null, exception);
            }
        }
    }

    public class UpdaterRunnable implements Runnable {
        public void run() {
            checkUpdate();
        }
    }
}
