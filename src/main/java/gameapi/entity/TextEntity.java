package gameapi.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import gameapi.GameAPI;
import gameapi.annotation.Description;
import glorydark.nukkit.languageapi.api.LanguageAPI;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TextEntity extends Entity {

    private final Map<String, Object> extraProperties = new LinkedHashMap<>();

    private int maxShowDistance = -1;

    public TranslationContainer rawText;

    @Description(usage = "used for LanguageAPI")
    private String translationCategory = "GameAPI";

    public TextEntity(FullChunk chunk, String text, CompoundTag nbt) {
        super(chunk, nbt);
        this.rawText = new TranslationContainer(text);
    }

    protected void initEntity() {
        super.initEntity();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setImmobile(true);
        this.getDataProperties().putLong(0, 65536L);
        this.setScale(0f);
        this.setCanBeSavedWithChunk(false);
    }

    public int getNetworkId() {
        return EntityArmorStand.NETWORK_ID;
    }

    public boolean onAsyncUpdate(int currentTick) {
        if (this.isClosed()) {
            return false;
        }
        /*
        if (Arrays.stream(this.level.getEntities()).noneMatch(entity -> entity == this)) {
            this.getLevel().addEntity(this);
        }
         */
        for (Player player : new ArrayList<>(this.getLevel().getPlayers().values())) {
            if (this.getViewers().containsKey(player.getLoaderId())) {
                if (!player.isOnline() || player.getLevel() != this.getLevel() || (this.maxShowDistance != -1 && player.distance(this) > this.maxShowDistance)) {
                    this.despawnFrom(player);
                    RemoveEntityPacket pk = new RemoveEntityPacket();
                    pk.eid = this.id;
                    player.dataPacket(pk);
                }
            } else {
                if (player.getLevel() == this.getLevel() && (this.maxShowDistance == -1 || player.distance(this) <= this.maxShowDistance)) {
                    this.spawnTo(player);
                }
            }
        }
        return true;
    }

    public boolean respawn() {
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        return true;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public void saveNBT() {

    }

    @Override
    public boolean canBeSavedWithChunk() {
        return false;
    }

    public Map<String, Object> getExtraProperties() {
        return extraProperties;
    }

    public void setMaxShowDistance(int maxShowDistance) {
        this.maxShowDistance = maxShowDistance;
    }

    public int getMaxShowDistance() {
        return maxShowDistance;
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
        if (GameAPI.getInstance().isLanguageAPIEnabled()) {
            if (this.rawText.getParameters() == null) {
                this.rawText.setParameters(new String[0]);
            }
            String[] params = this.rawText.getParameters().clone();
            for (int i = 0; i < params.length; i++) {
                params[i] = LanguageAPI.translate(
                        this.translationCategory,
                        player,
                        params[i]);
            }
            pk.metadata.putString(
                    DATA_NAMETAG,
                    LanguageAPI.translate(this.translationCategory, player, this.rawText.getText(),
                            (Object[]) params)
            );
        } else {
            pk.metadata.putString(
                    DATA_NAMETAG,
                    this.getNameTag()
            );
        }
        return pk;
    }

    public String getTranslationCategory() {
        return translationCategory;
    }

    public void setTranslationCategory(String translationCategory) {
        this.translationCategory = translationCategory;
    }
}