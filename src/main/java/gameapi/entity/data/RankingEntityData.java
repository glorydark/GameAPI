package gameapi.entity.data;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import gameapi.ranking.Ranking;

/**
 * @author glorydark
 */
public class RankingEntityData extends TextEntityData {

    private Ranking ranking;

    public RankingEntityData(Ranking ranking, Entity entity, Position position) {
        super(entity, position, "");
        this.ranking = ranking;
    }

    public Ranking getRanking() {
        return ranking;
    }

    public void setRanking(Ranking ranking) {
        this.ranking = ranking;
    }

    @Override
    public String getEntityType() {
        return TYPE_RANKING;
    }
}
