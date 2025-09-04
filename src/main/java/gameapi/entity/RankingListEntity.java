package gameapi.entity;


import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.manager.data.RankingManager;
import gameapi.ranking.Ranking;

public class RankingListEntity extends TextEntity {

    protected Ranking ranking;

    protected long lastUpdateMillis = 0L;

    public RankingListEntity(Ranking ranking, FullChunk chunk, CompoundTag nbt) {
        super(chunk, ranking.getDisplayContent(), nbt);
        this.ranking = ranking;
    }

    @Override
    public boolean onAsyncUpdate(int currentTick) {
        if (this.isClosed()) {
            return false;
        }
        if (this.getLevel().getPlayers().isEmpty()) {
            return false;
        }
        if (RankingManager.rankingTextEntityRefreshIntervals <= 0 || System.currentTimeMillis() - this.lastUpdateMillis >= RankingManager.rankingTextEntityRefreshIntervals) {
            this.ranking.refreshRankingData();
            this.setNameTag(this.ranking.getDisplayContent());
            this.lastUpdateMillis = System.currentTimeMillis();
        }
        return super.onAsyncUpdate(currentTick);
    }
}