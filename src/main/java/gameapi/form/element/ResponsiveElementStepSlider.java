package gameapi.form.element;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementStepSlider;
import cn.nukkit.form.response.FormResponseData;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

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

    public ResponsiveElementStepSlider text(String text) {
        this.setText(text);
        return this;
    }

    public ResponsiveElementStepSlider text(Supplier<String> supplier) {
        this.setText(supplier.get());
        return this;
    }

    public ResponsiveElementStepSlider step(String option) {
        this.getSteps().add(option);
        return this;
    }

    public ResponsiveElementStepSlider step(Supplier<String> supplier) {
        this.getSteps().add(supplier.get());
        return this;
    }

    public ResponsiveElementStepSlider steps(List<String> stringList) {
        this.getSteps().addAll(stringList);
        return this;
    }

    public ResponsiveElementStepSlider steps(Supplier<List<String>> supplier) {
        this.getSteps().addAll(supplier.get());
        return this;
    }

    public ResponsiveElementStepSlider toolTip(String s) {
        this.setTooltip(s);
        return this;
    }
}
