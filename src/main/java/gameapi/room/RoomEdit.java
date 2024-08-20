package gameapi.room;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.item.Item;
import gameapi.annotation.Future;

import java.util.Map;

/**
 * @author glorydark
 * @date {2023/8/4} {14:32}
 */
@Future
public class RoomEdit {

    private final int maxStep;
    protected Player player;
    Map<Integer, Item> inventoryCache;
    private int step = 0;

    public RoomEdit(Player player, int maxStep) {
        this.player = player;
        this.maxStep = maxStep;
    }

    public void init() {
        inventoryCache = player.getInventory().getContents();
        player.getInventory().clearAll();
        this.start();
        this.nextStep();
    }

    public void start() {
        player.sendMessage("您已进入编辑模式！");
    }

    public void end() {
        player.sendMessage("您已退出编辑模式！");
    }

    public void respondEvent(Event event) {

    }

    public void startStep(int step) {

    }

    public void prevStep() {
        if (step <= 1) {
            return;
        }
        // Automatically clean player's inventory
        player.getInventory().clearAll();
        this.step--;
        this.startStep(this.step);
    }

    public void nextStep() {
        if (step >= maxStep) {
            this.end();
            return;
        }
        // Automatically clean player's inventory
        player.getInventory().clearAll();
        this.step++;
        this.startStep(this.step);
    }

    public Player getPlayer() {
        return player;
    }

    public int getStep() {
        return step;
    }
}
