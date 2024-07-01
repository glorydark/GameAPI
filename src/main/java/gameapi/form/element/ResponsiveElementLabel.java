package gameapi.form.element;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementLabel;

import java.util.function.BiConsumer;

/**
 * @author glorydark
 */
public class ResponsiveElementLabel extends ElementLabel {

    protected BiConsumer<Player, String> response = null;

    public ResponsiveElementLabel(String text) {
        super(text);
    }

    public ResponsiveElementLabel onRespond(BiConsumer<Player, String> response) {
        this.response = response;
        return this;
    }

    public BiConsumer<Player, String> getResponse() {
        return response;
    }
}
