package gameapi.arena.settings;

import cn.nukkit.Player;

public class EditManager {

    Player player;

    int currentStep = 1; // Start from number 1

    int maxStep;

    public EditManager(Player player){
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
