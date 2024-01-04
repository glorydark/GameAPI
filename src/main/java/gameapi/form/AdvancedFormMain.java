package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.window.FormWindow;
import gameapi.annotation.Future;

import java.util.HashMap;
import java.util.LinkedHashMap;

@Future
public class AdvancedFormMain implements Listener {

    public static HashMap<Player, LinkedHashMap<Integer, FormWindow>> playerFormWindows = new HashMap<>();

    public static void showFormWindow(Player player, FormWindow window) {
        playerFormWindows.computeIfAbsent(player, i -> new LinkedHashMap<>()).put(player.showFormWindow(window), window);
    }

    protected void execute(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        if (playerFormWindows.containsKey(player)) {
            FormWindow window = playerFormWindows.getOrDefault(player, new LinkedHashMap<>()).get(event.getFormID());
            if (window != null) {
                if (window instanceof AdvancedForm) {
                    ((AdvancedForm) window).dealResponse(player, event.getResponse());
                }
                /*
                    FormResponseHandler is not advocated to use in the GameAPI,
                    but we still leave a supporting process here.
                */
                for (FormResponseHandler handler : window.getHandlers()) {
                    handler.handle(player, event.getFormID());
                }
            }
        }
    }

    @EventHandler
    public void PlayerFormRespondedEvent(PlayerFormRespondedEvent event) {
        this.execute(event);
    }

}
