package gameapi.form.element;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.response.FormResponseData;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author glorydark
 */
public class ResponsiveElementDropdown extends ElementDropdown {

    protected BiConsumer<Player, FormResponseData> response = null;

    public ResponsiveElementDropdown(String text) {
        super(text);
    }

    public ResponsiveElementDropdown(String text, List<String> options) {
        super(text, options);
    }

    public ResponsiveElementDropdown(String text, List<String> options, int defaultStep) {
        super(text, options, defaultStep);
    }

    public ResponsiveElementDropdown onRespond(BiConsumer<Player, FormResponseData> response) {
        this.response = response;
        return this;
    }

    public BiConsumer<Player, FormResponseData> getResponse() {
        return response;
    }

    public ResponsiveElementDropdown text(String text) {
        this.setText(text);
        return this;
    }

    public ResponsiveElementDropdown text(Supplier<String> supplier) {
        this.setText(supplier.get());
        return this;
    }

    public ResponsiveElementDropdown option(String option) {
        this.getOptions().add(option);
        return this;
    }

    public ResponsiveElementDropdown option(Supplier<String> supplier) {
        this.getOptions().add(supplier.get());
        return this;
    }

    public ResponsiveElementDropdown options(List<String> stringList) {
        this.getOptions().addAll(stringList);
        return this;
    }

    public ResponsiveElementDropdown options(Supplier<List<String>> supplier) {
        this.getOptions().addAll(supplier.get());
        return this;
    }
}
