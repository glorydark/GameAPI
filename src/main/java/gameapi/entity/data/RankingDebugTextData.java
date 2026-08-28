package gameapi.entity.data;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.DebugDrawerPacket;
import cn.nukkit.network.protocol.types.debugshape.DebugText;
import gameapi.ranking.Ranking;

import java.util.Map;

/**
 * 排行榜专用 DebugText 数据。
 * <p>
 * 每个玩家看到各自翻译后的内容，通过 {@link Ranking#getDisplayContent(Player)} 生成。
 * 仅 MOT 且客户端协议 >= 1.21.90 可用。
 *
 * @author glorydark
 */
public class RankingDebugTextData extends DebugTextData {

    private final Ranking ranking;
    private long lastUpdateMillis;

    public RankingDebugTextData(Location location, Ranking ranking, DebugTextSettings settings) {
        super(location, "", settings);
        this.ranking = ranking;
    }

    public Ranking getRanking() {
        return ranking;
    }

    public long getLastUpdateMillis() {
        return lastUpdateMillis;
    }

    public void setLastUpdateMillis(long lastUpdateMillis) {
        this.lastUpdateMillis = lastUpdateMillis;
    }

    /**
     * 全世界模式：给该世界所有玩家分别发送各自翻译后的内容。
     */
    @Override
    public void spawnToAll() {
        if (!checkLocationReady()) {
            return;
        }
        this.ranking.refreshRankingData();
        Map<Long, Player> players = getLocation().getLevel().getPlayers();
        if (players.isEmpty()) {
            return;
        }
        for (Player player : players.values()) {
            spawnTo(player);
        }
    }

    /**
     * per-player 模式：给单个玩家发送其翻译后的内容。
     */
    @Override
    public void spawnTo(Player player) {
        if (!checkLocationReady()) {
            return;
        }
        String content = this.ranking.getDisplayContent(player);
        DebugDrawerPacket packet = new DebugDrawerPacket();
        packet.shapes.add(createDebugText(content));
        player.dataPacket(packet);
        getSpawnedPlayers().add(player.getName());
    }

    /**
     * 刷新排行榜数据，并重新发给所有已跟踪的玩家。
     */
    public void refreshAndRespawn() {
        this.ranking.refreshRankingData();
        this.remove();
        if (!checkLocationReady()) {
            return;
        }
        Map<Long, Player> players = getLocation().getLevel().getPlayers();
        for (Player player : players.values()) {
            spawnTo(player);
        }
    }

    /**
     * 刷新排行榜数据，并重新发给单个玩家。
     */
    public void refreshAndRespawnTo(Player player) {
        this.ranking.refreshRankingData();
        this.removeFrom(player);
        spawnTo(player);
    }
}
