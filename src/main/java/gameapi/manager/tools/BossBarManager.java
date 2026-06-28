package gameapi.manager.tools;

import cn.nukkit.Player;
import cn.nukkit.utils.BossBarColor;
import cn.nukkit.utils.DummyBossBar;

import java.util.HashMap;

/**
 * @author Glorydark
 */
public class BossBarManager {
    public static HashMap<Player, Long> bossBars = new HashMap<>();

    public static void createBossBar(Player player, String text, float length, BossBarColor bossBarColor) {
        Long id = bossBars.get(player);
        if (id != null) {
            DummyBossBar existing = player.getDummyBossBar(id);
            if (existing != null) {
                player.updateBossBar(text, (int) length, id);
                return;
            }
            player.removeBossBar(id);
        }
        DummyBossBar bossBar = new DummyBossBar.Builder(player).text(text).color(bossBarColor).length(length).build();
        bossBars.put(player, bossBar.getBossBarId());
        player.createBossBar(bossBar);
    }

    public static void removeBossBar(Player player) {
        Long id = bossBars.remove(player);
        if (id != null) {
            player.removeBossBar(id);
        }
    }

}
