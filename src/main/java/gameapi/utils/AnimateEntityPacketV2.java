package gameapi.utils;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author IWareQ
 */

public class AnimateEntityPacketV2 extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ANIMATE_ENTITY_PACKET;

    private String animation = "";
    private String nextState = "";
    private String stopExpression = "";

    private int stopExpressionVersion = 0;
    private String controller = "";
    private float blendOutTime = 5.0f;
    private List<Long> animatedEntityRuntimeIds = new ArrayList<>();

    public AnimateEntityPacketV2(){

    }

    @Override
    public void decode() {
        // No-op
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.animation);
        this.putString(this.nextState);
        this.putString(this.stopExpression);
        this.putInt(this.stopExpressionVersion);
        this.putString(this.controller);
        this.putLFloat(this.blendOutTime);
        this.putUnsignedVarInt(this.animatedEntityRuntimeIds.size());
        for (long entityRuntimeId : this.animatedEntityRuntimeIds){
            this.putEntityRuntimeId(entityRuntimeId);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }


    public String getAnimation() {
        return this.animation;
    }


    public void setAnimation(String animation) {
        this.animation = animation;
    }


    public String getNextState() {
        return this.nextState;
    }


    public void setNextState(String nextState) {
        this.nextState = nextState;
    }


    public String getStopExpression() {
        return this.stopExpression;
    }


    public void setStopExpression(String stopExpression) {
        this.stopExpression = stopExpression;
    }


    public String getController() {
        return this.controller;
    }


    public void setController(String controller) {
        this.controller = controller;
    }


    public float getBlendOutTime() {
        return this.blendOutTime;
    }


    public void setBlendOutTime(float blendOutTime) {
        this.blendOutTime = blendOutTime;
    }


    public List<Long> getAnimatedEntityRuntimeIds() {
        return this.animatedEntityRuntimeIds;
    }


    public void setAnimatedEntityRuntimeIds(List<Long> animatedEntityRuntimeIds) {
        this.animatedEntityRuntimeIds = animatedEntityRuntimeIds;
    }

    public void addAnimatedEntityRuntimeIds(Long animatedEntityRuntimeId) {
        this.animatedEntityRuntimeIds.add(animatedEntityRuntimeId);
    }


    public int getStopExpressionVersion() {
        return stopExpressionVersion;
    }


    public void setStopExpressionVersion(int stopExpressionVersion) {
        this.stopExpressionVersion = stopExpressionVersion;
    }
}