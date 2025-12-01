package gameapi.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import com.google.gson.Gson;
import gameapi.GameAPI;
import gameapi.manager.RoomManager;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.room.status.factory.RoomDefaultStatusFactory;
import gameapi.tools.BlockTools;
import gameapi.tools.ItemTools;
import gameapi.tools.SmartTools;

import java.io.File;
import java.util.*;

/**
 * @author Glorydark
 * For in-game test
 */
@Deprecated
public class BaseCommand extends Command {

    public BaseCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length > 0) {
            Player p = commandSender.asPlayer();
            switch (strings[0].toLowerCase()) {
                case "savebattles":
                    if (commandSender.isOp()) {
                        // todo: For Tournament Restart Procedures
                        File saveDic = new File(GameAPI.getPath() + File.separator + "saves" + File.separator + SmartTools.dateToString(Calendar.getInstance().getTime(), "yyyyMMdd_HHmmss") + "/");
                        if (saveDic.exists() || saveDic.mkdirs()) {
                            for (Map.Entry<String, List<Room>> entry : RoomManager.getLoadedRooms().entrySet()) {
                                for (Room room : entry.getValue()) {
                                    if (room.getCurrentRoomStatus().equals(RoomStatus.ROOM_STATUS_START)) {
                                        File file = new File(saveDic.getPath() + File.separator + entry.getKey() + "_" + room.getRoomName() + ".json");
                                        Config config = new Config(file, Config.JSON);
                                        LinkedHashMap<String, Object> players = new LinkedHashMap<>();
                                        room.getPlayers().forEach(player -> players.put(player.getName(), getPlayerData(player)));
                                        LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();
                                        objectMap.put("players", players);
                                        objectMap.put("players_properties", getPropertiesData(room.getPlayerProperties()));
                                        objectMap.put("room_properties", room.getRoomProperties());
                                        objectMap.put("room_datas", new Gson().fromJson(room.toString(), Map.class));
                                        config.setAll(objectMap);
                                        config.save();
                                    }
                                }
                            }
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.save_battle.folder_created_failed", saveDic.getPath()));
                        }
                    }
                    break;
                case "stoproom": // todo
                    if (commandSender.isOp() && strings.length == 3) {
                        Room room = RoomManager.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            for (Player player : room.getPlayers()) {
                                player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                player.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.stop"));
                                RoomManager.getPlayerRoomHashMap().remove(player);
                            }
                            room.setCurrentRoomStatus(RoomDefaultStatusFactory.ROOM_STOPPED, "internal");
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.stop"));
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room_not_found"));
                        }
                    }
                    break;
                case "halt": // todo
                    if (commandSender.isOp() && strings.length == 3) {
                        Room room = RoomManager.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            if (room.getCurrentRoomStatus() != RoomDefaultStatusFactory.ROOM_STATUS_GAME_START) {
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room.not_start_yet"));
                                return true;
                            }
                            for (Player player : room.getPlayers()) {
                                player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                player.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.halt"));
                            }
                            room.setCurrentRoomStatus(RoomDefaultStatusFactory.ROOM_HALTED, "internal");
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.halt"));
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room_not_found"));
                        }
                    }
                    break;
                case "restart": // todo
                    if (commandSender.isOp() && strings.length == 3) {
                        Room room = RoomManager.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            for (Player player : room.getPlayers()) {
                                if (player.isOnline()) {
                                    player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                    player.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.restart"));
                                } else {
                                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.player_offline", player.getName()));
                                }
                            }
                            room.setCurrentRoomStatus(RoomDefaultStatusFactory.ROOM_STATUS_GAME_START, "internal");
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.restart"));
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room_not_found"));
                        }
                    }
                    break;
                case "breakblocks":
                    if (commandSender.isOp() && strings.length == 8) {
                        Level level = Server.getInstance().getLevelByName(strings[7]);
                        BlockTools.destroyAreaBlocks(new SimpleAxisAlignedBB(
                                new Vector3(
                                        Integer.parseInt(strings[1]),
                                        Integer.parseInt(strings[2]),
                                        Integer.parseInt(strings[3])
                                ),
                                new Vector3(
                                        Integer.parseInt(strings[4]),
                                        Integer.parseInt(strings[5]),
                                        Integer.parseInt(strings[6])
                                )
                        ), level, true);
                    }
                    break;
            }
        }
        return false;
    }

    public LinkedHashMap<String, Object> getPlayerData(Player player) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        maps.put("Location", player.getX() + ":" + player.getY() + ":" + player.getZ() + ":" + player.getLevel().getName());
        maps.put("Health", player.getHealth());
        List<String> invs = new ArrayList<>();
        player.getInventory().getContents().values().forEach(item -> invs.add(ItemTools.toString(item)));
        maps.put("Inventory", invs);
        List<String> armors = new ArrayList<>();
        for (Item item : player.getInventory().getArmorContents()) {
            armors.add(ItemTools.toString(item));
        }
        maps.put("ArmorContents", armors);
        return maps;
    }

    public LinkedHashMap<String, Object> getPropertiesData(LinkedHashMap<String, LinkedHashMap<String, Object>> playerObjectMap) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        for (String key : playerObjectMap.keySet()) {
            maps.put(key, playerObjectMap.get(key));
        }
        return maps;
    }
}
