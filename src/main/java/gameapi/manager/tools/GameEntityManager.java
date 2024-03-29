package gameapi.manager.tools;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import gameapi.entity.TextEntity;
import gameapi.ranking.Ranking;
import gameapi.ranking.RankingFormat;
import gameapi.ranking.RankingSortSequence;
import gameapi.tools.EntityTools;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameEntityManager {

    public static LinkedHashMap<Ranking, Set<TextEntity>> entityList = new LinkedHashMap<>();

    public static void onUpdate() {
        for (Map.Entry<Ranking, Set<TextEntity>> entry : new ArrayList<>(entityList.entrySet())) {
            for (TextEntity textEntity : entry.getValue()) {
                if (textEntity.isClosed() || !textEntity.isAlive()) {
                    entityList.get(entry.getKey()).remove(textEntity);
                    GameEntityManager.spawnTextEntity(textEntity.getPosition(), entry.getKey());
                }
            }
        }
    }

    public static void closeAll() {
        for (Map.Entry<Ranking, Set<TextEntity>> entry : entityList.entrySet()) {
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
        entity.spawnToAll();
        if (!entityList.containsKey(ranking)) {
            entityList.put(ranking, new HashSet<>());
        }
        entityList.get(ranking).add(entity);
    }

    public static void addRankingList(Player player, String gameName, String comparedType, RankingSortSequence rankingSortSequence) {
        Config config = new Config(GameAPI.path + "/rankings.yml");
        List<Map<String, Object>> maps = (List<Map<String, Object>>) config.get("list");
        Map<String, Object> add = new LinkedHashMap<>();
        add.put("game_name", gameName);
        add.put("compared_type", comparedType);
        add.put("x", player.getX());
        add.put("y", player.getY());
        add.put("z", player.getZ());
        add.put("level", player.getLevel().getName());
        add.put("title", "testTitle");
        add.put("format", "[%rank%] %player%: %score%");
        add.put("sort_sequence", "descend");
        maps.add(add);
        config.set("list", maps);
        config.save();
        new Ranking(player.getLocation(), comparedType, gameName, "No data", new RankingFormat(), rankingSortSequence).spawnEntity();
    }
}
