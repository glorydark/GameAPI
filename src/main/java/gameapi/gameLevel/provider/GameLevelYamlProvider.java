package gameapi.gameLevel.provider;

import cn.nukkit.utils.Config;
import gameapi.GameAPI;

import java.io.File;

/**
 * @author glorydark
 * @date {2024/1/4} {22:29}
 */
public class GameLevelYamlProvider implements GameLevelProvider {

    String path;

    public GameLevelYamlProvider(String path) {
        this.path = path;
    }

    @Override
    public void setPlayerLevel(String player, int level) {
        new Config(getFile(player), Config.YAML).set("level", level);
    }

    @Override
    public void setPlayerExp(String player, int exp) {
        new Config(getFile(player), Config.YAML).set("exp", exp);
    }

    @Override
    public int getPlayerLevel(String player) {
        File file = getFile(player);
        if (!file.exists()) {
            return 0;
        }
        return new Config(file, Config.YAML).getInt("level", 0);
    }

    @Override
    public void addPlayerExp(String player, int exp) {
        Config config = new Config(getFile(player), Config.YAML);
        config.set("exp", config.getInt("exp", 0) + exp);
        config.save();
    }

    @Override
    public void reducePlayerExp(String player, int exp) {
        Config config = new Config(getFile(player), Config.YAML);
        config.set("exp", config.getInt("exp", 0) - exp);
        config.save();
    }

    @Override
    public int getPlayerExp(String player) {
        File file = getFile(player);
        if (!file.exists()) {
            return 0;
        }
        return new Config(file, Config.YAML).getInt("exp", 0);
    }

    // todo: calculate when player join the game
    @Override
    public void updatePlayerLevelData(String player) {
        setPlayerLevel(player, GameAPI.system.getLevelByExperience(getPlayerExp(player)));
        setPlayerExp(player, GameAPI.system.getLevelSurplusExp(player));
    }

    public File getFile(String player) {
        return new File(path + "/" + player + ".yml");
    }
}
