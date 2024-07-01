package gameapi.form.element;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.response.FormResponseData;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author glorydark
 */
public class ResponsiveElementDropdown extends ElementDropdown {

    protected BiConsumer<Player, FormResponseData> response = null;

    public ResponsiveElementDropdown(String text) {
        super(text);
    }

    public ResponsiveElementDropdown(String text, List<String> steps) {
        super(text, steps);
    }

    public ResponsiveElementDropdown(String text, List<String> steps, int defaultStep) {
        super(text, steps, defaultStep);
    }

    public ResponsiveElementDropdown onRespond(BiConsumer<Player, FormResponseData> response) {
        this.response = response;
        return this;
    }

    public BiConsumer<Player, FormResponseData> getResponse() {
        return response;
    }
}
