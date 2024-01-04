package gameapi.locker;

import cn.nukkit.event.player.PlayerEvent;
import gameapi.annotation.Future;

/**
 * @author glorydark
 * @date {2023/12/23} {13:21}
 */
@Future
public interface LockerItem {

    LockerEntryType getType();

    void respondEvent(PlayerEvent event);
}
