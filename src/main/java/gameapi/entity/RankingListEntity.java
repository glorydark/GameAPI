package gameapi.entity;


import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import gameapi.manager.data.RankingManager;
import gameapi.ranking.Ranking;
import glorydark.nukkit.languageapi.api.LanguageAPI;

public class RankingListEntity extends TextEntity {

    protected Ranking ranking;

    protected long lastUpdateMillis = 0L;

    public RankingListEntity(Ranking ranking, FullChunk chunk, CompoundTag nbt) {
        super(chunk, "", nbt);
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
            this.lastUpdateMillis = System.currentTimeMillis();
        }
        return super.onAsyncUpdate(currentTick);
    }

    @Override
    public void spawnTo(Player player) {
        if (!this.hasSpawned.containsKey(player.getLoaderId()) && player.usedChunks.containsKey(Level.chunkHash(this.chunk.getX(), this.chunk.getZ()))) {
            player.dataPacket(this.createAddEntityPacket(player));
            this.hasSpawned.put(player.getLoaderId(), player);
        }
    }

    public DataPacket createAddEntityPacket(Player player) {
        AddEntityPacket pk = (AddEntityPacket) this.createAddEntityPacket();
        pk.metadata.putString(
                DATA_NAMETAG,
                LanguageAPI.translate("GameAPI", player, this.ranking.getDisplayContent(player))
        );
        return pk;
    }
}