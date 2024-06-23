package gameapi.manager.extensions;

import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import gameapi.annotation.Future;
import gameapi.extensions.task.GameTask;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author glorydark
 */
@Future
public class GameTaskManager {

    protected static LinkedHashMap<String, LinkedHashMap<String, GameTask>> tasks = new LinkedHashMap<>();

    public static void registerTask(GameTask gameTask) {
        String gameName = gameTask.getGameName();
        String taskIdentifier = gameTask.getTaskIdentifier();
        if (!tasks.containsKey(gameName)) {
            tasks.put(gameName, new LinkedHashMap<>());
        }
        tasks.get(gameName).put(taskIdentifier, gameTask);
    }

    public static GameTask getTask(String gameName, String taskIdentifier) {
        return tasks.getOrDefault(gameName, new LinkedHashMap<>()).getOrDefault(taskIdentifier, null);
    }

    public static void saveAllData() {
        String path = GameAPI.path + "/task_caches/";
        for (Map.Entry<String, LinkedHashMap<String, GameTask>> entry : tasks.entrySet()) {
            File gameDir = new File(path + "/" + entry.getKey());
            gameDir.mkdirs();
            for (Map.Entry<String, GameTask> taskEntry : entry.getValue().entrySet()) {
                Config config = new Config(gameDir.getPath() + "/" + taskEntry.getKey() + ".yml", Config.YAML);
                for (Map.Entry<String, Map<String, Object>> cacheEntry : taskEntry.getValue().getTaskCache().entrySet()) {
                    config.set(cacheEntry.getKey(), cacheEntry.getValue());
                }
                config.save();
            }
        }
    }

    public static void close() {
        tasks.clear();
    }
}
