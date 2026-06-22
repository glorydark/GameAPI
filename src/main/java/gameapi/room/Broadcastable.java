package gameapi.room;

import cn.nukkit.Player;
import gameapi.commands.defaults.dev.HideChatCommand;
import gameapi.tools.PlayerTools;
import gameapi.utils.TitleData;
import gameapi.utils.text.GameTextContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Broadcastable {

    List<Player> getPlayers();

    default List<Player> getSpectators() {
        return Collections.emptyList();
    }

    default List<Player> getAllPlayers() {
        List<Player> all = new ArrayList<>(this.getPlayers());
        all.addAll(this.getSpectators());
        return all;
    }

    default void sendMessageToAll(String string) {
        this.sendMessageToAll(string, true);
    }

    default void sendMessageToAll(String string, boolean includeSpectators) {
        PlayerTools.sendMessage(this.getPlayers(), string);
        if (includeSpectators) {
            PlayerTools.sendMessage(this.getSpectators(), string);
        }
    }

    default void sendMessageToAll(GameTextContainer text) {
        this.sendMessageToAll(text, true);
    }

    default void sendMessageToAll(GameTextContainer text, boolean includeSpectators) {
        for (Player player : this.getPlayers()) {
            if (HideChatCommand.hideMessagePlayers.contains(player)) {
                continue;
            }
            player.sendMessage(text.getText(player));
        }
        if (includeSpectators) {
            for (Player spectator : this.getSpectators()) {
                if (HideChatCommand.hideMessagePlayers.contains(spectator)) {
                    continue;
                }
                spectator.sendMessage(text.getText(spectator));
            }
        }
    }

    default void sendActionbarToAll(String string) {
        this.sendActionbarToAll(string, true);
    }

    default void sendActionbarToAll(String string, boolean includeSpectators) {
        PlayerTools.sendActionbar(this.getPlayers(), string);
        if (includeSpectators) {
            PlayerTools.sendActionbar(this.getSpectators(), string);
        }
    }

    default void sendActionbarToAll(GameTextContainer text) {
        this.sendActionbarToAll(text, true);
    }

    default void sendActionbarToAll(GameTextContainer text, boolean includeSpectators) {
        for (Player player : this.getPlayers()) {
            player.sendActionBar(text.getText(player));
        }
        if (includeSpectators) {
            for (Player spectator : this.getSpectators()) {
                spectator.sendActionBar(text.getText(spectator));
            }
        }
    }

    default void sendTitleToAll(String string) {
        this.sendTitleToAll(string, "", true);
    }

    default void sendTitleToAll(String string, String subtitle) {
        this.sendTitleToAll(string, subtitle, true);
    }

    default void sendTitleToAll(String string, String subtitle, boolean includeSpectators) {
        PlayerTools.sendTitle(this.getPlayers(), string, subtitle);
        if (includeSpectators) {
            PlayerTools.sendTitle(this.getSpectators(), string, subtitle);
        }
    }

    default void sendTitleToAll(TitleData titleData) {
        this.sendTitleToAll(titleData, true);
    }

    default void sendTitleToAll(TitleData titleData, boolean includeSpectators) {
        PlayerTools.sendTitle(this.getPlayers(), titleData);
        if (includeSpectators) {
            PlayerTools.sendTitle(this.getSpectators(), titleData);
        }
    }

    default void sendTipToAll(String string) {
        this.sendTipToAll(string, true);
    }

    default void sendTipToAll(String string, boolean includeSpectators) {
        PlayerTools.sendTip(this.getPlayers(), string);
        if (includeSpectators) {
            PlayerTools.sendTip(this.getSpectators(), string);
        }
    }

    default void sendTipToAll(GameTextContainer text) {
        this.sendTipToAll(text, true);
    }

    default void sendTipToAll(GameTextContainer text, boolean includeSpectators) {
        for (Player player : this.getPlayers()) {
            player.sendTip(text.getText(player));
        }
        if (includeSpectators) {
            for (Player spectator : this.getSpectators()) {
                spectator.sendTip(text.getText(spectator));
            }
        }
    }
}
