package gameapi.extensions.locker;

import cn.nukkit.entity.Entity;
import gameapi.annotation.Future;
import lombok.Builder;
import lombok.Getter;

/**
 * @author glorydark
 * @date {2024/1/2} {11:00}
 */
@Future
@Builder
@Getter
public class LockerEntry {

    String name;

    public LockerEntry(String name) {
        this.name = name;
    }

    public void use(Entity entity) {

    }
}
