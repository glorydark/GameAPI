package gameapi.extensions.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import gameapi.annotation.Future;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author glorydark
 */
@Future
public class GameTask extends AbstractGameTask {

    private List<String> commands;

    private List<String> messages;

    private LinkedHashMap<String, Map<String, Object>> taskCache;

    public GameTask(String gameName, String taskIdentifier, String taskDisplayName, String description, List<String> commands, List<String> messages) {
        super(gameName, taskIdentifier, taskDisplayName, description);
        this.commands = commands;
        this.messages = messages;
    }

    public Object getPlayerTaskCache(Player player, String key) {
        return getPlayerTaskCache(player.getName(), key);
    }

    public Object getPlayerTaskCache(String player, String key) {
        return taskCache.getOrDefault(player, new LinkedHashMap<>()).getOrDefault(key, null);
    }

    public Object getPlayerTaskCache(Player player, String key, Object defaultValue) {
        return getPlayerTaskCache(player.getName(), key, defaultValue);
    }

    public Object getPlayerTaskCache(String player, String key, Object defaultValue) {
        return taskCache.getOrDefault(player, new LinkedHashMap<>()).getOrDefault(key, defaultValue);
    }

    public void setPlayerTaskCache(Player player, String key, Object value) {
        setPlayerTaskCache(player.getName(), key, value);
    }

    public void setPlayerTaskCache(String player, String key, Object value) {
        if (!taskCache.containsKey(player)) {
            taskCache.put(player, new LinkedHashMap<>());
        }
        taskCache.get(player).put(key, value);
    }

    @Override
    public boolean isEnabled(Player player) {
        return true;
    }

    @Override
    public boolean isFinished(Player player) {
        if (this.isEnabled(player)) {
            for (String command : commands) {
                Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), command.replace("%player%", "\"" + player.getName() + "\""));
            }
            for (String message : messages) {
                player.sendMessage(message.replace("%player%", "\"" + player.getName() + "\""));
            }
            doAfterFinish(player);
            return true;
        }
        return false;
    }

    public void doAfterFinish(Player player) {
        setPlayerTaskCache(player, "times", ((Integer) getPlayerTaskCache(player, "times", 0)) + 1);
        setPlayerTaskCache(player, "last_finish_millis", System.currentTimeMillis());
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public LinkedHashMap<String, Map<String, Object>> getTaskCache() {
        return taskCache;
    }

    public void setTaskCache(LinkedHashMap<String, Map<String, Object>> taskCache) {
        this.taskCache = taskCache;
    }
}
