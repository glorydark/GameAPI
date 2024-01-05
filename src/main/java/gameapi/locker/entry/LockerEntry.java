package gameapi.locker.entry;

import lombok.Builder;
import lombok.Getter;

/**
 * @author glorydark
 * @date {2024/1/2} {11:00}
 */
@Builder
@Getter
public class LockerEntry {

    String name;

    public LockerEntry(String name) {
        this.name = name;
    }
}
