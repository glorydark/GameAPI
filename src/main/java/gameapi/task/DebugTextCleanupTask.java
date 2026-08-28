package gameapi.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.PluginTask;
import gameapi.GameAPI;
import gameapi.entity.data.DebugTextData;
import gameapi.manager.tools.DebugTextManager;

/**
 * 定时清理跨世界玩家的 DebugText 包。
 * <p>
 * 每 5 tick 执行一次。
 *
 * @author glorydark
 */
public class DebugTextCleanupTask extends PluginTask<GameAPI> {

    public DebugTextCleanupTask(GameAPI owner) {
        super(owner);
    }

    @Override
    public void onRun(int i) {
        String currentLevel;
        for (DebugTextData data : DebugTextManager.getDebugTextDataMap().values()) {
            currentLevel = data.getLocation().getLevelName();
            for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                if (!player.getLevel().getName().equals(currentLevel)) {
                    data.removeFrom(player);
                }
            }
        }
    }
}
