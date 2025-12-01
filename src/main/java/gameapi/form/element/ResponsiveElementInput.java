package gameapi.form.element;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author glorydark
 */
public class ResponsiveElementInput extends ElementInput {

    protected BiConsumer<Player, String> response = null;

    public ResponsiveElementInput(String text) {
        super(text);
    }

    public ResponsiveElementInput(String text, String placeholder) {
        super(text, placeholder);
    }

    public ResponsiveElementInput(String text, String placeholder, String defaultText) {
        super(text, placeholder, defaultText);
    }

    public ResponsiveElementInput onRespond(BiConsumer<Player, String> response) {
        this.response = response;
        return this;
    }

    public BiConsumer<Player, String> getResponse() {
        return response;
    }

    public ResponsiveElementInput text(String text) {
        this.setText(text);
        return this;
    }

    public ResponsiveElementInput text(Supplier<String> supplier) {
        this.setText(supplier.get());
        return this;
    }

    public ResponsiveElementInput toolTip(String s) {
        this.setTooltip(s);
        return this;
    }
}
