package gameapi.utils;

import cn.nukkit.Player;
import tip.messages.defaults.*;
import tip.utils.Api;

import java.util.LinkedList;

/**
 * @author LT_Name
 */

@SuppressWarnings("unused")
public class Tips {

    /**
     * 静态方法似乎会加载所有使用到的依赖
     * 我们需要兼容多个Tips版本
     */
    private static final Tips TIPS = new Tips();

    private Tips() {

    }

    /**
     * 关闭Tips显示
     * @param level 世界
     * @param player 玩家
     */
    public static void closeTipsShow(String level, Player player) {
        TIPS.closeTipsShowInternal(level, player.getName());
    }

    /**
     * 移除Tips设置
     * @param level 世界
     * @param player 玩家
     */
    public static void removeTipsConfig(String level, Player player) {
        TIPS.removeTipsConfigInternal(level, player.getName());
    }

    private void closeTipsShowInternal(String level, String playerName) {
        try {
            Api.setPlayerShowMessage(
                    playerName,
                    new BossBarMessage(level, false, 5, false, new LinkedList<>())
            );
            Api.setPlayerShowMessage(
                    playerName,
                    new BroadcastMessage(level, false, 5, new LinkedList<>())
            );
            Api.setPlayerShowMessage(
                    playerName,
                    new ChatMessage(level, false, "", true)
            );
            Api.setPlayerShowMessage(
                    playerName,
                    new NameTagMessage(level, true, "")
            );
            Api.setPlayerShowMessage(
                    playerName,
                    new ScoreBoardMessage(level, false, "", new LinkedList<>())
            );
            Api.setPlayerShowMessage(
                    playerName,
                    new TipMessage(level, false, TipMessage.TIP, "")
            );
        } catch (Exception e) {
            try {
                Api.setPlayerShowMessage(
                        playerName,
                        new tip.messages.BossBarMessage(level, false, 5, false, new LinkedList<>())
                );
                Api.setPlayerShowMessage(
                        playerName,
                        new tip.messages.NameTagMessage(level, true, "")
                );
                Api.setPlayerShowMessage(
                        playerName,
                        new tip.messages.ScoreBoardMessage(level, false, "", new LinkedList<>())
                );
                Api.setPlayerShowMessage(
                        playerName,
                        new tip.messages.TipMessage(level, false, 0, "")
                );
            } catch (Exception ignored) {

            }
        }
    }

    private void removeTipsConfigInternal(String level, String playerName) {
        try {
            Api.removePlayerShowMessage(
                    playerName,
                    new BossBarMessage(level, false, 5, false, new LinkedList<>())
            );
            Api.removePlayerShowMessage(
                    playerName,
                    new BroadcastMessage(level, false, 5, new LinkedList<>())
            );
            Api.removePlayerShowMessage(
                    playerName,
                    new ChatMessage(level, false, "", true)
            );
            Api.removePlayerShowMessage(
                    playerName,
                    new NameTagMessage(level, true, "")
            );
            Api.removePlayerShowMessage(
                    playerName,
                    new ScoreBoardMessage(level, false, "", new LinkedList<>())
            );
            Api.removePlayerShowMessage(
                    playerName,
                    new TipMessage(level, false, TipMessage.TIP, "")
            );
        } catch (Exception e) {
            try {
                Api.removePlayerShowMessage(
                        playerName,
                        new tip.messages.BossBarMessage(level, false, 5, false, new LinkedList<>())
                );
                Api.removePlayerShowMessage(
                        playerName,
                        new tip.messages.NameTagMessage(level, true, "")
                );
                Api.removePlayerShowMessage(
                        playerName,
                        new tip.messages.ScoreBoardMessage(level, false, "", new LinkedList<>())
                );
                Api.removePlayerShowMessage(
                        playerName,
                        new tip.messages.TipMessage(level, false, 0, "")
                );
            } catch (Exception ignored) {

            }
        }
    }

}