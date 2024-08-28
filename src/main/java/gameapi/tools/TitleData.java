package gameapi.tools;

import gameapi.utils.text.GameTextContainer;
import gameapi.utils.text.GameTranslationContainer;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class TitleData {

    private int fadeIn;

    private int duration;

    private int fadeOut;

    private GameTextContainer title;

    private GameTextContainer subtitle;

    public TitleData() {
        this.fadeIn = 10;
        this.duration = 20;
        this.fadeOut = 10;
        this.title = new GameTranslationContainer();
        this.subtitle = new GameTranslationContainer();
    }

    public TitleData fadeIn(int tick) {
        this.fadeIn = tick;
        return this;
    }

    public TitleData duration(int tick) {
        this.duration = tick;
        return this;
    }

    public TitleData fadeOut(int tick) {
        this.fadeOut = tick;
        return this;
    }

    public TitleData title(GameTextContainer gameTranslationContainer) {
        this.title = gameTranslationContainer;
        return this;
    }

    public TitleData subtitle(GameTextContainer gameTranslationContainer) {
        this.subtitle = gameTranslationContainer;
        return this;
    }
}
