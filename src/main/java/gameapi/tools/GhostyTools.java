package gameapi.tools;

import cn.nukkit.utils.Utils;
import gameapi.GameAPI;
import net.easecation.ghosty.LevelRecordPack;
import net.easecation.ghosty.recording.player.PlayerRecord;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

    public static void savePlayerRecordPack(PlayerRecord record, File file) {
        try {
            Utils.writeFile(file, new ByteArrayInputStream(record.toBinary()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
