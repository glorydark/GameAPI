package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.window.FormWindowModal;
import gameapi.listener.AdvancedFormListener;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AdvancedFormWindowModal extends FormWindowModal implements AdvancedForm {

    protected Consumer<Player> trueButtonResponseExecutor = null;

    protected Consumer<Player> falseButtonResponseExecutor = null;

    protected Consumer<Player> noResponseExecutor = null;

    public AdvancedFormWindowModal() {
        this("", "", "", "");
    }

    public AdvancedFormWindowModal(String title, String content, String firstButtonText, String secondButtonText) {
        super(title, content, firstButtonText, secondButtonText);
    }

    public AdvancedFormWindowModal title(Supplier<String> supplier) {
        return this.title(supplier.get());
    }

    public AdvancedFormWindowModal title(String string) {
        this.setTitle(string);
        return this;
    }

    public AdvancedFormWindowModal content(Supplier<String> supplier) {
        return this.content(supplier.get());
    }

    public AdvancedFormWindowModal content(String string) {
        this.setContent(string);
        return this;
    }

    public AdvancedFormWindowModal onClose(Consumer<Player> noResponseExecutor) {
        this.noResponseExecutor = noResponseExecutor;
        return this;
    }

    public AdvancedFormWindowModal trueButton(String content, Consumer<Player> trueButtonResponseExecutor) {
        this.setButton1(content);
        this.trueButtonResponseExecutor = trueButtonResponseExecutor;
        return this;
    }

    public AdvancedFormWindowModal falseButton(String content, Consumer<Player> falseButtonResponseExecutor) {
        this.setButton2(content);
        this.falseButtonResponseExecutor = falseButtonResponseExecutor;
        return this;
    }

    public void dealResponse(Player player, FormResponse response) {
        FormResponseModal responseModal = (FormResponseModal) response;
        if (this.wasClosed() || responseModal == null) {
            if (this.noResponseExecutor != null) {
                this.noResponseExecutor.accept(player);
            }
        } else {
            switch (responseModal.getClickedButtonId()) {
                case 0:
                    if (this.trueButtonResponseExecutor != null) {
                        this.trueButtonResponseExecutor.accept(player);
                    }
                    break;
                case 1:
                    if (this.falseButtonResponseExecutor != null) {
                        this.falseButtonResponseExecutor.accept(player);
                    }
                    break;
            }
        }
    }

    public void showToPlayer(Player player) {
        AdvancedFormListener.showToPlayer(player, this);
    }
}
