package gameapi.utils;

import cn.nukkit.utils.Utils;
import gameapi.GameAPI;

import java.io.File;
import java.io.IOException;

/**
 * @author lt_name (CrystalWar)
 */
public class FileUtil {

    public static boolean delete(File file) {
        try {
            if (!file.exists()) {
                return true;
            }
            File[] files = file.listFiles();
            if (files != null) {
                for (File getFile : files) {
                    if (getFile.isDirectory()) {
                        File[] listFiles = getFile.listFiles();
                        if(listFiles != null){
                            for (File listFile : listFiles) {
                                delete(listFile);
                            }
                        }
                    }else{
                        return getFile.delete();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            GameAPI.plugin.getLogger().error(GameAPI.getLanguage().getTranslation("file.delete.error", file.getName()));
            //e.printStackTrace();
        }
        return false;
    }

    public static boolean copy(String from, String to) {
        return copy(new File(from), new File(to));
    }

    public static boolean copy(File from, File to) {
        try {
            File [] files = from.listFiles();
            if (files != null) {
                if (!to.exists() && !to.mkdirs()) {
                    throw new IOException(GameAPI.getLanguage().getTranslation("file.folder.create.failed", to.getName()));
                }
                for (File file : files) {
                    if (file.isDirectory()) {
                        copy(file, new File(to, file.getName()));
                    }else {
                        Utils.copyFile(file, new File(to, file.getName()));
                    }
                }
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}