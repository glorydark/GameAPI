package gameapi.room.team;

import cn.nukkit.Player;
import gameapi.GameAPI;
import gameapi.room.Room;
import gameapi.utils.AdvancedLocation;
import gameapi.utils.PlayerTools;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Setter
@Getter
public class BaseTeam {
    private String registryName;

    private int score = 0;

    private String prefix;

    private List<Player> players = new ArrayList<>();

    private Room room;

    private int spawnIndex;

    private int maxPlayer;

    private boolean isAlive = true;

    private HashMap<String, Object> properties = new HashMap<>();

    public BaseTeam(Room room, String registryName, String prefix, int maxPlayer, int spawnIndex){
        this.room = room;
        this.registryName = registryName;
        this.prefix = prefix;
        this.spawnIndex = spawnIndex;
        this.maxPlayer = maxPlayer;
    }

    public void addPlayer(Player player){
        if(isAvailable()) {
            players.add(player);
        }else{
            player.sendMessage(GameAPI.getLanguage().getTranslation("room.team.full"));
        }
    }

    public int getSize(){
        return players.size();
    }

    public boolean isAvailable(){
        return players.size() < maxPlayer;
    }

    public void removePlayer(Player player){
        players.remove(player);
    }

    public boolean hasPlayer(Player player){
        return players.contains(player);
    }

    public void resetAll(){
        this.players.clear();
        this.isAlive = true;
    }

    public void teleportToSpawn(){
        if(room.getStartSpawn().size() == 0){ return; }
        if(room.getStartSpawn().size() < spawnIndex + 1){ return; }
        AdvancedLocation location = room.getStartSpawn().get(spawnIndex);
        for(Player player: players){
            location.teleport(player);
        }
    }

    public void sendMessageToAll(String string){
        PlayerTools.sendMessage(players, string);
    }

    public void sendActionbarToAll(String string){
        PlayerTools.sendActionbar(players, string);
    }

    public void sendTitleToAll(String string){
        PlayerTools.sendTitle(players, string);
    }

    public void sendTipToAll(String string){
        PlayerTools.sendTip(players, string);
    }

    @Override
    public String toString() {
        return "BaseTeam{" +
                "registryName='" + registryName + '\'' +
                ", score=" + score +
                ", prefix='" + prefix + '\'' +
                ", players=" + players +
                ", room=" + room +
                ", spawnIndex=" + spawnIndex +
                ", maxPlayer=" + maxPlayer +
                ", isAlive=" + isAlive +
                ", properties=" + properties +
                '}';
    }
}
