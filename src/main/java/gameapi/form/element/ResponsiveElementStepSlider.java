package gameapi.form.element;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementStepSlider;
import cn.nukkit.form.response.FormResponseData;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author glorydark
 */
public class ResponsiveElementStepSlider extends ElementStepSlider {

    protected BiConsumer<Player, FormResponseData> response = null;

    public ResponsiveElementStepSlider(String text) {
        super(text);
    }

    public ResponsiveElementStepSlider(String text, List<String> steps) {
        super(text, steps);
    }

    public ResponsiveElementStepSlider(String text, List<String> steps, int defaultStep) {
        super(text, steps, defaultStep);
    }

    public ResponsiveElementStepSlider onRespond(BiConsumer<Player, FormResponseData> response) {
        this.response = response;
        return this;
    }

    public BiConsumer<Player, FormResponseData> getResponse() {
        return response;
    }
}
