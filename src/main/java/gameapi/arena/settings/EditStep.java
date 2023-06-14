package gameapi.arena.settings;

import cn.nukkit.Player;
import cn.nukkit.event.Event;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EditStep {

    private BiConsumer<Player, Event> responseExecutor;

    private Consumer<Player> startExecutor;

    public Consumer<Player> getStartExecutor() {
        return startExecutor;
    }

    public void setStartExecutor(Consumer<Player> startExecutor) {
        this.startExecutor = startExecutor;
    }

    public BiConsumer<Player, Event> getResponseExecutor() {
        return responseExecutor;
    }

    public void setResponseExecutor(BiConsumer<Player, Event> responseExecutor) {
        this.responseExecutor = responseExecutor;
    }

    public static class Builder{
        private BiConsumer<Player, Event> responseExecutor;

        private Consumer<Player> startExecutor;

        public Builder(){

        }

        public Builder responseExecute(BiConsumer<Player, Event> responseExecutor){
            this.responseExecutor = responseExecutor;
            return this;
        }

        public Builder startExecute(Consumer<Player> startExecutor){
            this.startExecutor = startExecutor;
            return this;
        }

        public EditStep build(){
            EditStep editStep = new EditStep();
            editStep.setResponseExecutor(this.responseExecutor);
            editStep.setStartExecutor(this.startExecutor);
            return editStep;
        }

    }

}
