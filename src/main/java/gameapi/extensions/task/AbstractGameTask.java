package gameapi.extensions.task;

import cn.nukkit.Player;

/**
 * @author glorydark
 */
public abstract class AbstractGameTask {

    private final String gameName;

    private final String taskIdentifier;

    private final String name;

    private final String description;

    public AbstractGameTask(String gameName, String taskIdentifier, String name, String description) {
        this.gameName = gameName;
        this.taskIdentifier = taskIdentifier;
        this.name = name;
        this.description = description;
    }

    public boolean isFinished(Player player) {
        return true;
    }

    public boolean isEnabled(Player player) {
        return true;
    }

    public void finish() {

    }

    public void save() {

    }

    public String getTaskIdentifier() {
        return taskIdentifier;
    }

    public String getGameName() {
        return gameName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
