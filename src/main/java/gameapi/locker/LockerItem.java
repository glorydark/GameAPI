package gameapi.locker;

import cn.nukkit.event.player.PlayerEvent;

/**
 * @author glorydark
 * @date {2023/12/23} {13:21}
 */
public interface LockerItem {

    LockerItemType getType();

    void respondEvent(PlayerEvent event);
}
