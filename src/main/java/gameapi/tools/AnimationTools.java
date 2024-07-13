package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import gameapi.utils.protocol.AnimateEntityPacketV2;

import java.util.Collections;

/**
 * @author iGxnon
 * @date 2021/8/30
 */
public class AnimationTools {

    /**
     * 全服广播动画包
     *
     * @param packet 动画包
     */
    public static void senPacket(AnimateEntityPacketV2 packet) {
        Server.getInstance().getOnlinePlayers().values().forEach(player -> senPacket(packet, player));
    }

    /**
     * 向单个个体发放动画包
     *
     * @param packet 动画包
     * @param target 个体
     */
    public static void senPacket(AnimateEntityPacketV2 packet, Player target) {
        target.dataPacket(packet);
    }

    public static AnimationPacketBuilder builder() {
        return new AnimationPacketBuilder();
    }

    @SuppressWarnings("unused")
    public static class AnimationPacketBuilder {

        private String animation;
        private String nextState;
        private String stopExpression;
        private String controller;
        private float blendOutTime;
        private long entityRuntimeId;

        /**
         * 必要的
         *
         * @param animation 动画identifier 储存在cc.igxnon.squarelottery.animations.Info中
         * @return Builder
         */
        public AnimationPacketBuilder animation(String animation) {
            this.animation = animation;
            return this;
        }

        public AnimationPacketBuilder nextState(String nextState) {
            this.nextState = nextState;
            return this;
        }

        public AnimationPacketBuilder stopExpression(String stopExpression) {
            this.stopExpression = stopExpression;
            return this;
        }

        public AnimationPacketBuilder controller(String controller) {
            this.controller = controller;
            return this;
        }

        /**
         * 消除时间
         *
         * @param blendOutTime [填0为直接停止]
         * @return Builder
         */
        public AnimationPacketBuilder blendOutTime(float blendOutTime) {
            this.blendOutTime = blendOutTime;
            return this;
        }

        /**
         * 必要的
         *
         * @param entityRuntimeId 目标实体runtimeId
         * @return Builder
         */
        public AnimationPacketBuilder entityRuntimeId(long entityRuntimeId) {
            this.entityRuntimeId = entityRuntimeId;
            return this;
        }

        /**
         * @return 获取的动画包对象
         */
        public AnimateEntityPacketV2 build() {
            AnimateEntityPacketV2 packet = new AnimateEntityPacketV2();
            packet.setAnimation(animation);
            packet.setController(controller);
            packet.setBlendOutTime(blendOutTime);
            packet.setNextState(nextState);
            packet.setStopExpression(stopExpression);
            packet.setAnimatedEntityRuntimeIds(Collections.singletonList(entityRuntimeId));
            return packet;
        }

        /**
         * 必须在必要参数全补全了再发包
         *
         * @param player 个体
         */
        public void deliverTo(Player player) {
            AnimationTools.senPacket(build(), player);
        }

        /**
         * 必须在必要参数全补全了再发包
         */
        public void deliverToAll() {
            AnimationTools.senPacket(build());
        }
    }
}