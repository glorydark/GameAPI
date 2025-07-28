package gameapi.manager.data;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import gameapi.entity.RankingListEntity;
import gameapi.entity.data.RankingEntityData;
import gameapi.manager.tools.GameEntityManager;
import gameapi.ranking.Ranking;
import gameapi.ranking.RankingFormat;
import gameapi.ranking.RankingSortSequence;
import gameapi.ranking.simple.RankingValueType;
import gameapi.ranking.simple.SimpleRanking;
import gameapi.tools.SpatialTools;
import gameapi.utils.AdvancedLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author glorydark
 */
public class RankingManager {

    protected static final Map<String, Ranking> rankingFactory = new LinkedHashMap<>();
    public static int rankingTextEntityRefreshIntervals;

    public static void init() {
        Config config = new Config(GameAPI.getPath() + "/rankings.yml");
        rankingTextEntityRefreshIntervals = config.getInt("ranking-text-entity-refresh-intervals", 100);
        List<Map<String, Object>> maps = config.get("list", new ArrayList<>());
        for (Map<String, Object> map : maps) {
            String level = (String) map.get("level");
            if (Server.getInstance().getLevelByName(level) == null) {
                if (!Server.getInstance().loadLevel(level)) {
                    GameAPI.getInstance().getLogger().warning(GameAPI.getLanguage().getTranslation("loading.ranking_loader.world.not_found", level));
                    continue;
                } else {
                    GameAPI.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("loading.ranking_loader.world.load.start", level));
                }
            } else {
                GameAPI.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("loading.ranking_loader.world.already_loaded", level));
            }
            String rankingIdentifier = map.getOrDefault("game_name", "") + "_" + map.getOrDefault("data_name", "");
            if (!rankingFactory.containsKey(rankingIdentifier)) {
                rankingFactory.put(
                        rankingIdentifier,
                        new SimpleRanking(Ranking.getRankingValueType((String) map.getOrDefault("value_type", "")),
                                (String) map.getOrDefault("game_name", ""),
                                (String) map.getOrDefault("data_name", ""),
                                (String) map.getOrDefault("title", "Undefined"),
                                "暂无数据",
                                new RankingFormat(),
                                Ranking.getRankingSortSequence((String) map.getOrDefault("sort_sequence", "descend")),
                                (Integer) map.getOrDefault("max_show_count", 15)
                        )
                );
            }
            Location location;
            AdvancedLocation advancedLocation = SpatialTools.parseLocation(map.getOrDefault("x", 0) + ":" + map.getOrDefault("y", 0) + ":" + map.getOrDefault("z", 0) + ":" + level);
            if (advancedLocation != null) {
                location = advancedLocation.getLocation();
                if (location.getChunk() == null) {
                    if (!location.getLevel().loadChunk(location.getChunkX(), location.getChunkZ())) {
                        GameAPI.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("loading.ranking_loader.chunk.load_start", location.getChunkX(), location.getChunkZ()));
                        return;
                    } else {
                        GameAPI.getInstance().getLogger().warning(GameAPI.getLanguage().getTranslation("loading.ranking_loader.chunk.load.failed", location.getChunkX(), location.getChunkZ()));
                    }
                } else {
                    GameAPI.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("loading.ranking_loader.chunk.already_loaded", location.getChunkX(), location.getChunkZ()));
                }
                spawnRankingListEntity(location, rankingFactory.get(rankingIdentifier));
            }
        }
    }

    public static boolean registerRanking(String identifier, String gameName, String dataName, String title, String noDataContent, RankingFormat rankingFormat, RankingValueType rankingValueType, RankingSortSequence rankingSortSequence) {
        if (rankingFactory.containsKey(identifier)) {
            return false;
        }
        rankingFactory.put(identifier, new SimpleRanking(rankingValueType, gameName, dataName, title, noDataContent, rankingFormat, rankingSortSequence, -1));
        return true;
    }

    public static Ranking getRankingData(String identifier) {
        return rankingFactory.get(identifier);
    }

    public static Ranking getInternalRegisteredRankingData(String gameName, String dataName) {
        return rankingFactory.get(gameName + "_" + dataName);
    }

    public static Map<String, Ranking> getRankingFactory() {
        return rankingFactory;
    }

    public static void despawnAllRankingEntities() {
        for (Level level : Server.getInstance().getLevels().values()) {
            for (Entity entity : level.getEntities()) {
                if (entity instanceof RankingListEntity) {
                    ((RankingListEntity) entity).setInvalid(true);
                    entity.close();
                }
            }
        }
        GameEntityManager.textEntityDataList.clear();
    }

    public static void spawnRankingListEntity(Location location, Ranking ranking) {
        FullChunk chunk = location.getChunk();
        if (chunk == null) {
            return;
        }
        if (!chunk.isLoaded() || chunk.getProvider() == null) {
            try {
                location.getLevel().loadChunk(location.getChunkX(), location.getChunkZ());
            } catch (Throwable e) {
                GameAPI.getGameDebugManager().printError(e);
                return;
            }
        }
        chunk = location.getLevel().getChunk(location.getChunkX(), location.getChunkZ());
        if (!chunk.isLoaded() || chunk.getProvider() == null) {
            GameAPI.getGameDebugManager().error("Failed to summon ranking entity at " + location.asVector3f());
            return;
        }
        ranking.refreshRankingData();
        RankingListEntity entity = new RankingListEntity(ranking, chunk, RankingListEntity.getDefaultNBT(new Vector3(location.x, location.y, location.z)));
        entity.setImmobile(true);
        entity.spawnToAll();
        GameEntityManager.textEntityDataList.add(new RankingEntityData(ranking, entity, location));
    }

    public static void addRankingList(Location location, String valueType, String gameName, String dataName, String title, RankingSortSequence rankingSortSequence) {
        Config config = new Config(GameAPI.getPath() + File.separator + "rankings.yml");
        List<Map<String, Object>> maps = (List<Map<String, Object>>) config.get("list");
        Map<String, Object> add = new LinkedHashMap<>();
        add.put("game_name", gameName);
        add.put("data_name", dataName);
        add.put("x", location.getX());
        add.put("y", location.getY());
        add.put("z", location.getZ());
        add.put("level", location.getLevel().getName());
        add.put("title", title);
        add.put("format", "[%rank%] %player%: %score%");
        add.put("sort_sequence", "descend");
        add.put("value_type", valueType);
        add.put("max_display_count", 15);
        maps.add(add);
        config.set("list", maps);
        config.save();
        String rankingIdentifier = gameName + "_" + dataName;
        if (!rankingFactory.containsKey(rankingIdentifier)) {
            rankingFactory.put(gameName + "_" + dataName, new SimpleRanking(Ranking.getRankingValueType(valueType),
                    gameName,
                    dataName,
                    title,
                    "No data",
                    new RankingFormat(),
                    rankingSortSequence,
                    15));
        }
        spawnRankingListEntity(location, rankingFactory.get(rankingIdentifier));
    }
}
