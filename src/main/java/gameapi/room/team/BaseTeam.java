package gameapi.room.team;

import cn.nukkit.Player;
import cn.nukkit.utils.DyeColor;
import gameapi.GameAPI;
import gameapi.room.Room;
import gameapi.tools.PlayerTools;
import gameapi.utils.AdvancedLocation;
import lombok.Data;
import lombok.ToString;

import java.util.*;

@Data
@ToString
public class BaseTeam {
    protected Map<Player, Map<String, Object>> playerProperties = new LinkedHashMap<>();
    private String registryName;
    private int score = 0;
    private String prefix;
    private List<Player> players = new ArrayList<>();
    private Room room;
    private List<Integer> spawnIndexList;
    private int maxPlayer;
    private boolean alive = true;
    private boolean eliminated = false;
    private DyeColor dyeColor = DyeColor.WHITE;
    private Map<String, Object> properties = new LinkedHashMap<>();

    public BaseTeam(Room room, String registryName, String prefix, int maxPlayer, int spawnIndex) {
        this(room, registryName, prefix, maxPlayer, Collections.singletonList(spawnIndex));
    }

    public BaseTeam(Room room, String registryName, String prefix, int maxPlayer, List<Integer> spawnIndexList) {
        this.room = room;
        this.registryName = registryName;
        this.prefix = prefix;
        this.spawnIndexList = spawnIndexList;
        this.maxPlayer = maxPlayer;
    }

    public boolean addPlayer(Player player) {
        return this.addPlayer(player, true);
    }

    public boolean addPlayer(Player player, boolean tips) {
        if (this.isAvailable()) {
            this.players.add(player);
            return true;
        } else {
            if (tips) {
                player.sendMessage(GameAPI.getLanguage().getTranslation("room.team.full"));
            }
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
        this.alive = true;
    }

    public void teleportToSpawn() {
        this.teleportToSpawn(this.getPlayers().toArray(new Player[0]));
    }

    public void teleportToSpawn(Player... players) {
        if (this.room.getStartSpawn().isEmpty()) {
            return;
        }
        int spawnIndexSize = this.spawnIndexList.size();
        if (spawnIndexSize > 0) {
            if (spawnIndexSize == 1) {
                int spawnIndex = this.spawnIndexList.get(0);
                if (spawnIndex >= this.room.getStartSpawn().size()) {
                    GameAPI.getGameDebugManager().warning("Find spawn index bigger than the room has, game name: " + room.getGameName() + ", room name: " + room.getRoomName());
                    return;
                }
                AdvancedLocation location = this.room.getStartSpawn().get(spawnIndex);
                for (Player player : players) {
                    location.teleport(player);
                }
            } else {
                for (Player player : players) {
                    int playerIndex = this.players.indexOf(player);
                    int spawnIndex;
                    if (playerIndex >= this.spawnIndexList.size()) {
                        spawnIndex = this.spawnIndexList.get(0);
                        GameAPI.getGameDebugManager().warning("Find spawns are not satisfied with real performance, game name: " + room.getGameName() + ", room name: " + room.getRoomName());
                    } else {
                        spawnIndex = this.spawnIndexList.get(playerIndex);
                    }
                    if (spawnIndex >= this.room.getStartSpawn().size()) {
                        GameAPI.getGameDebugManager().warning("Find spawn index bigger than the room has, game name: " + room.getGameName() + ", room name: " + room.getRoomName());
                        return;
                    }
                    AdvancedLocation location = this.room.getStartSpawn().get(spawnIndex);
                    location.teleport(player);
                }
            }
        }
    }

    public void sendMessageToAll(String string) {
        PlayerTools.sendMessage(this.players, string);
        GameAPI.getInstance().getLogger().info(string);
    }

    public void sendActionbarToAll(String string) {
        PlayerTools.sendActionbar(players, string);
    }

    public void sendTitleToAll(String title) {
        PlayerTools.sendTitle(players, title);
    }

    public void sendTitleToAll(String title, String subtitle) {
        PlayerTools.sendTitle(players, title, subtitle);
    }

    public void sendTipToAll(String string) {
        PlayerTools.sendTip(players, string);
    }

    public <T> T getTeamProperty(String key, T value) {
        return (T) this.getProperties().getOrDefault(key, value);
    }

    public void setTeamProperty(String key, Object value) {
        this.getProperties().put(key, value);
    }

    public <T> T getPlayerProperty(Player player, String key, T value) {
        return (T) this.getPlayerProperties().getOrDefault(player, new LinkedHashMap<>()).getOrDefault(key, value);
    }

    public void setPlayerProperty(Player player, String key, Object value) {
        this.getPlayerProperties().computeIfAbsent(player, player1 -> new LinkedHashMap<>()).put(key, value);
    }
}
