package gameapi.entity;


import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.GameAPI;
import gameapi.ranking.Ranking;

public class RankingListEntity extends TextEntity {

    protected Ranking ranking;

    protected long lastUpdateMillis = 0L;

    public RankingListEntity(Ranking ranking, FullChunk chunk, Position position, CompoundTag nbt) {
        super(chunk, position, ranking.getDisplayContent(), nbt);
        this.ranking = ranking;
    }

    public boolean onUpdate(int currentTick) {
        if (this.getLevel().getPlayers().size() == 0) {
            return super.onUpdate(currentTick);
        }
        if (System.currentTimeMillis() - this.lastUpdateMillis >= GameAPI.getInstance().getEntityRefreshIntervals()) {
            this.ranking.refreshRankingData();
            this.setNameTag(this.ranking.getDisplayContent());
            this.lastUpdateMillis = System.currentTimeMillis();
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }
}