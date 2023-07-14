package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.window.FormWindowModal;

import java.util.function.Consumer;

public class AdvancedFormWindowModal extends FormWindowModal implements AdvancedForm {

    protected Consumer<Player> firstButtonResponseExecutor;

    protected Consumer<Player> secondButtonResponseExecutor;

    protected Consumer<Player> noResponseExecutor;

    public AdvancedFormWindowModal() {
        super("", "", "", "");
    }

    public AdvancedFormWindowModal(String title, String content, String firstButtonText, String secondButtonText) {
        super(title, content, firstButtonText, secondButtonText);
    }


    public void dealResponse(Player player, FormResponse response){
        FormResponseModal responseModal = (FormResponseModal) response;
        if(this.wasClosed() || responseModal == null){
            noResponseExecutor.accept(player);
        }else{
            switch (responseModal.getClickedButtonId()){
                case 0:
                    firstButtonResponseExecutor.accept(player);
                    break;
                case 1:
                    secondButtonResponseExecutor.accept(player);
                    break;
            }
        }
    }

    public static class Builder {

        private String title;

        private String content;

        private Consumer<Player> firstButtonResponseExecutor;

        private Consumer<Player> secondButtonResponseExecutor;

        private Consumer<Player> noResponseExecutor;

        private String trueButtonText;

        private String falseButtonText;

        public Builder(){

        }

        public Builder setTitle(String title){
            this.title = title;
            return this;
        }

        public Builder setContent(String content){
            this.content = content;
            return this;
        }

        public Builder setTrueButtonText(String text){
            this.trueButtonText = text;
            return this;
        }

        public Builder setFalseButtonText(String text){
            this.falseButtonText = text;
            return this;
        }

        public Builder firstButtonResponseExecute(Consumer<Player> firstButtonResponseExecutor){
            this.firstButtonResponseExecutor = firstButtonResponseExecutor;
            return this;
        }

        public Builder secondButtonResponseExecute(Consumer<Player> secondButtonResponseExecutor){
            this.secondButtonResponseExecutor = secondButtonResponseExecutor;
            return this;
        }

        public Builder responseExecute(Consumer<Player> noResponseExecutor){
            this.noResponseExecutor = noResponseExecutor;
            return this;
        }

        public AdvancedFormWindowModal build(){
            AdvancedFormWindowModal modal = new AdvancedFormWindowModal();
            modal.setTitle(title);
            modal.setContent(content);
            modal.setButton1(trueButtonText);
            modal.setButton2(falseButtonText);
            modal.firstButtonResponseExecutor = this.firstButtonResponseExecutor;
            modal.secondButtonResponseExecutor = this.secondButtonResponseExecutor;
            modal.noResponseExecutor = this.noResponseExecutor;
            return modal;
        }

    }
}
