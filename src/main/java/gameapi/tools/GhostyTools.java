package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.utils.Utils;
import com.google.gson.JsonObject;
import gameapi.GameAPI;
import net.easecation.ghosty.LevelRecordPack;
import net.easecation.ghosty.recording.LevelRecordEngine;
import net.easecation.ghosty.recording.PlayerRecordEngine;
import net.easecation.ghosty.recording.player.PlayerRecord;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

/**
 * @author glorydark
 */
public class GhostyTools {

    public static LevelRecordPack getLevelRecordPack(File file) {
        LevelRecordPack recordPack;
        try {
            recordPack = LevelRecordPack.unpackFile(file);
        } catch (Throwable e) {
            GameAPI.getGameDebugManager().error(file.getName() + " has found error while playing!");
            return null;
        }
        return recordPack;
    }

    public static PlayerRecord getPlayerRecordPack(File file) {
        PlayerRecord recordPack;
        try {
            recordPack = PlayerRecord.fromBinary(Files.readAllBytes(file.toPath()));
        } catch (Throwable e) {
            GameAPI.getGameDebugManager().error(file.getName() + " has found error while playing!");
            GameAPI.getGameDebugManager().printError(e);
            return null;
        }
        return recordPack;
    }

    @Deprecated
    public static void savePlayerRecordPack(PlayerRecord record, File file) {
        savePlayerRecord(record, file);
    }

    public static void savePlayerRecord(PlayerRecord record, File file) {
        try {
            Utils.writeFile(file, new ByteArrayInputStream(record.toBinary()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PlayerRecordEngine startPlayerRecord(Player player) {
        return new PlayerRecordEngine(player);
    }

    public static void saveLevelRecord(LevelRecordEngine levelRecordEngine, String savePath, Consumer<JsonObject> consumer) {
        File file = new File(savePath);
        // end
        try {
            // processing
            LevelRecordPack recordPack = levelRecordEngine.toRecordPack();
            JsonObject metadata = recordPack.getMetadata();
            if (consumer != null) {
                consumer.accept(metadata);
            }
            recordPack.packFile(file);
            GameAPI.getInstance().getLogger().info("成功保存录像文件，文件名：" + file.getName());
        } catch (Throwable t) {
            GameAPI.getGameDebugManager().printError(t);
        }
    }
}
