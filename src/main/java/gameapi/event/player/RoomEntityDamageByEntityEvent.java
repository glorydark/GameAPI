package gameapi.event.player;

import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import gameapi.event.Cancellable;
import gameapi.room.Room;

public class RoomEntityDamageByEntityEvent extends RoomPlayerEvent implements Cancellable {

    protected EntityDamageByEntityEvent event;

    public RoomEntityDamageByEntityEvent(Room room, EntityDamageByEntityEvent event){
        this.room = room;
        this.event = event;
    }

    public EntityDamageByEntityEvent getEvent() {
        return event;
    }
}
