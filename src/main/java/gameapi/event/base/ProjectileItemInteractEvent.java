package gameapi.event.base;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import cn.nukkit.item.Item;

/**
 * @author glorydark
 */
public class ProjectileItemInteractEvent extends PlayerEvent {

    private final Item item;

    private static final HandlerList handlers = new HandlerList();

    public ProjectileItemInteractEvent(Player player, Item item) {
        this.player = player;
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
