package gameapi.entity;


import cn.nukkit.event.entity.EntityDespawnEvent;
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
        if (this.health <= 0) {
            this.health += this.getMaxHealth();
        }
        if (System.currentTimeMillis() - lastUpdateMillis >= 500) {
            ranking.refreshRankingData();
            this.setNameTag(ranking.getDisplayContent());
            lastUpdateMillis = System.currentTimeMillis();
        }
        return super.onUpdate(currentTick);
    }


    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.server.getPluginManager().callEvent(new EntityDespawnEvent(this));
            this.despawnFromAll();
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }

            if (this.level != null) {
                this.level.removeEntity(this);
            }
        }
    }
}