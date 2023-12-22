package gameapi.utils;

import cn.nukkit.utils.Utils;
import gameapi.GameAPI;

import java.io.File;
import java.io.IOException;

/**
 * @author lt_name (CrystalWar)
 */
public class FileUtil {

    public static boolean delete(File deleteFile) {
        try {
            if (!deleteFile.exists()) {
                return true;
            }
            File[] files = deleteFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        delete(file);
                    }else if (!file.delete()) {
                        throw new IOException(GameAPI.getLanguage().getTranslation("file.delete.error", file.getName()));
                    }
                }
            }
            if (!deleteFile.delete()) {
                throw new IOException(GameAPI.getLanguage().getTranslation("file.delete.error", deleteFile.getName()));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copy(String from, String to) {
        return copy(new File(from), new File(to));
    }

    public static boolean copy(File from, File to) {
        try {
            File[] files = from.listFiles();
            if (files != null) {
                if (!to.exists() && !to.mkdirs()) {
                    throw new IOException(GameAPI.getLanguage().getTranslation("file.folder.create.failed", to.getName()));
                }
                for (File file : files) {
                    if (file.isDirectory()) {
                        copy(file, new File(to, file.getName()));
                    } else {
                        Utils.copyFile(file, new File(to, file.getName()));
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}