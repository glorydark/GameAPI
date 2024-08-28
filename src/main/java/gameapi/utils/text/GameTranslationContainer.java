package gameapi.utils.text;

import cn.nukkit.Player;
import gameapi.GameAPI;

/**
 * @author glorydark
 */
public class GameTranslationContainer extends GameTextContainer {

    private final Object[] params;

    public GameTranslationContainer() {
        this("");
    }

    public GameTranslationContainer(String text, Object... params) {
        super(text);
        this.params = params;
    }

    @Override
    public String getText(Player player) {
        return GameAPI.getLanguage().getTranslation(player, super.getText(player), this.params);
    }
}
