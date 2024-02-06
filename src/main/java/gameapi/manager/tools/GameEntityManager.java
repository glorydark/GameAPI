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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameEntityManager {

    public static Set<TextEntity> entityList = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void closeAll() {
        entityList.forEach(textEntity -> {
            if (!textEntity.closed) {
                textEntity.kill();
                textEntity.close();
            }
        });
    }

    public static void spawnTextEntity(Position position, Ranking ranking) {
        ranking.refreshRankingData();
        TextEntity entity = new TextEntity(position.getChunk(), position, ranking.getDisplayContent(), Entity.getDefaultNBT(new Vector3(position.x, position.y, position.z)));
        entity.setImmobile(true);
        entity.spawnToAll();
        entityList.add(entity);
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
