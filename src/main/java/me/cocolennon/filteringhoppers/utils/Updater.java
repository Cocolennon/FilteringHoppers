package me.cocolennon.filteringhoppers.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

public class Updater {
    private static final String USER_AGENT = "Spigot Plugin Updater";
    private String downloadLink;
    private Plugin plugin;
    private File updateFolder;
    private File file;
    private int id;
    private int page = 1;
    private UpdateType updateType;
    private Result result = Result.SUCCESS;
    private boolean emptyPage;
    private String version;
    private boolean logger;
    private Thread thread;

    private static final String DOWNLOAD = "/download";
    private static final String VERSIONS = "/versions";
    private static final String PAGE = "?page=";
    private static final String API_RESOURCE = "https://api.spiget.org/v2/resources/";

    public Updater(Plugin plugin, int id, File file, UpdateType updateType, boolean logger) {
        this.plugin = plugin;
        plugin.getServer().getUpdateFolderFile().mkdir();
        this.updateFolder = plugin.getServer().getUpdateFolderFile();
        this.id = id;
        this.file = file;
        this.updateType = updateType;
        this.logger = logger;
        downloadLink = API_RESOURCE + id;
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

    private boolean checkResource(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);
            int code = connection.getResponseCode();
            if(code != 200) {
                connection.disconnect();
                result = Result.BAD_ID;
                return false;
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void checkUpdate() {
        try {
            String page = Integer.toString(this.page);
            URL url = new URL(API_RESOURCE+id+VERSIONS+PAGE+page);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);
            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            JsonElement element = new JsonParser().parse(reader);
            JsonArray jsonArray = element.getAsJsonArray();
            if(jsonArray.size() == 10 && !emptyPage) {
                connection.disconnect();
                this.page++;
                checkUpdate();
            } else if(jsonArray.size() == 0) {
                emptyPage = true;
                this.page--;
                checkUpdate();
            } else if(jsonArray.size() < 10) {
                element = jsonArray.get(jsonArray.size()-1);
                JsonObject object = element.getAsJsonObject();
                element = object.get("name");
                version = element.toString().replaceAll("\"", "").replace("v","");
                if(logger) plugin.getLogger().info("Checking for update...");
                if(shouldUpdate(version, plugin.getDescription().getVersion()) && updateType == UpdateType.VERSION_CHECK) {
                    result = Result.UPDATE_FOUND;
                    if(logger)
                        plugin.getLogger().info("Update found!");
                } else if(updateType == UpdateType.DOWNLOAD) {
                    if(logger) plugin.getLogger().info("Downloading update... version not checked");
                    download();
                } else if(updateType == UpdateType.CHECK_DOWNLOAD) {
                    if(shouldUpdate(version, plugin.getDescription().getVersion())) {
                        if(logger) plugin.getLogger().info("Update found, downloading now...");
                        download();
                    } else {
                        if(logger) plugin.getLogger().info("Update not found");
                        result = Result.NO_UPDATE;
                    }
                } else {
                    if(logger) plugin.getLogger().info("Update not found");
                    result = Result.NO_UPDATE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean shouldUpdate(String newVersion, String oldVersion) {
        return !newVersion.equalsIgnoreCase(oldVersion);
    }

    private void download() {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            URL url = new URL(downloadLink);
            in = new BufferedInputStream(url.openStream());
            fout = new FileOutputStream(new File(updateFolder, file.getName()));
            final byte[] data = new byte[4096];
            int count;
            while ((count = in.read(data, 0, 4096)) != -1) {
                fout.write(data, 0, count);
            }
        } catch (Exception e) {
            if(logger) plugin.getLogger().log(Level.SEVERE, "Updater tried to download the update, but was unsuccessful.\n" + e.toString());
            result = Result.FAILED;
        } finally {
            try {
                if (in != null) in.close();
            } catch (final IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, null, e);
                e.printStackTrace();
            } try {
                if (fout != null) fout.close();
            } catch (final IOException e) {
                e.printStackTrace();
                this.plugin.getLogger().log(Level.SEVERE, null, e);
            }
        }
    }

    private void waitThread()
    {
        if(thread != null && thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                this.plugin.getLogger().log(Level.SEVERE, null, e);
            }
        }
    }

    public class UpdaterRunnable implements Runnable {
        public void run() {
            if(checkResource(downloadLink)) {
                downloadLink = downloadLink + DOWNLOAD;
                checkUpdate();
            }
        }
    }
}
