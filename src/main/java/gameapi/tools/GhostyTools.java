package gameapi.tools;

import gameapi.GameAPI;
import net.easecation.ghosty.LevelRecordPack;

import java.io.File;

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
            e.printStackTrace();
            return null;
        }
        return recordPack;
    }
}
