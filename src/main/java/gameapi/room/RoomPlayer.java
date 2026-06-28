package gameapi.room;

import cn.nukkit.entity.EntityHuman;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

public interface RoomPlayer {

    String getName();

    EntityHuman getEntity();

    boolean isFake();

    @Nullable
    Room getRoom();

    void setRoom(Room room);

    @Nullable
    <T> T getProperty(String key);

    <T> T getProperty(String key, T defaultValue);

    void setProperty(String key, Object value);

    boolean hasProperty(String key);

    void removeProperty(String key);

    Map<String, Object> getProperties();
}
