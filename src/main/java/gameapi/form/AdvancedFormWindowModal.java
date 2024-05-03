package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.window.FormWindowModal;

import java.util.LinkedHashMap;
import java.util.function.Consumer;

public class AdvancedFormWindowModal extends FormWindowModal implements AdvancedForm {

    protected Consumer<Player> trueButtonResponseExecutor = null;

    protected Consumer<Player> falseButtonResponseExecutor = null;

    protected Consumer<Player> noResponseExecutor = null;

    public AdvancedFormWindowModal() {
        super("", "", "", "");
    }

    public AdvancedFormWindowModal(String title, String content, String firstButtonText, String secondButtonText) {
        super(title, content, firstButtonText, secondButtonText);
    }

    public void setNoResponseExecutor(Consumer<Player> noResponseExecutor) {
        this.noResponseExecutor = noResponseExecutor;
    }

    public void setFalseButtonResponseExecutor(Consumer<Player> falseButtonResponseExecutor) {
        this.falseButtonResponseExecutor = falseButtonResponseExecutor;
    }

    public void setTrueButtonResponseExecutor(Consumer<Player> trueButtonResponseExecutor) {
        this.trueButtonResponseExecutor = trueButtonResponseExecutor;
    }

    public void dealResponse(Player player, FormResponse response) {
        FormResponseModal responseModal = (FormResponseModal) response;
        if (this.wasClosed() || responseModal == null) {
            if (noResponseExecutor != null) {
                noResponseExecutor.accept(player);
            }
        } else {
            switch (responseModal.getClickedButtonId()) {
                case 0:
                    if (trueButtonResponseExecutor != null) {
                        trueButtonResponseExecutor.accept(player);
                    }
                    break;
                case 1:
                    if (falseButtonResponseExecutor != null) {
                        falseButtonResponseExecutor.accept(player);
                    }
                    break;
            }
        }
    }

    public void showFormWindow(Player player) {
        AdvancedFormMain.playerFormWindows.computeIfAbsent(player, i -> new LinkedHashMap<>()).put(player.showFormWindow(this), this);
    }

    public static class Builder {

        private String title;

        private String content;

        private Consumer<Player> firstButtonResponseExecutor;

        private Consumer<Player> secondButtonResponseExecutor;

        private Consumer<Player> noResponseExecutor;

        private String trueButtonText;

        private String falseButtonText;

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

        public Builder trueButtonText(String text) {
            this.trueButtonText = text;
            return this;
        }

        public Builder falseButtonText(String text) {
            this.falseButtonText = text;
            return this;
        }

        public Builder onTrueButtonClick(Consumer<Player> firstButtonResponseExecutor) {
            this.firstButtonResponseExecutor = firstButtonResponseExecutor;
            return this;
        }

        public Builder onFalseButtonClick(Consumer<Player> secondButtonResponseExecutor) {
            this.secondButtonResponseExecutor = secondButtonResponseExecutor;
            return this;
        }

        public Builder onClose(Consumer<Player> noResponseExecutor) {
            this.noResponseExecutor = noResponseExecutor;
            return this;
        }

        public AdvancedFormWindowModal build() {
            AdvancedFormWindowModal modal = new AdvancedFormWindowModal();
            modal.setTitle(title);
            modal.setContent(content);
            modal.setButton1(trueButtonText);
            modal.setButton2(falseButtonText);
            modal.trueButtonResponseExecutor = this.firstButtonResponseExecutor;
            modal.falseButtonResponseExecutor = this.secondButtonResponseExecutor;
            modal.noResponseExecutor = this.noResponseExecutor;
            return modal;
        }

    }
}
