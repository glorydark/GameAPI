package gameapi.manager.music.data;

import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class OggMusicData {

    private final float volume;

    private final float pitch;

    private final int length;

    public OggMusicData(float volume, float pitch, int length) {
        this.volume = volume;
        this.pitch = pitch;
        this.length = length;
    }
}
