package gameapi.tools;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import gameapi.GameAPI;
import gameapi.utils.GsonAdapter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
                    } else if (!file.delete()) {
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

    public static Map<String, Object> convertConfigToMap(File file) {
        if (file.getName().endsWith(".json")) {
            InputStream stream;
            try {
                stream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8); //一定要以utf-8读取
            JsonReader reader = new JsonReader(streamReader);
            Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Map<String, Object>>() {
            }.getType(), new GsonAdapter()).create();
            Map<String, Object> mainMap = gson.fromJson(reader, new TypeToken<Map<String, Object>>() {
            }.getType());

            // Remember to close the streamReader after your implementation.
            try {
                reader.close();
                streamReader.close();
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return mainMap;
        } else if (file.getName().endsWith(".yml")) {
            return new Config(file, Config.YAML).getAll();
        }
        return new HashMap<>();
    }


}