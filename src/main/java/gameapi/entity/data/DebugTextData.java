package gameapi.entity.data;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.DebugDrawerPacket;
import cn.nukkit.network.protocol.types.debugshape.DebugShape;
import cn.nukkit.network.protocol.types.debugshape.DebugText;

import java.awt.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 单条 DebugText 浮动文字的数据。
 * <p>
 * 仅 MOT 且客户端协议 >= 1.21.90 可用。
 *
 * @author glorydark
 */
public class DebugTextData {

    private static final AtomicLong debugShapeIdCounter = new AtomicLong(1000000L);

    private final long shapeId;
    private final Location location;
    private final DebugTextSettings settings;
    private final long startMillis;

    /**
     * 跟踪此 DebugText 已发送给哪些玩家（玩家名 → Player）。
     */
    private final Set<String> spawnedPlayers = ConcurrentHashMap.newKeySet();

    private String text;

    public DebugTextData(Location location, String text, DebugTextSettings settings) {
        this.shapeId = debugShapeIdCounter.getAndIncrement();
        this.location = location;
        this.text = text;
        this.settings = settings;
        this.startMillis = System.currentTimeMillis();
    }

    public long getShapeId() {
        return shapeId;
    }

    public Location getLocation() {
        return location;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DebugTextSettings getSettings() {
        return settings;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public Set<String> getSpawnedPlayers() {
        return spawnedPlayers;
    }

    public void spawnToAll() {
        if (!checkLocationReady()) {
            return;
        }
        Map<Long, Player> players = this.location.getLevel().getPlayers();
        if (players.isEmpty()) {
            return;
        }
        DebugDrawerPacket packet = new DebugDrawerPacket();
        packet.shapes.add(createDebugText(this.text));
        for (Player player : players.values()) {
            player.dataPacket(packet);
            spawnedPlayers.add(player.getName());
        }
    }

    public void spawnTo(Player player) {
        if (!checkLocationReady()) {
            return;
        }
        DebugDrawerPacket packet = new DebugDrawerPacket();
        packet.shapes.add(createDebugText(this.text));
        player.dataPacket(packet);
        spawnedPlayers.add(player.getName());
    }

    public void remove() {
        if (this.location.getLevel() == null) {
            return;
        }
        Map<Long, Player> players = this.location.getLevel().getPlayers();
        if (players.isEmpty()) {
            return;
        }
        DebugDrawerPacket packet = new DebugDrawerPacket();
        packet.shapes.add(new DebugShape(shapeId, 0));
        for (Player player : players.values()) {
            player.dataPacket(packet);
        }
        spawnedPlayers.clear();
    }

    public void removeFrom(Player player) {
        DebugDrawerPacket packet = new DebugDrawerPacket();
        packet.shapes.add(new DebugShape(shapeId, 0));
        player.dataPacket(packet);
        spawnedPlayers.remove(player.getName());
    }

    protected boolean checkLocationReady() {
        if (this.location.getLevel() == null || this.location.getLevel().getProvider() == null) {
            return false;
        }
        if (!this.location.getLevel().isChunkLoaded(this.location.getChunkX(), this.location.getChunkZ())) {
            return false;
        }
        return !this.location.getLevel().getPlayers().isEmpty();
    }

    protected DebugText createDebugText(String content) {
        DebugTextSettings s = this.settings;
        Vector3f rotation = s.useRotation() ? new Vector3f(
                s.rotationPitch(),
                s.rotationYaw(),
                0f
        ) : null;
        return new DebugText(
                shapeId,
                0,
                new Vector3f((float) location.x, (float) location.y, (float) location.z),
                s.scale(),
                rotation,
                null,
                null,
                parseColor(s.fontColor()),
                content,
                s.useRotation(),
                parseBackgroundColor(s.backgroundColor()),
                s.depthTest(),
                s.showBackface(),
                s.showTextBackface()
        );
    }

    public static Color parseColor(String hex) {
        if (hex == null || hex.isEmpty()) {
            return null;
        }
        try {
            String clean = hex.startsWith("#") ? hex.substring(1) : hex;
            if (clean.length() == 6) {
                return new Color(Integer.parseInt(clean, 16));
            } else if (clean.length() == 8) {
                int argb = (int) Long.parseLong(clean, 16);
                return new Color(argb, true);
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    public static Color parseBackgroundColor(String hex) {
        Color c = parseColor(hex);
        return c != null ? c : new Color(0, true);
    }
}
