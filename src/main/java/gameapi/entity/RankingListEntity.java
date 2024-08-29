package gameapi.entity;


import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.GameAPI;
import gameapi.ranking.Ranking;

public class RankingListEntity extends TextEntity {

    protected Ranking ranking;

    protected long lastUpdateMillis = 0L;

    public RankingListEntity(Ranking ranking, FullChunk chunk, CompoundTag nbt) {
        super(chunk, ranking.getDisplayContent(), nbt);
        this.ranking = ranking;
    }

    public boolean onUpdate(int currentTick) {
        if (currentTick % GameAPI.TEXT_ENTITY_UPDATE_TICK_INTERVAL != 0) {
            return super.onUpdate(currentTick);
        }
        if (this.getLevel().getPlayers().isEmpty()) {
            return super.onUpdate(currentTick);
        }
        if (System.currentTimeMillis() - this.lastUpdateMillis >= GameAPI.getInstance().getEntityRefreshIntervals()) {
            this.ranking.refreshRankingData();
            this.setNameTag(this.ranking.getDisplayContent());
            this.lastUpdateMillis = System.currentTimeMillis();
        }
        return super.onUpdate(currentTick);
    }
}