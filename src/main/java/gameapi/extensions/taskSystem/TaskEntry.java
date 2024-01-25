package gameapi.extensions.taskSystem;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import gameapi.annotation.Future;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author glorydark
 */
@Future
public class TaskEntry {

    String gameName;

    String taskIdentifier;

    String name;

    String description;

    long intervalMillis; // -1 means that the task can be finished once

    List<String> commands;

    List<String> messages;

    int requiredTaskPoints;

    int finishLimits;

    File file = null;

    protected LinkedHashMap<String, Integer> playerTaskPointCache = new LinkedHashMap<>();

    protected LinkedHashMap<String, Long> playerFinishMillisPointCache = new LinkedHashMap<>();

    protected LinkedHashMap<String, Integer> playerFinishTimesCache = new LinkedHashMap<>();

    public TaskEntry fromConfig(String gameName, String taskIdentifier, File file) {
        if (file.exists()) {
            this.file = file;
            Config config = new Config(file, Config.YAML);
            return new TaskEntry(gameName, taskIdentifier, config.getString("name", ""),
                    config.getString("description", ""),
                    config.getInt("interval_millis", -1),
                    config.getInt("finish_limits", -1),
                    new ArrayList<>(config.getStringList("commands")),
                    new ArrayList<>(config.getStringList("messages")));
        } else {
            return null;
        }
    }

    public TaskEntry(String gameName, String taskIdentifier, String name, String description, int intervalMillis, int finishLimits, List<String> commands, List<String> messages) {
        this.gameName = gameName;
        this.taskIdentifier = taskIdentifier;
        this.name = name;
        this.description = description;
        this.intervalMillis = intervalMillis;
        this.finishLimits = finishLimits;
        this.commands = commands;
        this.messages = messages;
        File taskCacheFile = new File(GameAPI.path + "/task_caches/" + gameName + "/" + taskIdentifier + ".yml");
        if (taskCacheFile.exists()) {
            Config taskCacheConfig = new Config(taskCacheFile, Config.YAML);
            this.playerTaskPointCache = taskCacheConfig.get("player_points_caches", new LinkedHashMap<>());
            this.playerFinishMillisPointCache = taskCacheConfig.get("player_finished_time_caches", new LinkedHashMap<>());
            this.playerFinishTimesCache = taskCacheConfig.get("player_finished_times_cache", new LinkedHashMap<>());
        }
    }

    public boolean canFinish(Player player) {
        String playerName = player.getName();
        if (playerTaskPointCache.getOrDefault(playerName, 0) < requiredTaskPoints) {
            return false;
        }
        if (intervalMillis == -1) {
            if (this.playerFinishMillisPointCache.containsKey(playerName)) {
                return false;
            }
        } else if (intervalMillis > 0) {
            return System.currentTimeMillis() - this.playerFinishMillisPointCache.getOrDefault(playerName, 0L) >= intervalMillis;
        }
        if (finishLimits > 0) {
            return this.playerFinishTimesCache.getOrDefault(playerName, 0) < finishLimits;
        }
        return true;
    }

    public void execute(Player player) {
        for (String command : commands) {
            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), command.replace("%player%", "\"" + player.getName() + "\""));
        }
        for (String message : messages) {
            player.sendMessage(message.replace("%player%", "\"" + player.getName() + "\""));
        }
    }

    public void addFinishRecord(Player player) {
        String playerName = player.getName();
        int playerFinishTimes = playerFinishTimesCache.getOrDefault(playerName, 0) + 1;
        playerFinishTimesCache.put(playerName, playerFinishTimes);
        int playerTaskPoints = playerTaskPointCache.getOrDefault(playerName, 0) - requiredTaskPoints;
        playerTaskPointCache.put(playerName, playerTaskPoints);
        long playerFinishMillis = System.currentTimeMillis();
        playerFinishMillisPointCache.put(playerName, playerFinishMillis);
        if (file != null) {
            Config cacheConfig = new Config(file, Config.YAML);
            cacheConfig.getSection("player_finished_times_cache").set(playerName, playerFinishTimes);
            cacheConfig.getSection("player_points_caches").set(playerName, playerTaskPoints);
            cacheConfig.getSection("player_finished_time_caches").set(playerName, playerFinishMillis);
        }
    }
}
