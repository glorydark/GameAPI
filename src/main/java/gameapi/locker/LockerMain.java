package gameapi.locker;

import gameapi.GameAPI;
import gameapi.locker.entry.LockerEntry;

import java.util.LinkedHashMap;

/**
 * @author glorydark
 * @date {2024/1/2} {11:00}
 */
public class LockerMain {

    protected final LinkedHashMap<String, LockerEntry> loadedEntries = new LinkedHashMap<>();

    public void init() {
        // todo: add vanilla entries that fits mini-games
    }

    public void registerLockerEntry(String identifier, LockerEntry entry) {
        if (loadedEntries.containsKey(identifier)) {
            GameAPI.plugin.getLogger().warning("Already registered a locker named: " + identifier);
            return;
        }
        loadedEntries.put(identifier, entry);
    }

    public LockerEntry getLockerEntry(String identifier) {
        return loadedEntries.get(identifier);
    }
}
