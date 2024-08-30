package gameapi.manager.music;

import gameapi.GameAPI;
import gameapi.utils.music.NBSDecoder;
import gameapi.utils.music.Song;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author glorydark
 */
public class NBSMusicFactory {

    protected static final Map<String, Song> songs = new LinkedHashMap<>();

    public static void registerSong(File file) {
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));
        if (songs.containsKey(name)) {
            GameAPI.getGameDebugManager().error("Found a duplicated song registering: " + name);
            return;
        }
        Song song = NBSDecoder.parse(name, file);
        if (song != null) {
            songs.put(name, song);
            GameAPI.getGameDebugManager().info("Successfully load nbs song: " + name);
        } else {
            GameAPI.getGameDebugManager().error("Unable to parse nbs music: " + name);
        }
    }

    public static void registerSong(String identifier, File file) {
        if (songs.containsKey(identifier)) {
            GameAPI.getGameDebugManager().error("Found a duplicated song registering: " + identifier);
            return;
        }
        Song song = NBSDecoder.parse(identifier, file);
        if (song != null) {
            songs.put(identifier, song);
            GameAPI.getGameDebugManager().info("Successfully load nbs song: " + identifier);
        } else {
            GameAPI.getGameDebugManager().error("Unable to parse nbs music: " + identifier);
        }
    }

    public static void registerSong(String identifier, InputStream inputStream) {
        if (inputStream == null) {
            GameAPI.getGameDebugManager().error("Found a null inputstream while reading the nbs song: " + identifier);
            return;
        }
        if (songs.containsKey(identifier)) {
            GameAPI.getGameDebugManager().error("Found a duplicated song registering: " + identifier);
            return;
        }
        Song song = NBSDecoder.parse(identifier, inputStream);
        if (song != null) {
            songs.put(identifier, song);
            GameAPI.getGameDebugManager().info("Successfully load nbs song: " + identifier);
        } else {
            GameAPI.getGameDebugManager().info("Parsing " + identifier + " failed! Please use Minecraft Note Block to save the song as classic one!");
        }
    }

    public static Song getSong(String identifier) {
        return songs.get(identifier);
    }

    public static Map<String, Song> getSongs() {
        return songs;
    }
}
