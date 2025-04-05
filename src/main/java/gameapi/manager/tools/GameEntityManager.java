package gameapi.manager.tools;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import gameapi.GameAPI;
import gameapi.entity.RankingListEntity;
import gameapi.entity.TextEntity;
import gameapi.entity.data.RankingEntityData;
import gameapi.entity.data.TextEntityData;
import gameapi.manager.data.RankingManager;
import gameapi.ranking.Ranking;

import java.util.ArrayList;
import java.util.List;

public class GameEntityManager {

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
                        textEntity.despawnFromAll();
                        textEntity.close();
                        textEntityDataList.remove(textEntityData);
                        continue;
                    } else if (!textEntity.isAlive() || textEntity.isClosed()) {
                        textEntity.respawn();
                    }
                }
                switch (textEntityData.getEntityType()) {
                    case TextEntityData.TYPE_NORMAL:
                        if (textEntity == null) {
                            textEntityDataList.remove(textEntityData);
                            GameEntityManager.spawnTextEntity(textEntityData.getLocation(), textEntityData.getDefaultText());
                            // GameAPI.getGameDebugManager().info("Respawn text entity: " + textEntityData.getDefaultText() + " at " + textEntityData.getPosition().asVector3f());
                        } else {
                            Level level = textEntity.getLevel();
                            if (level == null || level.getPlayers().isEmpty()) {
                                continue;
                            }
                            if (!textEntity.isAlive() || textEntity.isClosed()) {
                                textEntityDataList.remove(textEntityData);
                                textEntity.despawnFromAll();
                                textEntity.close();
                                GameEntityManager.spawnTextEntity(textEntity.getLocation(), textEntityData.getDefaultText());
                                // GameAPI.getGameDebugManager().info("Respawn text entity: " + textEntityData.getDefaultText() + " at " + textEntityData.getPosition().asVector3f());
                            } else if (System.currentTimeMillis() - textEntityData.getStartMillis() >= 300000L) {
                                textEntityDataList.remove(textEntityData);
                                textEntity.despawnFromAll();
                                textEntity.close();
                                GameEntityManager.spawnTextEntity(textEntity.getLocation(), textEntityData.getDefaultText());
                            } else {
                                textEntity.onAsyncUpdate(Server.getInstance().getTick());
                            }
                        }
                        break;
                    case TextEntityData.TYPE_RANKING:
                        Ranking ranking = ((RankingEntityData) textEntityData).getRanking();
                        if (textEntity == null) {
                            textEntityDataList.remove(textEntityData);
                            RankingManager.spawnRankingListEntity(textEntityData.getLocation(), ranking);
                            // GameAPI.getGameDebugManager().info("Respawn text entity: " + textEntityData.getDefaultText() + " at " + textEntityData.getPosition().asVector3f());
                        } else {
                            Level level = textEntity.getLevel();
                            if (level == null || level.getPlayers().isEmpty()) {
                                continue;
                            }
                            if (!textEntity.isAlive() || textEntity.isClosed()) {
                                textEntityDataList.remove(textEntityData);
                                textEntity.despawnFromAll();
                                textEntity.close();
                                RankingManager.spawnRankingListEntity(textEntityData.getLocation(), ranking);
                                // GameAPI.getGameDebugManager().info("Respawn ranking: " + ranking.getTitle() + " at " + textEntityData.getPosition().asVector3f());
                            } else if (System.currentTimeMillis() - textEntityData.getStartMillis() >= 300000L) {
                                textEntityDataList.remove(textEntityData);
                                textEntity.despawnFromAll();
                                textEntity.close();
                                RankingManager.spawnRankingListEntity(textEntityData.getLocation(), ranking);
                            } else {
                                textEntity.onAsyncUpdate(Server.getInstance().getTick());
                            }
                        }
                        break;
                }
            } catch (Throwable e) {
                GameAPI.getGameDebugManager().printError(e);
            }
        }
    }

    public static void closeAll() {
        for (Level level : Server.getInstance().getLevels().values()) {
            for (Entity entity : level.getEntities()) {
                if (entity instanceof TextEntity) {
                    entity.despawnFromAll();
                    entity.close();
                }
            }
        }
        RankingManager.getRankingFactory().clear();
    }

    public static void spawnTextEntity(Location location, String content) {
        FullChunk chunk = location.getChunk();
        if (chunk == null) {
            return;
        }
        if (!chunk.isLoaded() || chunk.getProvider() == null) {
            try {
                location.getLevel().loadChunk(location.getChunkX(), location.getChunkZ());
                chunk = location.getLevel().getChunk(location.getChunkX(), location.getChunkZ());
            } catch (Throwable e) {
                GameAPI.getGameDebugManager().printError(e);
                return;
            }
        }
        TextEntity entity = new TextEntity(chunk, content, RankingListEntity.getDefaultNBT(new Vector3(location.x, location.y, location.z)));
        entity.setImmobile(true);
        entity.spawnToAll();
        textEntityDataList.add(new TextEntityData(entity, location, content));
    }

}
