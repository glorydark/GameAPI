package gameapi.room.executor;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import gameapi.GameAPI;
import gameapi.room.Room;
import gameapi.tools.*;
import gameapi.utils.AdvancedLocation;
import gameapi.utils.TitleData;
import gameapi.utils.text.GameTranslationContainer;
import lombok.Getter;

import java.util.List;
import java.util.Random;

/**
 * This class is meant to design the basic operations,
 * including teleporting players to spawns, sending tips.
 */
@Getter
public class BaseRoomExecutor extends RoomExecutor {

    protected Room room;

    public BaseRoomExecutor(Room room) {
        this.room = room;
    }

    @Override
    public void onWait() {
        if (!this.room.isAllowedToStart()) {
            this.room.sendActionbarToAll(new GameTranslationContainer("room.actionbar.wait.need_start_pass"));
        } else {
            if (this.room.getPlayers().size() < this.room.getMinPlayer()) {
                this.room.sendActionbarToAll(new GameTranslationContainer("room.actionbar.wait.wait_for_players", this.room.getPlayers().size(), this.room.getMinPlayer(), room.getMinPlayer() - room.getPlayers().size()));
            }
        }
    }

    @Override
    public void onPreStart() {
        int lastSecond = this.room.getWaitTime() - this.room.getTime();
        if (lastSecond > 3) {
            this.room.sendActionbarToAll(new GameTranslationContainer("room.actionbar.preStart.countdown", lastSecond));
        } else {
            this.room.sendActionbarToAll(new GameTranslationContainer("room.actionbar.preStart.countdown.ready", lastSecond));
        }
    }

    @Override
    public void onReadyStart() {
        int lastSec = this.room.getGameWaitTime() - this.room.getTime();
        if (lastSec > 10) {
            SoundTools.addSoundToPlayer(this.room.getPlayers(), Sound.NOTE_HARP, 1.0f, 1.0f);
            this.room.sendActionbarToAll(new GameTranslationContainer("room.actionbar.readyStart.countdown.normal", this.room.getGameWaitTime() - this.room.getTime()), false);
        } else {
            if (lastSec == 1) {
                SoundTools.addSoundToPlayer(this.room.getPlayers(), Sound.NOTE_FLUTE, 1.0f, 1.0f);
            } else {
                SoundTools.addSoundToPlayer(this.room.getPlayers(), Sound.NOTE_BASS, 1.0f, 1.0f);
            }
            for (Player player : this.room.getPlayers()) {
                player.sendActionBar(SmartTools.getCountdownText(this.room, player));
            }
            for (Player spectator : this.room.getSpectators()) {
                spectator.sendActionBar(SmartTools.getCountdownText(this.room, spectator));
            }
        }
    }

    @Override
    public void onGameEnd() {
        this.room.sendActionbarToAll(new GameTranslationContainer("room.actionbar.gameEnd", this.room.getGameEndTime() - this.room.getTime()));
    }

    @Override
    public void onCeremony() {
        this.room.sendActionbarToAll(new GameTranslationContainer("room.actionbar.ceremony", this.room.getCeremonyTime() - this.room.getTime()));
        for (Player p : this.room.getPlayers()) {
            if (p.getGamemode() == 3) {
                continue;
            }
            FireworkTools.spawnRandomFirework(p.getLocation());
        }
    }

    @Override
    public void onNextRoundPreStart() {
        this.room.sendActionbarToAll(new GameTranslationContainer("room.actionbar.nextRound", this.room.getNextRoundPreStartTime() - this.room.getTime()));
    }

    @Override
    public void beginGameStart() {
        if (GameAPI.getInstance().isTipsEnabled()) {
            for (Level playLevel : this.room.getPlayLevels()) {
                for (Player player : this.room.getPlayers()) {
                    TipsTools.closeTipsShow(playLevel.getName(), player);
                }
                for (Player player : this.room.getSpectators()) {
                    TipsTools.closeTipsShow(playLevel.getName(), player);
                }
            }
        }
        for (Player p : this.room.getPlayers()) {
            p.setImmobile(false);
            p.removeAllEffects();
            p.getFoodData().reset();
            p.setGamemode(this.room.getRoomRule().getGameMode());
        }
        this.room.sendTitleToAll(
                new TitleData()
                        .title(new GameTranslationContainer("room.title.start"))
                        .subtitle(new GameTranslationContainer("room.subtitle.start"))
        );
        if (!this.room.getRoomRule().isAutoStartTeleport()) {
            return;
        }
        List<AdvancedLocation> startSpawns = this.room.getStartSpawn();
        if (!this.room.getTeams().isEmpty()) {
            if (this.room.getRoomRule().isAutoAllocatePlayerToTeam()) {
                this.room.allocatePlayerToTeams(true);
            }
            this.room.getPlayers().forEach(room::teleportToSpawn);
        } else {
            if (startSpawns.size() > 1) {
                this.room.getPlayers().forEach(room::teleportToSpawn);
            } else if (this.room.getStartSpawn().size() == 1) {
                AdvancedLocation location = startSpawns.get(0);
                for (Player p : this.room.getPlayers()) {
                    location.teleport(p);
                }
            }
        }
        for (Player player : this.room.getSpectators()) {
            if (room.getPlayLevels().contains(player.getLevel())) {
                continue;
            }
            if (!this.room.getSpectatorSpawn().isEmpty()) {
                Random random = new Random(this.room.getSpectatorSpawn().size());
                AdvancedLocation location = this.room.getSpectatorSpawn().get(random.nextInt(this.room.getSpectatorSpawn().size()));
                location.teleport(player);
            } else {
                if (!this.room.getStartSpawn().isEmpty()) {
                    Random random = new Random(this.room.getStartSpawn().size());
                    AdvancedLocation location = this.room.getStartSpawn().get(random.nextInt(this.room.getStartSpawn().size()));
                    location.teleport(player);
                } else {
                    player.teleport(this.room.getPlayers().get(0).getLocation());
                }
            }
        }
    }
}
