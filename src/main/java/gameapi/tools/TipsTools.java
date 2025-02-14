package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import gameapi.tools.type.TipElementType;
import tip.messages.defaults.*;
import tip.utils.Api;

import java.util.LinkedList;

/**
 * @author LT_Name
 */

@SuppressWarnings("unused")
public class TipsTools {

    /**
     * 静态方法似乎会加载所有使用到的依赖
     * 我们需要兼容多个Tips版本
     */
    private static final TipsTools TIPS_TOOLS = new TipsTools();

    private TipsTools() {

    }

    /**
     * 关闭Tips显示
     *
     * @param level  世界
     * @param player 玩家
     */
    public static void closeTipsShow(String level, Player player, TipElementType... tipElementTypes) {
        TIPS_TOOLS.closeTipsShowInternal(level, player.getName(), tipElementTypes);
    }

    /**
     * 移除Tips设置
     *
     * @param level  世界
     * @param player 玩家
     */
    public static void removeTipsConfig(String level, Player player, TipElementType... tipElementTypes) {
        TIPS_TOOLS.removeTipsConfigInternal(level, player.getName(), tipElementTypes);
    }

    private void closeTipsShowInternal(String level, String playerName, TipElementType... tipElementTypes) {
        for (TipElementType tipElementType : tipElementTypes) {
            switch (tipElementType) {
                case TIP:
                    try {
                        Api.setPlayerShowMessage(
                                playerName,
                                new TipMessage(level, false, TipMessage.TIP, "")
                        );
                    } catch (Exception e) {
                        try {
                            Api.setPlayerShowMessage(
                                    playerName,
                                    new tip.messages.TipMessage(level, false, 0, "")
                            );
                        } catch (Exception ignored) {

                        }
                    }
                    Player player = Server.getInstance().getPlayer(playerName);
                    if (player != null) {
                        player.setNameTag(player.getName());
                        player.setNameTagVisible(true);
                        player.setNameTagAlwaysVisible(true);
                    }
                    break;
                case SCOREBOARD:
                    try {
                        Api.setPlayerShowMessage(
                                playerName,
                                new ScoreBoardMessage(level, false, "", new LinkedList<>())
                        );
                    } catch (Exception e) {
                        try {
                            Api.setPlayerShowMessage(
                                    playerName,
                                    new tip.messages.ScoreBoardMessage(level, false, "", new LinkedList<>())
                            );
                        } catch (Exception ignored) {

                        }
                    }
                    break;
                case BROADCAST:
                    Api.setPlayerShowMessage(
                            playerName,
                            new BroadcastMessage(level, false, 5, new LinkedList<>())
                    );
                    break;
                case BOSS_BAR:
                    try {
                        Api.setPlayerShowMessage(
                                playerName,
                                new BossBarMessage(level, false, 5, false, new LinkedList<>())
                        );
                    } catch (Exception e) {
                        try {
                            Api.setPlayerShowMessage(
                                    playerName,
                                    new tip.messages.BossBarMessage(level, false, 5, false, new LinkedList<>())
                            );
                        } catch (Exception ignored) {

                        }
                    }
                    break;
                case NAMETAG:
                    try {
                        Api.setPlayerShowMessage(
                                playerName,
                                new NameTagMessage(level, false, "")
                        );
                    } catch (Exception e) {
                        try {
                            Api.setPlayerShowMessage(
                                    playerName,
                                    new tip.messages.NameTagMessage(level, true, "")
                            );
                        } catch (Exception ignored) {

                        }
                    }
                    break;
                case CHAT:
                    Api.setPlayerShowMessage(
                            playerName,
                            new ChatMessage(level, false, "", true)
                    );
                    break;
            }
        }
    }

    private void removeTipsConfigInternal(String level, String playerName, TipElementType... tipElementTypes) {
        for (TipElementType tipElementType : tipElementTypes) {
            switch (tipElementType) {
                case TIP:
                    try {
                        Api.removePlayerShowMessage(
                                playerName,
                                new TipMessage(level, false, TipMessage.TIP, "")
                        );
                    } catch (Exception e) {
                        try {
                            Api.removePlayerShowMessage(
                                    playerName,
                                    new tip.messages.TipMessage(level, false, 0, "")
                            );
                        } catch (Exception ignored) {

                        }
                    }
                    break;
                case CHAT:
                    Api.removePlayerShowMessage(
                            playerName,
                            new ChatMessage(level, false, "", true)
                    );
                    break;
                case NAMETAG:
                    try {
                        Api.removePlayerShowMessage(
                                playerName,
                                new NameTagMessage(level, true, "")
                        );
                    } catch (Exception e) {
                        try {
                            Api.removePlayerShowMessage(
                                    playerName,
                                    new tip.messages.NameTagMessage(level, true, "")
                            );
                        } catch (Exception ignored) {

                        }
                    }
                    break;
                case BOSS_BAR:
                    try {
                        Api.removePlayerShowMessage(
                                playerName,
                                new BossBarMessage(level, false, 5, false, new LinkedList<>())
                        );
                    } catch (Exception e) {
                        try {
                            Api.removePlayerShowMessage(
                                    playerName,
                                    new tip.messages.BossBarMessage(level, false, 5, false, new LinkedList<>())
                            );
                        } catch (Exception ignored) {

                        }
                    }
                    break;
                case BROADCAST:
                    Api.removePlayerShowMessage(
                            playerName,
                            new BroadcastMessage(level, false, 5, new LinkedList<>())
                    );
                    break;
                case SCOREBOARD:
                    try {
                        Api.removePlayerShowMessage(
                                playerName,
                                new ScoreBoardMessage(level, false, "", new LinkedList<>())
                        );
                    } catch (Exception e) {
                        try {
                            Api.removePlayerShowMessage(
                                    playerName,
                                    new tip.messages.ScoreBoardMessage(level, false, "", new LinkedList<>())
                            );
                        } catch (Exception ignored) {

                        }
                    }
                    break;
            }
        }
    }

}