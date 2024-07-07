package gameapi.entity;


import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.ranking.Ranking;

public class RankingListEntity extends TextEntity {

    protected Ranking ranking;

    protected long lastUpdateMillis = 0L;

    public RankingListEntity(Ranking ranking, FullChunk chunk, Position position, CompoundTag nbt) {
        super(chunk, position, ranking.getDisplayContent(), nbt);
        this.ranking = ranking;
    }

    public boolean onUpdate(int currentTick) {
        if (this.health <= 0) {
            this.setHealth(this.getMaxHealth());
        }
        if (System.currentTimeMillis() - this.lastUpdateMillis >= 500) {
            this.ranking.refreshRankingData();
            this.lastUpdateMillis = System.currentTimeMillis();
        }
        return super.onUpdate(currentTick);
    }
}