package gameapi.utils.music;

import cn.nukkit.Player;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
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

    public static final Map<Integer, InstrumentData> SOUNDS = new Int2ObjectOpenHashMap<>() {{
        put(0, new InstrumentData(Sound.NOTE_HARP));
        put(1, new InstrumentData(Sound.NOTE_BD));
        put(2, new InstrumentData(Sound.NOTE_SNARE));
        put(3, new InstrumentData(Sound.NOTE_HAT));
        put(4, new InstrumentData(Sound.NOTE_BASS));
        put(5, new InstrumentData(Sound.NOTE_BELL, ProtocolInfo.v1_12_0, Sound.NOTE_HARP));
        put(6, new InstrumentData(Sound.NOTE_FLUTE, ProtocolInfo.v1_12_0, Sound.NOTE_HARP));
        put(7, new InstrumentData(Sound.NOTE_CHIME, ProtocolInfo.v1_12_0, Sound.NOTE_HARP));
        put(8, new InstrumentData(Sound.NOTE_GUITAR, ProtocolInfo.v1_12_0, Sound.NOTE_HARP));
        put(9, new InstrumentData(Sound.NOTE_XYLOPHONE, ProtocolInfo.v1_12_0, Sound.NOTE_HARP));
        put(10, new InstrumentData(Sound.NOTE_IRON_XYLOPHONE, ProtocolInfo.v1_12_0, Sound.NOTE_HARP));
        put(11, new InstrumentData(Sound.NOTE_COW_BELL, ProtocolInfo.v1_12_0, Sound.NOTE_HARP));
        put(12, new InstrumentData(Sound.NOTE_DIDGERIDOO, ProtocolInfo.v1_12_0, Sound.NOTE_HARP));
        put(13, new InstrumentData(Sound.NOTE_BIT, ProtocolInfo.v1_12_0, Sound.NOTE_HARP));
        put(14, new InstrumentData(Sound.NOTE_BANJO, ProtocolInfo.v1_12_0, Sound.NOTE_HARP));
        put(15, new InstrumentData(Sound.NOTE_PLING, ProtocolInfo.v1_12_0, Sound.NOTE_HARP));
        put(16, new InstrumentData("block.note_block.trumpet", ProtocolInfo.v1_26_10, Sound.NOTE_HARP));
        put(17, new InstrumentData("block.note_block.trumpet_exposed", ProtocolInfo.v1_26_10, Sound.NOTE_HARP));
        put(18, new InstrumentData("block.note_block.trumpet_weathered", ProtocolInfo.v1_26_10, Sound.NOTE_HARP));
        put(19, new InstrumentData("block.note_block.trumpet_oxidized", ProtocolInfo.v1_26_10, Sound.NOTE_HARP));
    }};

    private static final Map<Integer, Float> KEYS = new LinkedHashMap<>() {{
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
            InstrumentData sound = SOUNDS.getOrDefault((int) note.getInstrument(), null);
            float pitch = KEYS.getOrDefault(note.getKey() - 33, 0F);
            for (Player player : players) {
                if (sound != null) {
                    PlaySoundPacket soundPk = new PlaySoundPacket();
                    soundPk.name = sound.getSoundId(player);
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