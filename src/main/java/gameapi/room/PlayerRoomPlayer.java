package gameapi.room;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityHuman;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerRoomPlayer implements RoomPlayer {

    private final Player player;
    private Room room;

    public PlayerRoomPlayer(Player player, Room room) {
        this.player = player;
        this.room = room;
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public EntityHuman getEntity() {
        return this.player;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public boolean isFake() {
        return false;
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
        if (this.room == null) return defaultValue;
        return this.room.getPlayerProperty(this.getName(), key, defaultValue);
    }

    @Override
    public void setProperty(String key, Object value) {
        if (this.room == null) return;
        this.room.setPlayerProperty(this.getName(), key, value);
    }

    @Override
    public boolean hasProperty(String key) {
        return this.room != null && this.room.hasPlayerProperty(this.getName(), key);
    }

    @Override
    public void removeProperty(String key) {
        if (this.room == null) return;
        this.room.removePlayerProperty(this.getName(), key);
    }

    @Override
    public Map<String, Object> getProperties() {
        if (this.room == null) return new LinkedHashMap<>();
        Map<String, Object> props = this.room.getPlayerProperties().get(this.getName());
        return props != null ? props : new LinkedHashMap<>();
    }
}
