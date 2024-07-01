package gameapi.form.element;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementSlider;

import java.util.function.BiConsumer;

/**
 * @author glorydark
 */
public class ResponsiveElementSlider extends ElementSlider {

    protected BiConsumer<Player, Float> response = null;

    public ResponsiveElementSlider(String text, float min, float max) {
        super(text, min, max);
    }

    public ResponsiveElementSlider(String text, float min, float max, int step) {
        super(text, min, max, step);
    }

    public ResponsiveElementSlider(String text, float min, float max, int step, float defaultValue) {
        super(text, min, max, step, defaultValue);
    }

    public ResponsiveElementSlider onRespond(BiConsumer<Player, Float> response) {
        this.response = response;
        return this;
    }

    public BiConsumer<Player, Float> getResponse() {
        return response;
    }
}
