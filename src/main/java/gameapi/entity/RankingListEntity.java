package gameapi.entity;


import cn.nukkit.event.entity.EntityDespawnEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.GameAPI;
import gameapi.utils.GameRecord;

public class RankingListEntity extends TextEntity
{

    private String gameName;

    private String comparedKey;

    private GameRecord.SortSequence sortSequence;

    public RankingListEntity(FullChunk chunk, Position position, String text, GameRecord.SortSequence sortSequence, CompoundTag nbt) {
        super(chunk, position, text, nbt);
    }

    public RankingListEntity(FullChunk chunk, Position position, String gameName, String comparedKey, GameRecord.SortSequence sortSequence, CompoundTag nbt) {
        super(chunk, position, GameRecord.getGameRecordRankingListString(gameName, comparedKey, sortSequence), nbt);
        this.gameName = gameName;
        this.comparedKey = comparedKey;
    }

    public boolean onUpdate(int currentTick)
    {
        if(this.health <= 0){
            this.health+=this.getMaxHealth();
        }
        if(currentTick % GameAPI.entityRefreshIntervals == 0){
            this.setNameTag(GameRecord.getGameRecordRankingListString(gameName, comparedKey, sortSequence));
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