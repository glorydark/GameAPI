package gameapi.locker.entry;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.SpawnParticleEffectPacket;
import lombok.Getter;

/**
 * @author glorydark
 * @date {2024/1/4} {22:04}
 */
@Getter
public class LockerParticleEffectEntry extends LockerEntry {

    private final String identifier;

    LockerParticleEffectEntry(String name, String identifier) {
        super(name);
        this.identifier = identifier;
    }

    public void use(Player player) {
        SpawnParticleEffectPacket pk = new SpawnParticleEffectPacket(); // 此数据包用于调用客户端的颗粒效果
        pk.position = player.getPosition().asVector3f(); // 生成颗粒效果的位置
        pk.identifier = identifier; // 颗粒效果定义符, 必须和材质包内设定的一样, 否则不会显示
        pk.dimensionId = 0; // 维度ID, 填玩家所在世界维度的即可, 默认为 0 (0: 主世界, 1: 地狱, 2: 末地)
        pk.uniqueEntityId = -1; // 某实体的UUID, 目前无需理会, 默认为 -1
        Server.broadcastPacket(player.getLevel().getPlayers().values(), pk);
    }
}
