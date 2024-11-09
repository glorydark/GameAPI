package gameapi.manager.data.activity;

import cn.nukkit.utils.ConfigSection;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
@AllArgsConstructor
public class ActivityPlayerDataCache {

    private ConfigSection section;

    private long createTime;

    protected static final long EXPIRE_MILLIS = 300000L;

    public boolean isExpired() {
        return System.currentTimeMillis() - this.createTime >= EXPIRE_MILLIS;
    }
}
