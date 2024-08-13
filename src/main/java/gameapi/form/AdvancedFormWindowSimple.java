package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;
import gameapi.listener.AdvancedFormListener;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AdvancedFormWindowSimple extends FormWindowSimple implements AdvancedForm {

    protected BiConsumer<Player, FormResponseSimple> responseExecutor = null;

    protected Consumer<Player> noResponseExecutor = null;

    public AdvancedFormWindowSimple() {
        this("", "");
    }

    public AdvancedFormWindowSimple(String title) {
        this(title, "");
    }

    public AdvancedFormWindowSimple(String title, String content) {
        super(title, content);
    }

    public AdvancedFormWindowSimple title(Supplier<String> supplier) {
        return this.title(supplier.get());
    }

    public AdvancedFormWindowSimple title(String string) {
        this.setTitle(string);
        return this;
    }

    public AdvancedFormWindowSimple content(Supplier<String> supplier) {
        return this.content(supplier.get());
    }

    public AdvancedFormWindowSimple content(String string) {
        this.setContent(string);
        return this;
    }

    public void dealResponse(Player player, FormResponse response) {
        FormResponseSimple responseSimple = (FormResponseSimple) response;
        if (this.wasClosed() || response == null) {
            if (this.noResponseExecutor != null) {
                this.noResponseExecutor.accept(player);
            }
        } else {
            ElementButton clickedButton = ((FormResponseSimple) response).getClickedButton();
            if (clickedButton instanceof ResponsiveElementButton) {
                ResponsiveElementButton responsiveElementButton = (ResponsiveElementButton) clickedButton;
                Consumer<Player> onClickResponse = responsiveElementButton.getResponse();
                if (onClickResponse != null) {
                    onClickResponse.accept(player);
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
