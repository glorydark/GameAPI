package gameapi.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.Config;
import com.google.gson.Gson;
import gameapi.GameAPI;
import gameapi.entity.EntityTools;
import gameapi.inventory.InventoryTools;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.sound.SoundTools;
import gameapi.utils.SmartTools;

import java.io.File;
import java.util.*;

/**
 * @author Glorydark
 * For in-game test
 */
public class BaseCommands extends Command {
    public BaseCommands(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.isOp()) {
            return false;
        }
        if (strings.length > 0) {
            switch (strings[0].toLowerCase()) {
                case "playressound":
                    if (strings.length > 2) {
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        if (player != null) {
                            SoundTools.playResourcePackOggMusic(player, strings[2]);
                        } else {
                            GameAPI.plugin.getLogger().warning("Can not find the chosen player!");
                        }
                    }
                    break;
                case "playambientsound":
                    if (strings.length > 2) {
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        if (player != null) {
                            Sound sound = Sound.valueOf(strings[2]);
                            if (sound.getSound() != null) {
                                SoundTools.addAmbientSound(player.level, player, sound);
                            } else {
                                GameAPI.plugin.getLogger().warning("Can not find the chosen sound!");
                            }
                        } else {
                            GameAPI.plugin.getLogger().warning("Can not find the chosen player!");
                        }
                    }
                    break;
                case "debug":
                    if (strings.length != 2) {
                        return false;
                    }
                    if(commandSender.isPlayer()){
                        switch (strings[1]) {
                            case "true":
                                GameAPI.debug.add((Player) commandSender);
                                commandSender.sendMessage("已开启debug模式！");
                                break;
                            case "false":
                                GameAPI.debug.remove((Player) commandSender);
                                commandSender.sendMessage("已关闭debug模式！");
                        }
                    }else{
                        commandSender.sendMessage("请在游戏内执行！");
                    }
                    break;
                case "savebattles": // For Tournament Restart Procedures
                    File saveDic = new File(GameAPI.path+"/saves/"+ SmartTools.dateToString(Calendar.getInstance().getTime()) + "/");
                    if(saveDic.exists() || saveDic.mkdirs()) {
                        for (String key : GameAPI.RoomHashMap.keySet()) {
                            for (Room room : GameAPI.RoomHashMap.get(key)) {
                                if (room.getRoomStatus().equals(RoomStatus.ROOM_STATUS_GameStart)) {
                                    File file = new File(saveDic.getPath() + "/" + key + "_" + room.getRoomName() + ".json");
                                    Config config = new Config(file, Config.JSON);
                                    LinkedHashMap<String, Object> players = new LinkedHashMap<>();
                                    room.getPlayers().forEach(player -> players.put(player.getName(), getPlayerDatas(player)));
                                    LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();
                                    objectMap.put("players", players);
                                    objectMap.put("players_properties", getPropertiesDatas(room.getPlayerProperties()));
                                    objectMap.put("room_properties", room.getRoomProperties());
                                    objectMap.put("room_datas", new Gson().fromJson(room.toString(), Map.class));
                                    config.setAll(objectMap);
                                    config.save();
                                }
                            }
                        }
                    }else{
                        GameAPI.plugin.getLogger().warning("无法创建文件夹："+saveDic.getPath());
                    }
                    break;
                case "addrank":
                    if(commandSender.isPlayer()) {
                        if (strings.length == 3) {
                            Player player = (Player) commandSender;
                            EntityTools.addRankingList(player, strings[1], strings[2]);
                        }
                    }else{
                        commandSender.sendMessage("请在游戏内执行！");
                    }
                    break;
                case "stoproom":
                    if(strings.length == 3){
                        Room room = Room.getRoom(strings[1], strings[2]);
                        if(room != null){
                            for (Player player : room.getPlayers()) {
                                player.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation().getLocation(), null);
                                player.sendMessage("该对局已被强行停止！");
                                GameAPI.playerRoomHashMap.remove(player);
                            }
                            room.setRoomStatus(RoomStatus.ROOM_STOPPED);
                            commandSender.sendMessage("Room Stopped!");
                        }else{
                            commandSender.sendMessage("Room Not Found!");
                        }
                    }
                    break;
                case "halt":
                    if(strings.length == 3){
                        Room room = Room.getRoom(strings[1], strings[2]);
                        if(room != null){
                            if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){
                                commandSender.sendMessage("The game of this room is not processing!");
                                return true;
                            }
                            for (Player player : room.getPlayers()) {
                                player.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation().getLocation(), null);
                                player.sendMessage("该对局已被强行暂停！");
                            }
                            room.setRoomStatus(RoomStatus.ROOM_HALTED);
                            commandSender.sendMessage("Room Halted!");
                        }else{
                            commandSender.sendMessage("Room Not Found!");
                        }
                    }
                    break;
                case "restart":
                    if(strings.length == 3){
                        Room room = Room.getRoom(strings[1], strings[2]);
                        if(room != null){
                            for (Player player : room.getPlayers()) {
                                if(player.isOnline()){
                                    player.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation().getLocation(), null);
                                    player.sendMessage("该对局已重新开始！");
                                }else{
                                    commandSender.sendMessage("玩家未在线，玩家名："+player.getName());
                                }
                            }
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameStart);
                            commandSender.sendMessage("Room Restarted!");
                        }else{
                            commandSender.sendMessage("Room Not Found!");
                        }
                    }
                    break;
                case "status":
                    commandSender.sendMessage("获取游戏房间状态ing...");
                    List<String> games = new ArrayList<>(GameAPI.RoomHashMap.keySet());
                    if(games.size() > 0){
                        for(String game: games){
                            commandSender.sendMessage("游戏名称【"+ game+"】房间信息：");
                            List<Room> rooms = GameAPI.RoomHashMap.get(game);
                            if(rooms.size() > 0){
                                for(Room room: rooms){
                                    if(room.getRoomRule().needPreStartPass){
                                        commandSender.sendMessage(room.getRoomName()+": "+room.getRoomStatus().toString()+"【"+room.getPlayers().size()+"/"+room.getMinPlayer()+"】 - 需要管理员手动开始比赛");
                                    }else{
                                        commandSender.sendMessage(room.getRoomName()+": "+room.getRoomStatus().toString()+"【"+room.getPlayers().size()+"/"+room.getMinPlayer()+"】");
                                    }
                                }
                            }else{
                                commandSender.sendMessage("§c该游戏无房间！");
                            }
                        }
                    }else{
                        commandSender.sendMessage("§cNo Loaded Games Existed");
                    }
                    break;
                case "unloadworld":
                    commandSender.sendMessage("已加载世界：");
                    Level level = Server.getInstance().getLevelByName(strings[1]);
                    level.unload(true);
                    break;
            }
        }
        return true;
    }

    public LinkedHashMap<String, Object> getPlayerDatas(Player player){
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        maps.put("Location", player.getX() + ":" + player.getY() + ":" + player.getZ()+ ":" + player.getLevel().getName());
        maps.put("Health", player.getHealth());
        List<String> invs = new ArrayList<>();
        player.getInventory().getContents().values().forEach(item -> invs.add(InventoryTools.getItemString(item)));
        maps.put("Inventory", invs);
        List<String> armors = new ArrayList<>();
        for(Item item: player.getInventory().getArmorContents()){
            armors.add(InventoryTools.getItemString(item));
        }
        maps.put("ArmorContents", armors);
        return maps;
    }

    public LinkedHashMap<String, Object> getPropertiesDatas(LinkedHashMap<String, LinkedHashMap<String, Object>> playerObjectMap){
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        for(String key: playerObjectMap.keySet()){
            maps.put(key, playerObjectMap.get(key));
        }
        return maps;
    }
}
