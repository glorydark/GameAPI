package gameapi.manager.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
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

import java.util.*;

public class GameEntityManager {

    public static List<TextEntityData> rankingList = new ArrayList<>();

    public static void onUpdate() {
        if (Server.getInstance().getOnlinePlayers().isEmpty()) {
            return;
        }
        for (TextEntityData textEntityData : new ArrayList<>(rankingList)) {
            Entity textEntity = textEntityData.getEntity();
            switch (textEntityData.getEntityType()) {
                case TextEntityData.TYPE_NORMAL:
                    if (textEntity == null) {
                        GameEntityManager.spawnTextEntity(textEntityData.getPosition(), textEntityData.getDefaultText());
                        // GameAPI.getGameDebugManager().info("Respawn text entity: " + textEntityData.getDefaultText() + " at " + textEntityData.getPosition().asVector3f());
                    } else if (!textEntity.isAlive() || textEntity.isClosed()) {
                        rankingList.remove(textEntityData);
                        textEntity.despawnFromAll();
                        textEntity.close();
                        GameEntityManager.spawnTextEntity(textEntity.getPosition(), textEntityData.getDefaultText());
                        // GameAPI.getGameDebugManager().info("Respawn text entity: " + textEntityData.getDefaultText() + " at " + textEntityData.getPosition().asVector3f());
                    }
                    break;
                case TextEntityData.TYPE_RANKING:
                    Ranking ranking = ((RankingEntityData) textEntityData).getRanking();
                    if (textEntity == null) {
                        GameEntityManager.spawnRankingListEntity(textEntityData.getPosition(), ranking);
                        // GameAPI.getGameDebugManager().info("Respawn ranking: " + ranking.getTitle() + " at " + textEntityData.getPosition().asVector3f());
                    } else if (!textEntity.isAlive() || textEntity.isClosed()) {
                        rankingList.remove(textEntityData);
                        textEntity.despawnFromAll();
                        textEntity.close();
                        GameEntityManager.spawnRankingListEntity(textEntity.getPosition(), ranking);
                        // GameAPI.getGameDebugManager().info("Respawn ranking: " + ranking.getTitle() + " at " + textEntityData.getPosition().asVector3f());
                    }
                    break;
            }
        }
        rankingList.remove(null);
    }

    public static void closeAll() {
        for (Level level : Server.getInstance().getLevels().values()) {
            for (Entity entity : level.getEntities()) {
                if (entity instanceof TextEntity) {
                    entity.kill();
                }
            }
        }
        for (TextEntityData data : rankingList) {
            Entity textEntity = data.getEntity();
            textEntity.despawnFromAll();
            textEntity.close();
        }
    }

    public static void spawnTextEntity(Position position, String content) {
        TextEntity entity = new TextEntity(position.getChunk(), position, content, Entity.getDefaultNBT(new Vector3(position.x, position.y, position.z)));
        entity.setImmobile(true);
        entity.scheduleUpdate();
        entity.spawnToAll();
        rankingList.add(new TextEntityData(entity, position, content));
    }

    public static void spawnRankingListEntity(Position position, Ranking ranking) {
        ranking.refreshRankingData();
        RankingListEntity entity = new RankingListEntity(ranking, position.getChunk(), position, Entity.getDefaultNBT(new Vector3(position.x, position.y, position.z)));
        entity.setImmobile(true);
        entity.scheduleUpdate();
        entity.spawnToAll();
        rankingList.add(new RankingEntityData(ranking, entity, position));
    }

    public static void addRankingList(Player player, String valueType, String gameName, String dataName, String title, RankingSortSequence rankingSortSequence) {
        Config config = new Config(GameAPI.getPath() + "/rankings.yml");
        List<Map<String, Object>> maps = (List<Map<String, Object>>) config.get("list");
        Map<String, Object> add = new LinkedHashMap<>();
        add.put("game_name", gameName);
        add.put("data_name", dataName);
        add.put("x", player.getX());
        add.put("y", player.getY());
        add.put("z", player.getZ());
        add.put("level", player.getLevel().getName());
        add.put("title", title);
        add.put("format", "[%rank%] %player%: %score%");
        add.put("sort_sequence", "descend");
        add.put("value_type", valueType);
        add.put("max_display_count", 15);
        maps.add(add);
        config.set("list", maps);
        config.save();
        new SimpleRanking(player.getLocation(), Ranking.getRankingValueType(valueType), gameName, dataName, title, "No data", new RankingFormat(), rankingSortSequence, 15).spawnEntity();
    }
}
