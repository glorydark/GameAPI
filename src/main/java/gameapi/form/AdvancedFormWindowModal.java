package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.window.FormWindowModal;
import gameapi.listener.AdvancedFormListener;

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

    public AdvancedFormWindowModal onClose(Consumer<Player> noResponseExecutor) {
        this.noResponseExecutor = noResponseExecutor;
        return this;
    }

    public AdvancedFormWindowModal onClickFalse(Consumer<Player> falseButtonResponseExecutor) {
        this.falseButtonResponseExecutor = falseButtonResponseExecutor;
        return this;
    }

    public AdvancedFormWindowModal onClickTrue(Consumer<Player> trueButtonResponseExecutor) {
        this.trueButtonResponseExecutor = trueButtonResponseExecutor;
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
        AdvancedFormListener.playerFormWindows.computeIfAbsent(player, i -> new LinkedHashMap<>()).put(player.showFormWindow(this), this);
    }
}
