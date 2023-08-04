package gameapi.room.executor;

import cn.nukkit.Player;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.fireworkapi.CreateFireworkApi;
import gameapi.room.Room;
import gameapi.utils.AdvancedLocation;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BaseRoomExecutor implements RoomExecutor {

    protected Room room;

    public BaseRoomExecutor(Room room){
        this.room = room;
    }
    
    @Override
    public void onWait() {
        if (room.getPlayers().size() >= room.getMinPlayer()) {
            if(room.getRoomRule().isNeedPreStartPass() && !room.isPreStartPass()){
                for(Player player : room.getPlayers()){
                    player.sendActionBar(GameAPI.getLanguage().getTranslation(player, "room.actionbar.wait.needStartPass"));
                }
            }
        }else{
            for(Player player : room.getPlayers()){
                player.sendActionBar(GameAPI.getLanguage().getTranslation("room.actionbar.wait.waitForPlayers", room.getPlayers().size(), room.getMinPlayer(), room.getMinPlayer() - room.getPlayers().size()));
            }
        }
    }

    @Override
    public void onPreStart() {
        for(Player player : room.getPlayers()){
            player.sendTitle(TextFormat.LIGHT_PURPLE+String.valueOf(room.getWaitTime() - room.getTime()), GameAPI.getLanguage().getTranslation(player, "room.title.preStart.subtitle"));
        }
    }

    @Override
    public void onReadyStart() {
        for (Player p : room.getPlayers()) {
            int lastSec = room.getGameWaitTime() - room.getTime();
            if(lastSec > 10) {
                p.getLevel().addSound(p.getPosition(), Sound.NOTE_HARP);
                p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart", room.getGameWaitTime() - room.getTime()));
            }else{
                if(lastSec == 1){
                    p.getLevel().addSound(p.getPosition(), Sound.NOTE_FLUTE);
                }else{
                    p.getLevel().addSound(p.getPosition(), Sound.NOTE_BASS);
                }
                switch (lastSec){
                    case 10:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.ten", lastSec));
                        break;
                    case 9:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.nine", lastSec));
                        break;
                    case 8:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.eight", lastSec));
                        break;
                    case 7:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.seven", lastSec));
                        break;
                    case 6:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.six", lastSec));
                        break;
                    case 5:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.five", lastSec));
                        break;
                    case 4:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.four", lastSec));
                        break;
                    case 3:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.three", lastSec));
                        break;
                    case 2:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.two", lastSec));
                        break;
                    case 1:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.one", lastSec));
                        break;
                    case 0:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.zero", lastSec));
                        break;
                }
            }
        }
    }

    @Override
    public void onGameStart() {

    }

    @Override
    public void onGameEnd() {
        if(room.getRound() == room.getMaxRound()){
            for(Player player: room.getPlayers()){
                player.sendActionBar(GameAPI.getLanguage().getTranslation(player, "room.actionbar.gameEnd", room.getGameEndTime() - room.getTime()));
            }
        }else{
            for(Player player: room.getPlayers()){
                player.sendActionBar(GameAPI.getLanguage().getTranslation(player, "room.actionbar.nextRound", room.getGameEndTime() - room.getTime()));
            }
        }
    }

    @Override
    public void onCeremony() {
        for (Player p : room.getPlayers()) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int i1 = random.nextInt(14);
            int i2 = random.nextInt(4);
            CreateFireworkApi.spawnFirework(p.getPosition(), CreateFireworkApi.getColorByInt(i1), CreateFireworkApi.getExplosionTypeByInt(i2));
            p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.ceremony", room.getCeremonyTime() - room.getTime()));
        }
    }

    @Override
    public void onNextRoundPreStart() {
        for (Player p : room.getPlayers()) {
            int lastSec = room.getGameWaitTime() - room.getTime();
            if(lastSec > 10) {
                p.getLevel().addSound(p.getPosition(), Sound.NOTE_HARP);
                p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart", room.getGameWaitTime() - room.getTime()));
            }else{
                if(lastSec == 1){
                    p.getLevel().addSound(p.getPosition(), Sound.NOTE_FLUTE);
                }else{
                    p.getLevel().addSound(p.getPosition(), Sound.NOTE_BASS);
                }
                switch (lastSec){
                    case 10:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.ten", lastSec));
                        break;
                    case 9:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.nine", lastSec));
                        break;
                    case 8:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.eight", lastSec));
                        break;
                    case 7:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.seven", lastSec));
                        break;
                    case 6:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.six", lastSec));
                        break;
                    case 5:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.five", lastSec));
                        break;
                    case 4:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.four", lastSec));
                        break;
                    case 3:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.three", lastSec));
                        break;
                    case 2:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.two", lastSec));
                        break;
                    case 1:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.one", lastSec));
                        break;
                    case 0:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.preStart.zero", lastSec));
                        break;
                }
            }
        }
    }

    @Override
    public void beginPreStart() {

    }

    @Override
    public void beginReadyStart() {
        for(Player p:room.getPlayers()){
            p.getInventory().clearAll();
        }
    }

    @Override
    public void beginGameStart() {
        List<AdvancedLocation> startSpawns = room.getStartSpawn();
        if (room.getTeams().size() > 0) {
            room.allocatePlayerToTeams();
            room.getPlayers().forEach(room::teleportToSpawn);
            room.getSpectators().forEach(player -> {
                if(room.getSpectatorSpawn().size() != 0){
                    Random random = new Random(room.getSpectatorSpawn().size());
                    AdvancedLocation location = room.getSpectatorSpawn().get(random.nextInt(room.getSpectatorSpawn().size()));
                    location.teleport(player);
                }else{
                    if(room.getStartSpawn().size() != 0){
                        Random random = new Random(room.getStartSpawn().size());
                        AdvancedLocation location = room.getStartSpawn().get(random.nextInt(room.getStartSpawn().size()));
                        location.teleport(player);
                    }else{
                        player.teleport(room.getPlayers().get(0).getLocation());
                    }
                }
            });
        } else {
            if (startSpawns.size() > 1) {
                for (Player p : room.getPlayers()) {
                    if (room.getPlayerProperties(p.getName(), "spawnIndex") == null) {
                        Random random = new Random(System.currentTimeMillis());
                        AdvancedLocation location = startSpawns.get(random.nextInt(startSpawns.size()));
                        location.teleport(p);
                    } else {
                        AdvancedLocation location = startSpawns.get((Integer) room.getPlayerProperties(p.getName(), "spawnIndex"));
                        location.teleport(p);
                    }
                }
            } else if (room.getStartSpawn().size() == 1) {
                AdvancedLocation location = startSpawns.get(0);
                for (Player p : room.getPlayers()) {
                    location.teleport(p);
                }
            }
        }
        for (Player p : room.getPlayers()) {
            p.getFoodData().reset();
            p.setGamemode(room.getRoomRule().getGameMode());
            p.sendTitle(GameAPI.getLanguage().getTranslation(p, "room.title.start"), GameAPI.getLanguage().getTranslation(p, "room.subtitle.start"));
        }
    }

    @Override
    public void beginGameEnd() {
        for(Player player:room.getPlayers()){
            player.getInventory().clearAll();
        }
    }

    @Override
    public void beginCeremony() {

    }

    @Override
    public void beginNextRoundPreStart() {

    }

    public Room getRoom() {
        return room;
    }
}