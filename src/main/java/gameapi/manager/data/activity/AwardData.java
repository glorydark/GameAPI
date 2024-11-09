package gameapi.manager.data.activity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import gameapi.form.element.ResponsiveElementButton;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * @author glorydark
 */
@Getter
public class AwardData {

    private final ActivityData activityData;

    private final String name;

    private BiFunction<ActivityData, Player, Boolean> checkFinish = null;

    private BiFunction<ActivityData, Player, Boolean> checkClaimStatus = null;

    private BiConsumer<ActivityData, Player> claimProcess = null;

    private final List<String> commands = new ArrayList<>();

    private final List<String> messages = new ArrayList<>();

    private ElementButtonImageData elementButtonImageData = new ElementButtonImageData("path", "");

    public static final String PLAYER_REPLACEMENT = "{player}";
    protected static String STATUS_CLAIMED = "§6[已领取]";
    protected static String STATUS_UNCLAIMED = "§a[可领取]";
    protected static String STATUS_NOT_QUALIFIED = "§c[未满足条件]";

    public AwardData(ActivityData activityData, String name) {
        this.activityData = activityData;
        this.name = name;
    }

    public AwardData checkFinish(BiFunction<ActivityData, Player, Boolean> checkFinish) {
        this.checkFinish = checkFinish;
        return this;
    }

    public AwardData checkClaimStatus(BiFunction<ActivityData, Player, Boolean> checkClaimStatus) {
        this.checkClaimStatus = checkClaimStatus;
        return this;
    }

    public AwardData claim(BiConsumer<ActivityData, Player> claimProcess) {
        this.claimProcess = claimProcess;
        return this;
    }

    public AwardData message(String... strings) {
        this.messages.addAll(Arrays.asList(strings));
        return this;
    }

    public AwardData command(String... strings) {
        this.commands.addAll(Arrays.asList(strings));
        return this;
    }

    public AwardData buttonImageData(ElementButtonImageData imageData) {
        this.elementButtonImageData = imageData;
        return this;
    }

    public ElementButton getElementButton(Player player) {
        boolean finished = this.checkFinish != null && this.checkFinish.apply(this.activityData, player);
        if (finished) {
            boolean claimed = this.checkClaimStatus != null && this.checkClaimStatus.apply(this.activityData, player);
            if (claimed) {
                return new ElementButton(this.name + "\n" + STATUS_CLAIMED, elementButtonImageData);
            } else {
                return new ResponsiveElementButton(this.name + "\n" + STATUS_UNCLAIMED, elementButtonImageData)
                        .onRespond(player1 -> {
                            this.claimProcess.accept(this.activityData, player1);
                            for (String command : this.commands) {
                                Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), command.replace(PLAYER_REPLACEMENT, "\"" + player1.getName() + "\""));
                            }
                            for (String message : this.messages) {
                                player1.sendMessage(message.replace(PLAYER_REPLACEMENT, player1.getName()));
                            }
                        });
            }
        } else {
            return new ElementButton(this.name + "\n" + STATUS_NOT_QUALIFIED, elementButtonImageData);
        }
    }
}
