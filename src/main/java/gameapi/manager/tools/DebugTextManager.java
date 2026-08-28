package gameapi.manager.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import gameapi.GameAPI;
import gameapi.entity.data.DebugTextData;
import gameapi.entity.data.DebugTextSettings;
import gameapi.entity.data.RankingDebugTextData;
import gameapi.manager.data.RankingManager;
import gameapi.ranking.Ranking;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DebugText 浮动文字管理器。
 * <p>
 * 通过 {@link cn.nukkit.network.protocol.DebugDrawerPacket} 发包实现，不创建实体。
 * 仅 MOT 且客户端协议 >= 1.21.90 可用。
 *
 * @author glorydark
 */
public class DebugTextManager {

    private static final Map<Long, DebugTextData> debugTextDataMap = new ConcurrentHashMap<>();

    public static boolean update = true;

    public static Map<Long, DebugTextData> getDebugTextDataMap() {
        return debugTextDataMap;
    }

    public static long spawnDebugText(Location location, String text, DebugTextSettings settings) {
        DebugTextData data = new DebugTextData(location, text, settings);
        data.spawnToAll();
        debugTextDataMap.put(data.getShapeId(), data);
        return data.getShapeId();
    }

    public static long spawnRankingDebugText(Location location, Ranking ranking, DebugTextSettings settings) {
        RankingDebugTextData data = new RankingDebugTextData(location, ranking, settings);
        data.spawnToAll();
        debugTextDataMap.put(data.getShapeId(), data);
        return data.getShapeId();
    }

    public static long spawnDebugTextTo(Location location, String text, DebugTextSettings settings, Player... players) {
        DebugTextData data = new DebugTextData(location, text, settings);
        for (Player player : players) {
            data.spawnTo(player);
        }
        debugTextDataMap.put(data.getShapeId(), data);
        return data.getShapeId();
    }

    public static long spawnRankingDebugTextTo(Location location, Ranking ranking, DebugTextSettings settings, Player... players) {
        RankingDebugTextData data = new RankingDebugTextData(location, ranking, settings);
        for (Player player : players) {
            data.spawnTo(player);
        }
        debugTextDataMap.put(data.getShapeId(), data);
        return data.getShapeId();
    }

    public static void updateDebugText(long shapeId) {
        DebugTextData data = debugTextDataMap.get(shapeId);
        if (data == null) {
            return;
        }
        data.remove();
        data.spawnToAll();
    }

    public static void updateRankingDebugText(long shapeId) {
        DebugTextData data = debugTextDataMap.get(shapeId);
        if (data instanceof RankingDebugTextData rankingData) {
            rankingData.refreshAndRespawn();
        }
    }

    public static boolean removeDebugText(long shapeId) {
        DebugTextData data = debugTextDataMap.remove(shapeId);
        if (data == null) {
            return false;
        }
        data.remove();
        return true;
    }

    public static void removeDebugTextFrom(long shapeId, Player player) {
        DebugTextData data = debugTextDataMap.get(shapeId);
        if (data != null) {
            data.removeFrom(player);
        }
    }

    public static void showDebugTextTo(long shapeId, Player player) {
        DebugTextData data = debugTextDataMap.get(shapeId);
        if (data != null) {
            data.spawnTo(player);
        }
    }

    public static void hideDebugTextFrom(long shapeId, Player player) {
        DebugTextData data = debugTextDataMap.get(shapeId);
        if (data != null) {
            data.removeFrom(player);
        }
    }

    public static void respawnAllTo(Player player) {
        Level playerLevel = player.getLevel();
        if (playerLevel == null) {
            return;
        }
        for (DebugTextData data : debugTextDataMap.values()) {
            if (!data.getLocation().getLevelName().equals(playerLevel.getName())) {
                continue;
            }
            // per-player 模式：只补发给被跟踪的玩家
            if (!data.getSpawnedPlayers().isEmpty() && !data.getSpawnedPlayers().contains(player.getName())) {
                continue;
            }
            data.spawnTo(player);
        }
    }

    public static void onPlayerQuit(Player player) {
        // 清理缓存逻辑，当前实现为无操作
    }

    public static void onUpdate() {
        if (!update) {
            return;
        }
        if (Server.getInstance().getOnlinePlayers().isEmpty()) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<Long, DebugTextData>> iterator = debugTextDataMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, DebugTextData> entry = iterator.next();
            DebugTextData data = entry.getValue();
            Level level = data.getLocation().getLevel();
            if (level == null || level.getPlayers().isEmpty()) {
                continue;
            }

            // 过期清理（与 GameEntityManager 一致，5 分钟）
            if (data.getStartMillis() > 0 && currentTime - data.getStartMillis() >= 300000L) {
                data.remove();
                iterator.remove();
                continue;
            }

            // 排行榜定时刷新
            if (data instanceof RankingDebugTextData rankingData) {
                int refreshInterval = RankingManager.rankingTextEntityRefreshIntervals;
                if (refreshInterval <= 0 || currentTime - rankingData.getLastUpdateMillis() >= refreshInterval) {
                    rankingData.refreshAndRespawn();
                    rankingData.setLastUpdateMillis(currentTime);
                }
            }
        }
    }

    public static void closeAll() {
        update = false;
        for (DebugTextData data : debugTextDataMap.values()) {
            data.remove();
        }
        debugTextDataMap.clear();
    }
}
