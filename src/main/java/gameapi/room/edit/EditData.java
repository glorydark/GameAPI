package gameapi.room.edit;

import cn.nukkit.Player;
import cn.nukkit.utils.ConfigSection;
import gameapi.annotation.Future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author glorydark
 */
@Future
public class EditData {

    protected ConfigSection configCache;

    protected Player player = null;

    protected int index = 0;

    private final List<EditStep> steps = new ArrayList<>();

    public EditData() {
        configCache = new ConfigSection();
    }

    public void onStart(Player player) {
        if (this.player != null && this.player.isOnline()) {
            player.sendMessage("There is already someone editing the same entry! Please wait...");
            return;
        }
        this.player = player;
        configCache = new ConfigSection();
        index = 0;
        this.nextStep();
    }

    public List<EditStep> getSteps() {
        return steps;
    }

    public Player getPlayer() {
        return player;
    }

    public void addStep(EditStep... addSteps) {
        steps.addAll(Arrays.asList(addSteps));
    }

    public EditStep getCurrentStep() {
        if (this.index > 0 && this.index < this.steps.size()) {
            return this.steps.get(this.index - 1);
        } else {
            return null;
        }
    }

    public void nextStep() {
        if (this.index > 0 && this.index < this.steps.size()) {
            EditStep lastStep = getCurrentStep();
            lastStep.onEnd(this);
        }
        this.index++;
        if (index >= this.steps.size()) {
            this.onEnd();
            return;
        }
        EditStep nextStep = getCurrentStep();
        if (nextStep == null) {
            this.onError();
            this.player = null;
            this.configCache = new ConfigSection();
            this.index = 0;
            return;
        }
        nextStep.onStart(this);
    }

    public void onEnd() {

    }

    public void onError() {

    }

    public void onQuit() {

    }

    public ConfigSection getConfigCache() {
        return configCache;
    }
}
