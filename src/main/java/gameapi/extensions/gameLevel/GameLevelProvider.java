package gameapi.extensions.gameLevel;

import gameapi.annotation.Future;

/**
 * @author glorydark
 * @date {2024/1/4} {22:27}
 */
@Future
public interface GameLevelProvider {

    void setPlayerLevel(String player, int level);

    int getPlayerLevel(String player);

    void addPlayerExp(String player, int exp);

    void reducePlayerExp(String player, int exp);

    int getPlayerExp(String player);

    void setPlayerExp(String player, int exp);

    void updatePlayerLevelData(String player);
}
