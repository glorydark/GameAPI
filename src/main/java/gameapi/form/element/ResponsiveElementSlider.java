package gameapi.form.element;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementSlider;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

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

    public ResponsiveElementSlider text(String text) {
        this.setText(text);
        return this;
    }

    public ResponsiveElementSlider text(Supplier<String> supplier) {
        this.setText(supplier.get());
        return this;
    }

    public ResponsiveElementSlider toolTip(String s) {
        this.setTooltip(s);
        return this;
    }
}
