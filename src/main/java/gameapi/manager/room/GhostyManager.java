package gameapi.manager.room;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import com.google.gson.JsonObject;
import gameapi.GameAPI;
import gameapi.room.Room;
import lombok.Getter;
import net.easecation.ghosty.LevelRecordPack;
import net.easecation.ghosty.recording.LevelRecordEngine;
import net.easecation.ghosty.recording.PlayerRecordEngine;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author glorydark
 * Thanks for Easecation's marvelous Ghosty
 */
@Getter
public class GhostyManager {

    protected Room room;

    protected List<LevelRecordEngine> levelRecordEngines;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public GhostyManager(Room room) {
        this.levelRecordEngines = new ArrayList<>();
        this.room = room;
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
            // levelRecordEngine.removePlayer(player);
        }
    }

    /**
     * These two methods are to support multi-world record
     * for the room that has multiple play levels
     */
    public List<LevelRecordEngine> startMultiWorldRecords(Room room) {
        List<LevelRecordEngine> levelRecordEngines = new ArrayList<>();
        for (Level playLevel : room.getPlayLevels()) {
            this.startWorldRecord(playLevel);
        }
        return levelRecordEngines;
    }

    /**
     * For common purpose, these two methods are okay.
     */
    public void startWorldRecord(Level level) {
        if (this.levelRecordEngines.stream().anyMatch(levelRecordEngine -> levelRecordEngine.getLevel() == level)) {
            GameAPI.getGameDebugManager().info("Already find a recoding in the same room records the same map.");
            return;
        }
        LevelRecordEngine levelRecordEngine = new LevelRecordEngine(level);
        this.levelRecordEngines.add(levelRecordEngine);
    }

    public void stopAllRecords() {
        for (LevelRecordEngine levelRecordEngine : this.levelRecordEngines) {
            levelRecordEngine.stopRecord();
        }
    }

    public void saveWorldRecord(LevelRecordEngine levelRecordEngine, String savePath) {
        saveWorldRecord(levelRecordEngine, savePath, null);
    }

    public void saveWorldRecord(LevelRecordEngine levelRecordEngine, String savePath, Consumer<JsonObject> consumer) {
        File file = new File(savePath);
        List<String> playerNameList = new ArrayList<>();
        for (Player player : this.room.getPlayers()) {
            playerNameList.add(player.getName());
        }
        // end
        try {
            // processing
            LevelRecordPack recordPack = levelRecordEngine.toRecordPack();
            JsonObject metadata = recordPack.getMetadata();
            /*
            metadata.addProperty("roomName", this.room.getRoomName());
            metadata.addProperty("roomWorld", levelRecordEngine.getLevel().getName());
            metadata.addProperty("backup", this.room.getRoomLevelBackup());
            String time = sdf.format(new Date(this.room.getStartMillis()));
            metadata.addProperty("time", time);
             */
            metadata.addProperty("player_list", playerNameList.toString());
            if (consumer != null) {
                consumer.accept(metadata);
            }
            recordPack.packFile(file);
            GameAPI.getInstance().getLogger().info("成功保存录像文件，文件名：" + file.getName() + "，游戏名：" + this.room.getGameName() + ", 参与玩家: " + playerNameList);
        } catch (Throwable t) {
            GameAPI.getGameDebugManager().printError(t);
        }
    }

    public void clearAll() {
        this.stopAllRecords();
        this.levelRecordEngines = new ArrayList<>();
    }

    @Nullable
    public LevelRecordEngine getFirstRecordEngine() {
        if (!levelRecordEngines.isEmpty()) {
            return levelRecordEngines.get(0);
        }
        return null;
    }

    public List<LevelRecordEngine> getLevelRecordEngines() {
        return levelRecordEngines;
    }

    public void recordTitleForAll(String title, String subtitle) {
        this.recordTitleForAll(title, subtitle, 10, 20, 10);
    }

    public void recordTitleForAll(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (LevelRecordEngine levelRecordEngine : levelRecordEngines) {
            this.recordTitle(levelRecordEngine, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public void recordMessageForAll(String message) {
        for (LevelRecordEngine levelRecordEngine : levelRecordEngines) {
            this.recordMessage(levelRecordEngine, message);
        }
    }

    public void recordActionBarForAll(String message) {
        for (LevelRecordEngine levelRecordEngine : levelRecordEngines) {
            this.recordActionBar(levelRecordEngine, message);
        }
    }

    public void recordPopupForAll(String message) {
        for (LevelRecordEngine levelRecordEngine : levelRecordEngines) {
            this.recordPopup(levelRecordEngine, message);
        }
    }

    public void recordCustomEventForAll(JsonObject jsonObject) {
        for (LevelRecordEngine levelRecordEngine : levelRecordEngines) {
            this.recordCustomEvent(levelRecordEngine, jsonObject);
        }
    }

    public void recordTitle(LevelRecordEngine levelRecordEngine, String title, String subtitle) {
        levelRecordEngine.recordTitle(title, subtitle);
    }

    public void recordTitle(LevelRecordEngine levelRecordEngine, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        levelRecordEngine.recordTitle(title, subTitle, fadeIn, stay, fadeOut);
    }

    public void recordMessage(LevelRecordEngine levelRecordEngine, String message) {
        levelRecordEngine.recordMessage(message);
    }

    public void recordActionBar(LevelRecordEngine levelRecordEngine, String message) {
        levelRecordEngine.recordActionBar(message);
    }

    public void recordPopup(LevelRecordEngine levelRecordEngine, String message) {
        levelRecordEngine.recordPopup(message);
    }

    public void recordCustomEvent(LevelRecordEngine levelRecordEngine, JsonObject message) {
        levelRecordEngine.recordCustomEvent(message);
    }
}