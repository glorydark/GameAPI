package gameapi.manager.tools;

import cn.nukkit.Player;
import cn.nukkit.utils.BossBarColor;
import cn.nukkit.utils.DummyBossBar;

import java.util.HashMap;

/**
 * @author Glorydark
 */
public class BossBarManager {
    public static HashMap<Player, Long> bossBars;

    public static void createBossBar(Player player, String text, float length, BossBarColor bossBarColor) {
        if (bossBars.getOrDefault(player, null) != null) {
            player.removeBossBar(bossBars.get(player));
        }
        DummyBossBar bossBar = new DummyBossBar.Builder(player).text(text).color(bossBarColor).length(length).build();
        bossBars.put(player, bossBar.getBossBarId());
        player.createBossBar(bossBar);
    }

    public static void removeBossBar(Player player) {
        player.removeBossBar(bossBars.getOrDefault(player, 0L));
    }

}
