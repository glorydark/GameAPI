package gameapi.extensions.taskSystem;

import gameapi.annotation.Future;

import java.util.LinkedHashMap;

/**
 * @author glorydark
 */
@Future
public class TaskManager {

    public LinkedHashMap<String, TaskEntry> tasks = new LinkedHashMap<>();

    public void registerTaskEntry(TaskEntry taskEntry) {
        tasks.put(taskEntry.gameName + ":" + taskEntry.taskIdentifier, taskEntry);
    }

    //todo: use AdvancedForm to showcase all tasks in forms
}
