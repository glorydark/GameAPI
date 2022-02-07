package gameapi.bossbar;

import cn.nukkit.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.BossBarColor;
import cn.nukkit.utils.DummyBossBar;

import java.util.HashMap;

public class BossBar {
    public static HashMap<Player, Long> bossBars;

    public static void createBossBar(Player player, String text, BlockColor blockColor){
        DummyBossBar bossBar = new DummyBossBar.Builder(player).text(text).color(BossBarColor.BLUE).build();
        bossBars.put(player, bossBar.getBossBarId());
        player.createBossBar(bossBar);
    }

    /*
    public static void createBossBar(Player player, String text, Integer r, Integer g, Integer b){
        DummyBossBar bossBar = new DummyBossBar.Builder(player).text(text).color(BossBarColor.BLUE).build();
        bossBars.put(player, bossBar.getBossBarId());
        player.createBossBar(bossBar);
    }

     */
}
