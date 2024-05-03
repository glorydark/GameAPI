package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.Element;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindowCustom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AdvancedFormWindowCustom extends FormWindowCustom implements AdvancedForm {

    protected BiConsumer<Player, FormResponseCustom> responseExecutor = null;

    protected Consumer<Player> noResponseExecutor = null;

    public AdvancedFormWindowCustom(String title) {
        super(title);
    }

    public AdvancedFormWindowCustom() {
        super("");
    }

    public void dealResponse(Player player, FormResponse response) {
        if (this.wasClosed() || response == null) {
            if (noResponseExecutor != null) {
                noResponseExecutor.accept(player);
            }
        } else {
            if (responseExecutor != null) {
                responseExecutor.accept(player, (FormResponseCustom) response);
            }
        }
    }

    public void setResponseExecutor(BiConsumer<Player, FormResponseCustom> responseExecutor) {
        this.responseExecutor = responseExecutor;
    }

    public void setNoResponseExecutor(Consumer<Player> noResponseExecutor) {
        this.noResponseExecutor = noResponseExecutor;
    }

    public void showFormWindow(Player player) {
        AdvancedFormMain.playerFormWindows.computeIfAbsent(player, i -> new LinkedHashMap<>()).put(player.showFormWindow(this), this);
    }

    public static class Builder {

        protected List<Element> elements = new ArrayList<>();
        private String title;
        private BiConsumer<Player, FormResponseCustom> responseExecutor;
        private Consumer<Player> noResponseExecutor;

        public Builder() {

        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder onResponse(BiConsumer<Player, FormResponseCustom> responseExecutor) {
            this.responseExecutor = responseExecutor;
            return this;
        }

        public Builder onClose(Consumer<Player> noResponseExecutor) {
            this.noResponseExecutor = noResponseExecutor;
            return this;
        }

        public Builder addElement(Element element) {
            this.elements.add(element);
            return this;
        }

        public AdvancedFormWindowCustom build() {
            AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom();
            custom.setTitle(title);
            for (Element element : elements) {
                custom.addElement(element);
            }
            custom.responseExecutor = this.responseExecutor;
            custom.noResponseExecutor = this.noResponseExecutor;
            return custom;
        }

    }
}
