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
import gameapi.manager.GameDebugManager;
import gameapi.ranking.Ranking;
import gameapi.ranking.RankingFormat;
import gameapi.ranking.RankingSortSequence;
import gameapi.ranking.simple.SimpleRanking;

import java.util.*;

public class GameEntityManager {

    public static Map<Ranking, Set<TextEntity>> rankingListMap = new LinkedHashMap<>();

    public static void onUpdate() {
        if (Server.getInstance().getOnlinePlayers().size() == 0) {
            return;
        }
        for (Map.Entry<Ranking, Set<TextEntity>> entry : new ArrayList<>(rankingListMap.entrySet())) {
            for (TextEntity textEntity : entry.getValue()) {
                if (!textEntity.isAlive() || textEntity.isClosed()) {
                    textEntity.despawnFromAll();
                    textEntity.close();
                    rankingListMap.get(entry.getKey()).remove(textEntity);
                    if (textEntity instanceof RankingListEntity) {
                        GameAPI.getGameDebugManager().info("Respawn ranking: " + entry.getKey().getTitle() + " at " + textEntity.getPosition().asVector3f());
                        GameEntityManager.spawnRankingListEntity(textEntity.getPosition(), entry.getKey());
                    } else {
                        GameEntityManager.spawnTextEntity(textEntity.getPosition(), entry.getKey());
                    }
                }
            }
        }
    }

    public static void closeAll() {
        for (Level level : Server.getInstance().getLevels().values()) {
            for (Entity entity : level.getEntities()) {
                if (entity instanceof TextEntity) {
                    entity.kill();
                }
            }
        }
        for (Map.Entry<Ranking, Set<TextEntity>> entry : rankingListMap.entrySet()) {
            for (TextEntity textEntity : entry.getValue()) {
                textEntity.despawnFromAll();
                textEntity.close();
            }
        }
    }

    public static void spawnTextEntity(Position position, Ranking ranking) {
        ranking.refreshRankingData();
        TextEntity entity = new TextEntity(position.getChunk(), position, ranking.getDisplayContent(), Entity.getDefaultNBT(new Vector3(position.x, position.y, position.z)));
        entity.setImmobile(true);
        entity.scheduleUpdate();
        entity.spawnToAll();
    }

    public static void spawnRankingListEntity(Position position, Ranking ranking) {
        ranking.refreshRankingData();
        RankingListEntity entity = new RankingListEntity(ranking, position.getChunk(), position, Entity.getDefaultNBT(new Vector3(position.x, position.y, position.z)));
        entity.setImmobile(true);
        entity.scheduleUpdate();
        entity.spawnToAll();
        rankingListMap.computeIfAbsent(ranking, ranking1 -> new HashSet<>()).add(entity);
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
        maps.add(add);
        config.set("list", maps);
        config.save();
        new SimpleRanking(player.getLocation(), Ranking.getRankingValueType(valueType), gameName, dataName, title, "No data", new RankingFormat(), rankingSortSequence).spawnEntity();
    }
}
