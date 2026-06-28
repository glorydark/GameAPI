package gameapi.room;

import cn.nukkit.entity.EntityHuman;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class FakeRoomPlayer implements RoomPlayer {

    private final EntityHuman entity;
    private final String name;
    private Room room;
    private final Map<String, Object> properties = new LinkedHashMap<>();

    public FakeRoomPlayer(String name, EntityHuman entity, Room room) {
        this.name = name;
        this.entity = entity;
        this.room = room;
    }

    public FakeRoomPlayer(EntityHuman entity, Room room) {
        this(entity.getName(), entity, room);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public EntityHuman getEntity() {
        return this.entity;
    }

    @Override
    public boolean isFake() {
        return true;
    }

    @Override
    @Nullable
    public Room getRoom() {
        return this.room;
    }

    @Override
    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    @Nullable
    public <T> T getProperty(String key) {
        return this.getProperty(key, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, T defaultValue) {
        return (T) this.properties.getOrDefault(key, defaultValue);
    }

    @Override
    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    @Override
    public boolean hasProperty(String key) {
        return this.properties.containsKey(key);
    }

    @Override
    public void removeProperty(String key) {
        this.properties.remove(key);
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }
}
