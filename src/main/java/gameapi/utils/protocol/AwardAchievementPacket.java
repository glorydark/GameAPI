package gameapi.utils.protocol;

import cn.nukkit.network.protocol.DataPacket;
import lombok.ToString;

/**
 * @since 685
 */
@ToString
public class AwardAchievementPacket extends DataPacket {

    public static final int NETWORK_ID = 309;

    public int achievementId;

    @Override
    public void decode() {
        this.achievementId = this.getLInt();
    }

    @Override
    public void encode() {
        this.putLInt(this.achievementId);
    }

    @Override
    public int packetId() {
        return NETWORK_ID;
    }

    @Override
    public byte pid() {
        throw new UnsupportedOperationException("Not supported.");
    }
}