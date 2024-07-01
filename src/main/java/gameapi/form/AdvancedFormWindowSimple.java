package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;
import gameapi.listener.AdvancedFormListener;

import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AdvancedFormWindowSimple extends FormWindowSimple implements AdvancedForm {

    protected BiConsumer<Player, FormResponseSimple> responseExecutor;

    protected Consumer<Player> noResponseExecutor;

    public AdvancedFormWindowSimple(String title, String content) {
        super(title, content);
    }

    public AdvancedFormWindowSimple() {
        super("", "");
    }

    public void dealResponse(Player player, FormResponse response) {
        FormResponseSimple responseSimple = (FormResponseSimple) response;
        if (this.wasClosed() || response == null) {
            if (this.noResponseExecutor != null) {
                this.noResponseExecutor.accept(player);
            }
        } else {
            for (ElementButton button : this.getButtons()) {
                if (button instanceof ResponsiveElementButton) {
                    ResponsiveElementButton responsiveElementButton = (ResponsiveElementButton) button;
                    Consumer<Player> onClickResponse = responsiveElementButton.getResponse();
                    if (onClickResponse != null) {
                        onClickResponse.accept(player);
                    }
                }
            }
            if (this.responseExecutor != null) {
                this.responseExecutor.accept(player, responseSimple);
            }
        }
    }

    public void onRespond(BiConsumer<Player, FormResponseSimple> responseExecutor) {
        this.responseExecutor = responseExecutor;
    }

    public void onClose(Consumer<Player> noResponseExecutor) {
        this.noResponseExecutor = noResponseExecutor;
    }

    public void showToPlayer(Player player) {
        AdvancedFormListener.showToPlayer(player, this);
    }
}
