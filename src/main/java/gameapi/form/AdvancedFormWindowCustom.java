package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.*;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseData;
import cn.nukkit.form.window.FormWindowCustom;
import gameapi.form.element.*;
import gameapi.listener.AdvancedFormListener;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AdvancedFormWindowCustom extends FormWindowCustom implements AdvancedForm {

    protected BiConsumer<Player, FormResponseCustom> responseExecutor = null;

    protected Consumer<Player> noResponseExecutor = null;

    public AdvancedFormWindowCustom(String title) {
        super(title);
    }

    public AdvancedFormWindowCustom() {
        super("");
    }

    public AdvancedFormWindowCustom title(Supplier<String> supplier) {
        return this.title(supplier.get());
    }

    public AdvancedFormWindowCustom title(String string) {
        this.setTitle(string);
        return this;
    }

    public void dealResponse(Player player, FormResponse response) {
        FormResponseCustom custom = (FormResponseCustom) response;
        if (this.wasClosed() || custom == null) {
            if (this.noResponseExecutor != null) {
                this.noResponseExecutor.accept(player);
            }
        } else {
            for (int i = 0; i < this.getElements().size(); i++) {
                Element element = this.getElements().get(i);
                if (element instanceof ResponsiveElementInput) {
                    ResponsiveElementInput converted = (ResponsiveElementInput) element;
                    BiConsumer<Player, String> consumer = converted.getResponse();
                    if (consumer != null) {
                        consumer.accept(player, custom.getInputResponse(i));
                    }
                } else if (element instanceof ResponsiveElementLabel) {
                    ResponsiveElementLabel converted = (ResponsiveElementLabel) element;
                    BiConsumer<Player, String> consumer = converted.getResponse();
                    if (consumer != null) {
                        consumer.accept(player, custom.getLabelResponse(i));
                    }
                } else if (element instanceof ResponsiveElementToggle) {
                    ResponsiveElementToggle converted = (ResponsiveElementToggle) element;
                    BiConsumer<Player, Boolean> consumer = converted.getResponse();
                    if (consumer != null) {
                        consumer.accept(player, custom.getToggleResponse(i));
                    }
                } else if (element instanceof ResponsiveElementSlider) {
                    ResponsiveElementSlider converted = (ResponsiveElementSlider) element;
                    BiConsumer<Player, Float> consumer = converted.getResponse();
                    if (consumer != null) {
                        consumer.accept(player, custom.getSliderResponse(i));
                    }
                } else if (element instanceof ResponsiveElementStepSlider) {
                    ResponsiveElementStepSlider converted = (ResponsiveElementStepSlider) element;
                    BiConsumer<Player, FormResponseData> consumer = converted.getResponse();
                    if (consumer != null) {
                        consumer.accept(player, custom.getStepSliderResponse(i));
                    }
                } else if (element instanceof ResponsiveElementDropdown) {
                    ResponsiveElementDropdown converted = (ResponsiveElementDropdown) element;
                    BiConsumer<Player, FormResponseData> consumer = converted.getResponse();
                    if (consumer != null) {
                        consumer.accept(player, custom.getDropdownResponse(i));
                    }
                }
            }
            if (this.responseExecutor != null) {
                this.responseExecutor.accept(player, custom);
            }
        }
    }

    public AdvancedFormWindowCustom onRespond(BiConsumer<Player, FormResponseCustom> responseExecutor) {
        this.responseExecutor = responseExecutor;
        return this;
    }

    public AdvancedFormWindowCustom onClose(Consumer<Player> noResponseExecutor) {
        this.noResponseExecutor = noResponseExecutor;
        return this;
    }

    public void showToPlayer(Player player) {
        AdvancedFormListener.showToPlayer(player, this);
    }

    // basic functions
    public AdvancedFormWindowCustom label(ElementLabel element) {
        this.addElement(element);
        return this;
    }

    public AdvancedFormWindowCustom input(ElementInput element) {
        this.addElement(element);
        return this;
    }

    public AdvancedFormWindowCustom dropdown(ElementDropdown element) {
        this.addElement(element);
        return this;
    }

    public AdvancedFormWindowCustom toggle(ElementToggle element) {
        this.addElement(element);
        return this;
    }

    public AdvancedFormWindowCustom slider(ElementSlider element) {
        this.addElement(element);
        return this;
    }

    public AdvancedFormWindowCustom stepSlider(ElementStepSlider element) {
        this.addElement(element);
        return this;
    }
}
