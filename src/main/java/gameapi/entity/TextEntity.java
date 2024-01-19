package gameapi.entity;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.RemoveEntityPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TextEntity extends Entity {

    private final HashMap<Integer, Player> hasSpawned = new HashMap<>();

    public TextEntity(FullChunk chunk, Position position, String text, CompoundTag nbt) {
        super(chunk, nbt);
        this.setPosition(position);
        setNameTag(text);
    }

    @Deprecated
    public TextEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        close();
    }

    public int getNetworkId() {
        return 64;
    }

    protected void initEntity() {
        super.initEntity();
        setNameTagAlwaysVisible(true);
        setImmobile(true);
        getDataProperties().putLong(0, 65536L);
    }

    public boolean onUpdate(int currentTick) {
        for (Map.Entry<Integer, Player> integerPlayerEntry : new ArrayList<>(hasSpawned.entrySet())) {
            if (integerPlayerEntry.getValue() == null || !integerPlayerEntry.getValue().isOnline()) {
                hasSpawned.remove(integerPlayerEntry.getKey());
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public void spawnToAll() {
        for (Player player : this.getLevel().getPlayers().values()) {
            this.spawnTo(player);
        }
    }

    @Override
    public void despawnFromAll() {
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            this.despawnFrom(player);
        }
    }

    @Override
    public void spawnTo(Player player) {
        if (this.getNetworkId() == -1) {
            super.spawnTo(player);
            this.sendData(player);
        }
        if (this.getLevel().equals(player.getLevel())) {
            if (!this.hasSpawned.containsKey(player.getLoaderId()) && this.chunk != null && player.usedChunks.containsKey(Level.chunkHash(this.chunk.getX(), this.chunk.getZ()))) {
                this.hasSpawned.put(player.getLoaderId(), player);
                player.dataPacket(this.createAddEntityPacket());
                this.sendData(player);
            }
            this.hasSpawned.put(player.getLoaderId(), player);
            GameEntityCreator.entityList.add(this);
        }
    }

    @Override
    public void despawnFrom(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player.getLoaderId());
        }
        GameEntityCreator.entityList.remove(this);
    }

    @Override
    public void kill() {

    }
}