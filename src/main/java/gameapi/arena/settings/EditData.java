package gameapi.arena.settings;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import gameapi.annotation.Experimental;

import java.util.HashMap;

@Experimental
public class EditData {

    Player player;

    int currentStep;

    HashMap<Integer, EditStep> stepExecutors = new HashMap<>();

    public EditData(Player player){
        this.player = player;
        process();
    }

    protected void process(){
        start(currentStep);
        if(currentStep < stepExecutors.size()) {
            currentStep++;
        }else{
            end();
        }
    }

    public void start(int currentStep){
        EditStep step = stepExecutors.get(currentStep);
        if(step != null){
            step.getStartExecutor().accept(player);
        }
    }

    public void dealResponse(Event event){
        EditStep step = stepExecutors.get(currentStep);
        if(step != null){
            step.getResponseExecutor().accept(player, event);
        }
    }

    protected void end(){
        player.getInventory().clearAll();
        player.sendMessage("设置完成！");
    }

}
