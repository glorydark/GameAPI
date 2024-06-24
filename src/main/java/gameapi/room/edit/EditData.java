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

    private final List<EditStep> steps = new ArrayList<>();
    protected ConfigSection configCache;
    protected Player player = null;
    protected int index = 0;

    public EditData() {
        this.configCache = new ConfigSection();
    }

    public void onStart(Player player) {
        if (this.player != null && this.player.isOnline()) {
            player.sendMessage("There is already someone editing the same entry! Please wait...");
            return;
        }
        this.player = player;
        this.configCache = new ConfigSection();
        this.index = 0;
        this.nextStep();
    }

    public List<EditStep> getSteps() {
        return this.steps;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void addStep(EditStep... addSteps) {
        this.steps.addAll(Arrays.asList(addSteps));
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
            lastStep.onEnd();
        }
        this.index++;
        if (this.index >= this.steps.size()) {
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
        nextStep.onStart();
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
