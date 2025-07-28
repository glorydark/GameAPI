package gameapi.utils.music;

import cn.nukkit.Player;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.PlaySoundPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;
import java.util.Map;

/*
    this class is excerpted from MusicPlus,
    which is authored by Nissining,
    while Glorydark has made some tweaks on it for compatibility and usage.
*/
@Data
@EqualsAndHashCode(of = {"songName", "title"})
public class Song {

    public static final Map<Integer, Sound> SOUNDS = new Int2ObjectOpenHashMap<Sound>() {{
        put(0, Sound.NOTE_HARP);
        put(1, Sound.NOTE_BASS);
        put(2, Sound.NOTE_BD);
        put(3, Sound.NOTE_SNARE);
        put(4, Sound.NOTE_HAT);
        put(5, Sound.NOTE_GUITAR);
        put(6, Sound.NOTE_FLUTE);
        put(7, Sound.NOTE_BELL);
        put(8, Sound.NOTE_CHIME);
        put(9, Sound.NOTE_XYLOPHONE);
    }};
    private static final Map<Integer, Float> KEYS = new LinkedHashMap<Integer, Float>() {{
        put(0, 0.5f);
        put(1, 0.529732f);
        put(2, 0.561231f);
        put(3, 0.594604f);
        put(4, 0.629961f);
        put(5, 0.667420f);
        put(6, 0.707107f);
        put(7, 0.749154f);
        put(8, 0.793701f);
        put(9, 0.840896f);
        put(10, 0.890899f);
        put(11, 0.943874f);
        put(12, 1.0f);
        put(13, 1.059463f);
        put(14, 1.122462f);
        put(15, 1.189207f);
        put(16, 1.259921f);
        put(17, 1.334840f);
        put(18, 1.414214f);
        put(19, 1.498307f);
        put(20, 1.587401f);
        put(21, 1.681793f);
        put(22, 1.781797f);
        put(23, 1.887749f);
        put(24, 2.0f);
    }};
    private final Map<Integer, Layer> layerHashMap;
    private final short songHeight;
    private final short length;
    private final String title;
    private final String author;
    private final String description;
    private final float speed;
    private final float delay;
    private final String songName;

    public Song(float speed,
                Map<Integer, Layer> layerHashMap,
                short songHeight,
                final short length,
                String title,
                String author,
                String description,
                String songName) {
        this.speed = speed;
        this.delay = 20 / speed;
        this.layerHashMap = layerHashMap;
        this.songHeight = songHeight;
        this.length = length;
        this.title = title;
        this.author = author;
        this.description = description;
        this.songName = songName;
    }

    public String getFormatSongName(Song targetSong) {
        if (targetSong.equals(this)) {
            return this.getSongName();
        }
        return targetSong.getSongName();
    }

    public void playTick(Player[] players, int tick) {
        for (Layer layer : this.getLayerHashMap().values()) {
            Note note = layer.getNote(tick);
            if (note == null) {
                continue;
            }
            Sound sound = SOUNDS.getOrDefault((int) note.getInstrument(), null);
            float pitch = KEYS.getOrDefault(note.getKey() - 33, 0F);
            for (Player player : players) {
                if (sound != null) {
                    PlaySoundPacket soundPk = new PlaySoundPacket();
                    soundPk.name = sound.getSound();
                    soundPk.volume = layer.getVolume();
                    soundPk.pitch = pitch;
                    soundPk.x = player.getFloorX();
                    soundPk.y = player.getFloorY();
                    soundPk.z = player.getFloorZ();
                    player.dataPacket(soundPk);
                }
            }
        }
    }
}