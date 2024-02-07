package gameapi.tools;

import cn.nukkit.level.Level;
import gameapi.room.Room;
import net.easecation.ghosty.recording.LevelRecordEngine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 * Thanks for Easecation's marvelous Ghosty
 */
public class GhostyTools {

    public static List<LevelRecordEngine> startMultiWorldRecord(Room room) {
        List<LevelRecordEngine> levelRecordEngines = new ArrayList<>();
        for (Level playLevel : room.getPlayLevels()) {
            levelRecordEngines.add(startSingleWorldRecord(playLevel));
        }
        return levelRecordEngines;
    }

    public static void stopMultiWorldRecord(List<LevelRecordEngine> levelRecordEngines, String saveFolder) {
        for (LevelRecordEngine levelRecordEngine : levelRecordEngines) {
            stopSingleWorldRecord(levelRecordEngine, saveFolder + "/" + levelRecordEngine.getLevel().getName());
        }
    }

    public static LevelRecordEngine startSingleWorldRecord(Level level) {
        return new LevelRecordEngine(level);
    }

    public static void stopSingleWorldRecord(LevelRecordEngine levelRecordEngine, String savePath) {
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
