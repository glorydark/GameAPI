package gameapi.manager.room;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import gameapi.manager.GameDebugManager;
import gameapi.room.Room;
import lombok.Getter;
import net.easecation.ghosty.recording.LevelRecordEngine;
import net.easecation.ghosty.recording.PlayerRecordEngine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 * Thanks for Easecation's marvelous Ghosty
 */
@Getter
public class GhostyManager {

    protected List<LevelRecordEngine> levelRecordEngines;

    public GhostyManager() {
        this.levelRecordEngines = new ArrayList<>();
    }

    /**
     * When finding a player leaving the room,
     * you should execute this method at the time,
     * which will reduce the possibility of causing errors like NPE and so on.
     */
    public void stopRecordingPlayer(Player player) {
        for (LevelRecordEngine levelRecordEngine : this.levelRecordEngines) {
            PlayerRecordEngine playerRecordEngine = levelRecordEngine.getPlayerRecordEngines().get(player);
            if (playerRecordEngine != null) {
                playerRecordEngine.stopRecord();
            }
            levelRecordEngine.removePlayer(player);
        }
    }

    /**
     * These two methods are to support multi-world record
     * for the room that has multiple play levels
     */
    public List<LevelRecordEngine> startMultiWorldRecord(Room room) {
        List<LevelRecordEngine> levelRecordEngines = new ArrayList<>();
        for (Level playLevel : room.getPlayLevels()) {
            this.startWorldRecord(playLevel);
        }
        return levelRecordEngines;
    }

    public void stopMultiWorldRecord(List<LevelRecordEngine> levelRecordEngines, String saveFolder) {
        for (LevelRecordEngine levelRecordEngine : levelRecordEngines) {
            stopAndSaveWorldRecord(levelRecordEngine, saveFolder + "/" + levelRecordEngine.getLevel().getName());
        }
    }

    /**
     * For common purpose, these two methods are okay.
     */
    public void startWorldRecord(Level level) {
        if (this.levelRecordEngines.stream().anyMatch(levelRecordEngine -> levelRecordEngine.getLevel() == level)) {
            GameDebugManager.info("Already find a recoding in the same room records the same map.");
            return;
        }
        this.levelRecordEngines.add(new LevelRecordEngine(level));
    }

    public void stopAndSaveWorldRecord(LevelRecordEngine levelRecordEngine, String savePath) {
        levelRecordEngine.stopRecord();
        File file = new File(savePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(levelRecordEngine.getLevelRecord().toBinary());
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
