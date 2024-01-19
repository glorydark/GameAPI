package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AdvancedFormWindowSimple extends FormWindowSimple implements AdvancedForm {

    protected BiConsumer<Player, FormResponseSimple> responseExecutor = (player, responseSimple) -> {};

    protected Consumer<Player> noResponseExecutor = player -> {};

    public AdvancedFormWindowSimple(String title, String content) {
        super(title, content);
    }

    public AdvancedFormWindowSimple() {
        super("", "");
    }

    public void dealResponse(Player player, FormResponse response) {
        FormResponseSimple responseSimple = (FormResponseSimple) response;
        if (this.wasClosed() || response == null) {
            noResponseExecutor.accept(player);
        } else {
            responseExecutor.accept(player, responseSimple);
        }
    }

    public void setResponseExecutor(BiConsumer<Player, FormResponseSimple> responseExecutor) {
        this.responseExecutor = responseExecutor;
    }

    public void setNoResponseExecutor(Consumer<Player> noResponseExecutor) {
        this.noResponseExecutor = noResponseExecutor;
    }

    public static class Builder {

        private String title;

        private String content;

        private List<ElementButton> buttonList;

        private BiConsumer<Player, FormResponseSimple> responseExecutor;

        private Consumer<Player> noResponseExecutor;

        public Builder() {

        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder addButton(String text) {
            buttonList.add(new ElementButton(text));
            return this;
        }

        public Builder addButton(String text, String icon, String iconPathType) {
            buttonList.add(new ElementButton(text, new ElementButtonImageData(icon, iconPathType)));
            return this;
        }

        public Builder onButtonClick(BiConsumer<Player, FormResponseSimple> responseExecutor) {
            this.responseExecutor = responseExecutor;
            return this;
        }

        public Builder onClose(Consumer<Player> noResponseExecutor) {
            this.noResponseExecutor = noResponseExecutor;
            return this;
        }

        public AdvancedFormWindowSimple build() {
            AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple();
            simple.setTitle(title);
            simple.setContent(content);
            simple.getButtons().addAll(buttonList);
            simple.responseExecutor = this.responseExecutor;
            simple.noResponseExecutor = this.noResponseExecutor;
            return simple;
        }

    }
}
