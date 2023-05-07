package gameapi.block;

import gameapi.event.RoomEvent;

public interface AdvancedBlock {

    void trigger(RoomEvent event);

    String getId(); // "id:meta"

}
