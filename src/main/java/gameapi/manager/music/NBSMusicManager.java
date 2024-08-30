package gameapi.manager.music;

import cn.nukkit.Player;
import gameapi.GameAPI;
import gameapi.room.Room;
import gameapi.utils.music.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author glorydark
 */
public class NBSMusicManager {

    protected Room room;

    protected List<Song> playList;

    protected PlayType playType;

    private Song currentSong = null;

    protected final List<Player> stopMusicPlayers = new ArrayList<>();

    protected int currentTick = 0;

    protected long lastPlayed = 0L;

    protected boolean stopped = true;

    public NBSMusicManager(Room room, PlayType playType) {
        this.room = room;
        this.playList = new ArrayList<>();
        this.playType = playType;
    }

    public enum PlayType {
        SINGLE_TRACK_LOOP,
        LIST_LOOP,
        SHUFFLE,
        LIST
    }

    public void start() {
        if (!this.stopped) {
            return;
        }
        this.reset();
        this.stopped = false;
    }

    public void onTick() {
        Song currentSong = this.getCurrentSong();
        if (currentSong == null) {
            if (this.playList.isEmpty()) {
                this.stopped = true;
                GameAPI.getGameDebugManager().error("Found the empty song list, room: " + this.room.getRoomName());
                return;
            }
            this.currentSong = this.playList.get(0);
        }
        if (currentSong == null) {
            this.stopped = true;
            GameAPI.getGameDebugManager().error("Found the song null, stopped playing for the room " + this.room.getRoomName());
            return;
        }
        if (System.currentTimeMillis() - this.lastPlayed < 50 * currentSong.getDelay()) {
            return;
        }
        this.lastPlayed = System.currentTimeMillis();
        boolean isFinish = this.currentTick > currentSong.getLength();
        if (isFinish) {
            if (this.getNextSong() == null) {
                GameAPI.getGameDebugManager().info("Finish playing, stopping music player for the room: " + this.room.getRoomName());
                this.stopped = true;
            } else {
                this.currentSong = this.getNextSong();
            }
            return;
        }
        this.currentTick++;
        this.currentSong.playTick(this.room.getPlayers().stream().filter(player -> !this.stopMusicPlayers.contains(player)).distinct().toArray(Player[]::new), this.currentTick);
        this.lastPlayed = System.currentTimeMillis();
    }

    public void stop() {
        this.stopped = true;
    }

    public void reset() {
        this.stopped = true;
        this.currentTick = 0;
        this.lastPlayed = 0L;
        if (this.currentSong == null) {
            if (this.playList.isEmpty()) {
                return;
            }
            this.currentSong = this.playList.get(0);
        }
    }

    public void removeSong(Song song) {
        this.playList.remove(song);
    }

    public Song getNextSong() {
        int currentSongIndex = this.playList.indexOf(this.currentSong);
        switch (this.playType) {
            case LIST_LOOP:
                if (currentSongIndex + 1 >= this.playList.size()) {
                    currentSongIndex = 0;
                } else {
                    currentSongIndex += 1;
                }
                return this.playList.get(currentSongIndex);
            case SINGLE_TRACK_LOOP:
                return this.playList.get(currentSongIndex);
            case SHUFFLE:
                Collections.shuffle(this.playList);
                return this.playList.get(ThreadLocalRandom.current().nextInt(this.playList.size()));
            case LIST:
            default:
                if (currentSongIndex + 1 >= this.playList.size()) {
                    return null;
                }
                return this.playList.get(currentSongIndex + 1);
        }
    }

    public List<Player> getStopMusicPlayers() {
        return stopMusicPlayers;
    }

    public boolean isStopped() {
        return stopped;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public List<Song> getPlayList() {
        return playList;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    public PlayType getPlayType() {
        return playType;
    }

    public void setPlayType(PlayType playType) {
        this.playType = playType;
    }

    public Room getRoom() {
        return room;
    }

    public Song getCurrentSong() {
        return currentSong;
    }
}
