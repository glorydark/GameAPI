package gameapi.room.edit;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import cn.nukkit.utils.ConfigSection;
import gameapi.GameAPI;
import gameapi.entity.TextEntity;

import java.util.*;

/**
 * @author glorydark
 */
public abstract class EditProcess {

    protected List<TextEntity> textEntities = new ArrayList<>();
    protected List<EditStep> steps = new ArrayList<>();
    protected ConfigSection configCache;
    protected Map<String, Object> properties = new LinkedHashMap<>();
    protected Player player = null;
    protected int currentStep = 0;
    protected static final int INDEX_OFFSET = 1;

    public EditProcess() {
        this.configCache = new ConfigSection();
    }

    public void begin(Player player) {
        if (this.player != null && this.player.isOnline()) {
            player.sendMessage("There is already someone editing the same entry! Please wait...");
            return;
        }
        this.player = player;
        GameAPI.editProcessList.add(this);
        this.onStart();
        this.player = player;
        this.configCache = new ConfigSection();
        this.currentStep = 0;
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
        if (this.currentStep <= this.getSteps().size()) {
            return this.getSteps().get(this.currentStep - 1);
        } else {
            return null;
        }
    }

    public EditStep getNextStep() {
        if (this.currentStep + 1 <= this.getSteps().size()) {
            return this.getSteps().get(this.currentStep);
        } else {
            return null;
        }
    }

    public EditStep getPrevStep() {
        if (this.currentStep - 1 > 0) {
            return this.getSteps().get(this.currentStep - 1 - INDEX_OFFSET);
        } else {
            return null;
        }
    }

    public void prevStep() {
        EditStep prevStep = this.getPrevStep();
        if (prevStep != null) {
            this.currentStep--;
            prevStep.onStart(this.player);
        } else {
            this.getPlayer().sendMessage("无法进行上一步！");
        }
    }

    public void nextStep() {
        EditStep nextStep = this.getNextStep();
        if (nextStep != null) {
            this.currentStep++;
            nextStep.onStart(this.player);
        } else {
            this.onEnd();
            GameAPI.editProcessList.remove(this);
        }
    }

    public void onStart() {

    }

    public void onEnd() {

    }

    public void onError() {

    }

    public void onQuit() {

    }

    public void onTick() {

    }

    public ConfigSection getConfigCache() {
        return configCache;
    }

    public void addTextEntity(Position position, String text) {
        position = position.floor();
        TextEntity textEntity = new TextEntity(position.getChunk(), position, text, Entity.getDefaultNBT(position));
        textEntity.spawnToAll();
        this.textEntities.add(textEntity);
    }

    public void removeTextEntity(Position position) {
        position = position.floor();
        Position finalPosition = position;
        new ArrayList<>(this.textEntities).stream()
                .filter(textEntity -> textEntity.distance(finalPosition) < 0.1d)
                .forEach(textEntity -> {
                    textEntity.close();
                    this.textEntities.remove(textEntity);
                });
    }

    public void clearAllTextEntities() {
        for (TextEntity textEntity : this.textEntities) {
            textEntity.close();
        }
        this.textEntities = new ArrayList<>();
    }

    public <T> T getProperty(String key, T defaultValue) {
        return (T) properties.getOrDefault(key, defaultValue);
    }

    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
