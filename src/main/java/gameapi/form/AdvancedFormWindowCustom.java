package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.Element;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindowCustom;
import gameapi.annotation.Future;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Future
public class AdvancedFormWindowCustom extends FormWindowCustom implements AdvancedForm{

    protected BiConsumer<Player, FormResponseCustom> responseExecutor;

    protected Consumer<Player> noResponseExecutor;

    protected List<Element> elements;

    public AdvancedFormWindowCustom(String title) {
        super(title);
    }

    public AdvancedFormWindowCustom() {
        super("");
    }


    public void dealResponse(Player player, FormResponse response){
        if(this.wasClosed() || response == null){
            noResponseExecutor.accept(player);
        }else{
            responseExecutor.accept(player, (FormResponseCustom) response);
        }
    }

    public static class Builder {

        private String title;

        private BiConsumer<Player, FormResponseCustom> responseExecutor;

        private Consumer<Player> noResponseExecutor;

        protected List<Element> elements = new ArrayList<>();

        public Builder(){

        }

        public Builder setTitle(String title){
            this.title = title;
            return this;
        }

        public Builder onResponse(BiConsumer<Player, FormResponseCustom> responseExecutor){
            this.responseExecutor = responseExecutor;
            return this;
        }

        public Builder onClose(Consumer<Player> noResponseExecutor){
            this.noResponseExecutor = noResponseExecutor;
            return this;
        }

        public Builder addElement(Element element){
            this.elements.add(element);
            return this;
        }

        public AdvancedFormWindowCustom build(){
            AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom();
            custom.setTitle(title);
            custom.elements = elements;
            custom.responseExecutor = this.responseExecutor;
            custom.noResponseExecutor = this.noResponseExecutor;
            return custom;
        }

    }
}