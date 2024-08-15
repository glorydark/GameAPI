package gameapi.room.team;

import cn.nukkit.Player;
import gameapi.GameAPI;
import gameapi.room.Room;
import gameapi.tools.PlayerTools;
import gameapi.utils.AdvancedLocation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, Object> properties = new LinkedHashMap<>();

    public BaseTeam(Room room, String registryName, String prefix, int maxPlayer, int spawnIndex) {
        this.room = room;
        this.registryName = registryName;
        this.prefix = prefix;
        this.spawnIndex = spawnIndex;
        this.maxPlayer = maxPlayer;
    }

    public boolean addPlayer(Player player) {
        if (this.isAvailable()) {
            this.players.add(player);
            return true;
        } else {
            player.sendMessage(GameAPI.getLanguage().getTranslation("room.team.full"));
            return false;
        }
    }

    public int getSize() {
        return this.players.size();
    }

    public boolean isAvailable() {
        return this.players.size() < this.maxPlayer;
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public boolean hasPlayer(Player player) {
        return this.players.contains(player);
    }

    public void resetAll() {
        this.players.clear();
        this.isAlive = true;
    }

    public void teleportToSpawn() {
        this.teleportToSpawn(this.getPlayers().toArray(new Player[0]));
    }

    public void teleportToSpawn(Player... players) {
        if (this.room.getStartSpawn().size() == 0) {
            return;
        }
        if (this.spawnIndex >= this.room.getStartSpawn().size()) {
            return;
        }
        AdvancedLocation location = this.room.getStartSpawn().get(this.spawnIndex);
        for (Player player : players) {
            location.teleport(player);
        }
    }

    public void sendMessageToAll(String string) {
        PlayerTools.sendMessage(this.players, string);
        GameAPI.getInstance().getLogger().info(string);
    }

    public void sendActionbarToAll(String string) {
        PlayerTools.sendActionbar(players, string);
    }

    public void sendTitleToAll(String string) {
        PlayerTools.sendTitle(players, string);
    }

    public void sendTipToAll(String string) {
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
