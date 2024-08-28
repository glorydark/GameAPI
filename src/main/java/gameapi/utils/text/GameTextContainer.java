package gameapi.utils.text;

import cn.nukkit.Player;

/**
 * @author glorydark
 */
public class GameTextContainer {

    private final String text;

    public GameTextContainer() {
        this.text = "";
    }

    public GameTextContainer(String text) {
        this.text = text;
    }

    public String getText(Player player) {
        return this.text;
    }
}
