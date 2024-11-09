package gameapi.manager.tools;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import gameapi.entity.RankingListEntity;
import gameapi.entity.TextEntity;
import gameapi.entity.data.RankingEntityData;
import gameapi.entity.data.TextEntityData;
import gameapi.ranking.Ranking;
import gameapi.ranking.RankingFormat;
import gameapi.ranking.RankingSortSequence;
import gameapi.ranking.simple.SimpleRanking;

import java.io.File;
import java.util.*;

public class GameEntityManager {

    public static Map<String, Ranking> rankingFactory = new LinkedHashMap<>();

    public static List<TextEntityData> textEntityDataList = new ArrayList<>();

    public static void onUpdate() {
        if (Server.getInstance().getOnlinePlayers().isEmpty()) {
            return;
        }
        for (TextEntityData textEntityData : new ArrayList<>(textEntityDataList)) {
            try {
                TextEntity textEntity = textEntityData.getEntity();
                if (textEntity != null) {
                    if (textEntity.getLevel().getPlayers().isEmpty()) {
                        continue;
                    }
                }
                switch (textEntityData.getEntityType()) {
                    case TextEntityData.TYPE_NORMAL:
                        if (textEntity == null) {
                            textEntityDataList.remove(textEntityData);
                            GameEntityManager.spawnTextEntity(textEntityData.getLocation(), textEntityData.getDefaultText());
                            // GameAPI.getGameDebugManager().info("Respawn text entity: " + textEntityData.getDefaultText() + " at " + textEntityData.getPosition().asVector3f());
                        } else if (!textEntity.isAlive() || textEntity.isClosed()
                                || !textEntity.getChunk().getEntities().containsValue(textEntity)) {
                            textEntityDataList.remove(textEntityData);
                            textEntity.close();
                            GameEntityManager.spawnTextEntity(textEntity.getLocation(), textEntityData.getDefaultText());
                            // GameAPI.getGameDebugManager().info("Respawn text entity: " + textEntityData.getDefaultText() + " at " + textEntityData.getPosition().asVector3f());
                        } else if (System.currentTimeMillis() - textEntityData.getStartMillis() >= 300000L) {
                            textEntityDataList.remove(textEntityData);
                            textEntity.close();
                            GameEntityManager.spawnTextEntity(textEntity.getLocation(), textEntityData.getDefaultText());
                        } else {
                            textEntity.onAsyncUpdate(Server.getInstance().getTick());
                        }
                        break;
                    case TextEntityData.TYPE_RANKING:
                        Ranking ranking = ((RankingEntityData) textEntityData).getRanking();
                        if (textEntity == null) {
                            textEntityDataList.remove(textEntityData);
                            GameEntityManager.spawnRankingListEntity(textEntityData.getLocation(), ranking);
                            // GameAPI.getGameDebugManager().info("Respawn text entity: " + textEntityData.getDefaultText() + " at " + textEntityData.getPosition().asVector3f());
                        } else if (!textEntity.isAlive() || textEntity.isClosed()
                                || !textEntity.getChunk().getEntities().containsValue(textEntity)) {
                            textEntityDataList.remove(textEntityData);
                            textEntity.close();
                            GameEntityManager.spawnRankingListEntity(textEntityData.getLocation(), ranking);
                            // GameAPI.getGameDebugManager().info("Respawn ranking: " + ranking.getTitle() + " at " + textEntityData.getPosition().asVector3f());
                        } else if (System.currentTimeMillis() - textEntityData.getStartMillis() >= 300000L) {
                            textEntityDataList.remove(textEntityData);
                            textEntity.close();
                            GameEntityManager.spawnRankingListEntity(textEntityData.getLocation(), ranking);
                        } else {
                            textEntity.onAsyncUpdate(Server.getInstance().getTick());
                        }
                        break;
                }
            } catch (Throwable e) {
                e.printStackTrace();
                GameAPI.getGameDebugManager().error(e.getCause().getMessage() + "\n"
                        + e + ":\n"
                        + Arrays.toString(e.getStackTrace()).replace("[", "\n").replace("]", "\n").replace(", ", "\n")
                );
            }
        }
    }

    public static void closeAll() {
        for (Level level : Server.getInstance().getLevels().values()) {
            for (Entity entity : level.getEntities()) {
                if (entity instanceof TextEntity) {
                    entity.close();
                }
            }
        }
        List<TextEntityData> cacheList = new ArrayList<>(textEntityDataList);
        textEntityDataList.clear();
        for (TextEntityData data : cacheList) {
            Entity textEntity = data.getEntity();
            textEntity.despawnFromAll();
            textEntity.close();
        }
        rankingFactory.clear();
    }

    public static void spawnTextEntity(Location location, String content) {
        FullChunk chunk = location.getChunk();
        if (!chunk.isLoaded()) {
            try {
                location.getLevel().loadChunk(location.getChunkX(), location.getChunkZ());
                chunk = location.getLevel().getChunk(location.getChunkX(), location.getChunkZ());
            } catch (Throwable e) {
                e.printStackTrace();
                GameAPI.getGameDebugManager().error(e.getCause().getMessage() + "\n"
                        + e + ":\n"
                        + Arrays.toString(e.getStackTrace()).replace("[", "\n").replace("]", "\n").replace(", ", "\n")
                );
                return;
            }
        }
        TextEntity entity = new TextEntity(chunk, content, RankingListEntity.getDefaultNBT(new Vector3(location.x, location.y, location.z)));
        entity.setImmobile(true);
        entity.spawnToAll();
        textEntityDataList.add(new TextEntityData(entity, location, content));
    }

    public static void spawnRankingListEntity(Location location, Ranking ranking) {
        FullChunk chunk = location.getChunk();
        if (chunk == null) {
            return;
        }
        if (!chunk.isLoaded() || chunk.getProvider() == null) {
            try {
                location.getLevel().loadChunk(location.getChunkX(), location.getChunkZ());
                chunk = location.getLevel().getChunk(location.getChunkX(), location.getChunkZ());
            } catch (Throwable e) {
                e.printStackTrace();
                GameAPI.getGameDebugManager().error(e.getCause().getMessage() + "\n"
                        + e + ":\n"
                        + Arrays.toString(e.getStackTrace()).replace("[", "\n").replace("]", "\n").replace(", ", "\n")
                );
                return;
            }
        }
        ranking.refreshRankingData();
        RankingListEntity entity = new RankingListEntity(ranking, chunk, RankingListEntity.getDefaultNBT(new Vector3(location.x, location.y, location.z)));
        entity.setImmobile(true);
        entity.spawnToAll();
        textEntityDataList.add(new RankingEntityData(ranking, entity, location));
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
