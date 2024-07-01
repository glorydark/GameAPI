package gameapi.form.element;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementToggle;

import java.util.function.BiConsumer;

/**
 * @author glorydark
 */
public class ResponsiveElementToggle extends ElementToggle {

    protected BiConsumer<Player, Boolean> response = null;

    public ResponsiveElementToggle(String text) {
        super(text);
    }

    public ResponsiveElementToggle(String text, boolean defaultValue) {
        super(text, defaultValue);
    }

    public ResponsiveElementToggle onRespond(BiConsumer<Player, Boolean> response) {
        this.response = response;
        return this;
    }

    public BiConsumer<Player, Boolean> getResponse() {
        return response;
    }
}
