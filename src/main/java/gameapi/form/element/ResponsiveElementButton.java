package gameapi.form.element;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author glorydark
 */
public class ResponsiveElementButton extends ElementButton {

    protected Consumer<Player> response = null;

    public ResponsiveElementButton(String text) {
        super(text);
    }

    public ResponsiveElementButton(String text, ElementButtonImageData image) {
        super(text, image);
    }

    public ResponsiveElementButton onRespond(Consumer<Player> onClickResponse) {
        this.response = onClickResponse;
        return this;
    }

    public Consumer<Player> getResponse() {
        return response;
    }

    public ResponsiveElementButton text(String text) {
        this.setText(text);
        return this;
    }

    public ResponsiveElementButton text(Supplier<String> supplier) {
        this.setText(supplier.get());
        return this;
    }
}
