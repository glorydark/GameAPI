package gameapi.manager.tools;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameEntityManager {

    public static Map<String, Ranking> rankingFactory = new LinkedHashMap<>();

    public static List<TextEntityData> textEntityDataList = new ArrayList<>();

    public static void onUpdate() {
        if (Server.getInstance().getOnlinePlayers().isEmpty()) {
            return;
        }
        for (TextEntityData textEntityData : new ArrayList<>(textEntityDataList)) {
            Entity textEntity = textEntityData.getEntity();
            switch (textEntityData.getEntityType()) {
                case TextEntityData.TYPE_NORMAL:
                    if (textEntity == null) {
                        textEntityDataList.remove(textEntityData);
                        GameEntityManager.spawnTextEntity(textEntityData.getPosition(), textEntityData.getDefaultText());
                        // GameAPI.getGameDebugManager().info("Respawn text entity: " + textEntityData.getDefaultText() + " at " + textEntityData.getPosition().asVector3f());
                    } else if (!textEntity.isAlive() || textEntity.isClosed()) {
                        textEntityDataList.remove(textEntityData);
                        GameEntityManager.spawnTextEntity(textEntity.getPosition(), textEntityData.getDefaultText());
                        textEntity.close();
                        // GameAPI.getGameDebugManager().info("Respawn text entity: " + textEntityData.getDefaultText() + " at " + textEntityData.getPosition().asVector3f());
                    } else if (System.currentTimeMillis() - textEntityData.getStartMillis() >= 300000L) {
                        textEntityDataList.remove(textEntityData);
                        GameEntityManager.spawnTextEntity(textEntity.getPosition(), textEntityData.getDefaultText());
                        textEntity.close();
                    }
                    break;
                case TextEntityData.TYPE_RANKING:
                    Ranking ranking = ((RankingEntityData) textEntityData).getRanking();
                    if (!textEntity.isAlive() || textEntity.isClosed()) {
                        textEntityDataList.remove(textEntityData);
                        textEntity.close();
                        spawnRankingListEntity(textEntity, ranking);
                        // GameAPI.getGameDebugManager().info("Respawn ranking: " + ranking.getTitle() + " at " + textEntityData.getPosition().asVector3f());
                    } else if (System.currentTimeMillis() - textEntityData.getStartMillis() >= 300000L) {
                        textEntityDataList.remove(textEntityData);
                        textEntity.close();
                        spawnRankingListEntity(textEntity, ranking);
                    }
                    break;
            }
        }
        textEntityDataList.remove(null);
    }

    public static void closeAll() {
        for (Level level : Server.getInstance().getLevels().values()) {
            for (Entity entity : level.getEntities()) {
                if (entity instanceof TextEntity) {
                    entity.kill();
                }
            }
        }
        for (TextEntityData data : textEntityDataList) {
            Entity textEntity = data.getEntity();
            textEntity.despawnFromAll();
            textEntity.close();
        }
    }

    public static void spawnTextEntity(Position position, String content) {
        if (position.isValid() && !position.getChunk().isLoaded()) {
            try {
                position.getChunk().load(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        TextEntity entity = new TextEntity(position.getChunk(), content, RankingListEntity.getDefaultNBT(new Vector3(position.x, position.y, position.z)));
        entity.setImmobile(true);
        entity.spawnToAll();
        entity.scheduleUpdate();
        textEntityDataList.add(new TextEntityData(entity, position, content));
    }

    public static void spawnRankingListEntity(Position position, Ranking ranking) {
        ranking.refreshRankingData();
        RankingListEntity entity = new RankingListEntity(ranking, position.getChunk(), RankingListEntity.getDefaultNBT(new Vector3(position.x, position.y, position.z)));
        entity.setImmobile(true);
        entity.spawnToAll();
        entity.scheduleUpdate();
        textEntityDataList.add(new RankingEntityData(ranking, entity, position));
    }

    public static void addRankingList(Location location, String valueType, String gameName, String dataName, String title, RankingSortSequence rankingSortSequence) {
        Config config = new Config(GameAPI.getPath() + "/rankings.yml");
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
