package gameapi.manager;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.logger.CustomFormatter;
import gameapi.tools.CalendarTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * @author glorydark
 */
public class GameDebugManager {

    private final Logger logger;

    private boolean enableConsoleDebug = true;

    protected List<Player> players = new ArrayList<>();

    public GameDebugManager(String name, File saveDir) {
        try {
            String date = CalendarTools.getDateStringWithoutDetailsByDefault(System.currentTimeMillis());
            File dic = new File(saveDir.getPath() + File.separator + date + File.separator);
            dic.mkdirs();
            logger = Logger.getLogger(name);
            logger.setUseParentHandlers(false);
            FileHandler fileHandler = new FileHandler(dic.getPath() + File.separator + CalendarTools.getDateStringByDefault(System.currentTimeMillis()).replace(" ", "_") + ".log");
            // Set a formatter to format log records
            CustomFormatter formatter = new CustomFormatter();
            fileHandler.setFormatter(formatter);
            // Add the FileHandler to the logger
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void info(String message) {
        this.info(message, false);
    }

    public void info(String message, boolean sendPlayer) {
        if (sendPlayer) {
            for (Player player : players) {
                player.sendMessage(message);
            }
        }
        logger.info(TextFormat.clean(message));
        if (enableConsoleDebug) {
            GameAPI.getInstance().getLogger().info(message);
        }
    }

    public void warning(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
        logger.warning(TextFormat.clean(message));
        if (enableConsoleDebug) {
            GameAPI.getInstance().getLogger().warning(message);
        }
    }

    public void error(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
        logger.severe(TextFormat.clean(message));
        if (enableConsoleDebug) {
            GameAPI.getInstance().getLogger().error(message);
        }
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setEnableConsoleDebug(boolean enableConsoleDebug) {
        this.enableConsoleDebug = enableConsoleDebug;
    }

    public boolean isEnableConsoleDebug() {
        return enableConsoleDebug;
    }
}
