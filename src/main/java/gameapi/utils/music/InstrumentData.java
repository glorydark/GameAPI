package gameapi.utils.music;

import cn.nukkit.Player;
import cn.nukkit.level.Sound;

/**
 * @author glorydark
 */
public class InstrumentData {

    private final String soundId;
    private final int minimumProtocol;
    private final String replacedSound;

    public InstrumentData(Sound sound) {
        this(sound.getSound(), -1, "");
    }

    public InstrumentData(String soundId) {
        this(soundId, -1, "");
    }

    public InstrumentData(Sound sound, int minimumProtocol, Sound replacedSound) {
        this(sound.getSound(), minimumProtocol, replacedSound.getSound());
    }

    public InstrumentData(String soundId, int minimumProtocol, Sound replacedSound) {
        this(soundId, minimumProtocol, replacedSound.getSound());
    }

    public InstrumentData(String soundId, int minimumProtocol, String replacedSound) {
        this.soundId = soundId;
        this.minimumProtocol = minimumProtocol;
        this.replacedSound = replacedSound;
    }

    public String getSoundId(Player player) {
        if (this.minimumProtocol == -1 || player.protocol >= this.minimumProtocol) {
            return this.soundId;
        } else {
            return this.replacedSound;
        }
    }
}
