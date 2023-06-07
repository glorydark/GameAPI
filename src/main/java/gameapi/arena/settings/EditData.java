package gameapi.arena.settings;

import cn.nukkit.Player;
import gameapi.annotation.Future;

@Future
public class EditData {

    Player player;

    int currentStep = 1; // Start from number 1

    int maxStep;

    public EditData(Player player){
        this.player = player;
    }

    protected void start(){
        if(player != null) {
            process();
        }
    }

    protected void process(){
        execute(currentStep);
        if(currentStep < maxStep) {
            currentStep++;
        }else{
            end();
        }
    }

    public void execute(int step){

    }

    public void end(){

    }

}
