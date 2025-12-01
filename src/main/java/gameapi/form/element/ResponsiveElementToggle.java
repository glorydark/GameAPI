package gameapi.form.element;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementToggle;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

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

    public ResponsiveElementToggle text(String text) {
        this.setText(text);
        return this;
    }

    public ResponsiveElementToggle text(Supplier<String> supplier) {
        this.setText(supplier.get());
        return this;
    }

    public ResponsiveElementToggle toolTip(String s) {
        this.setTooltip(s);
        return this;
    }
}
