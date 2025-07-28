package gameapi.commands.vanilla;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.CameraInstructionPacket;
import cn.nukkit.network.protocol.types.camera.CameraEase;
import cn.nukkit.network.protocol.types.camera.CameraFadeInstruction;
import cn.nukkit.network.protocol.types.camera.CameraSetInstruction;
import cn.nukkit.utils.CameraPresetManager;
import gameapi.commands.base.EasySubCommand;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImprovedCameraCommand extends EasySubCommand {

    public static final String[] EASE_TYPES = Arrays.stream(CameraEase.values()).map(CameraEase::getSerializeName).toArray(String[]::new);

    public ImprovedCameraCommand(String name) {
        super(name);
        this.getCommandParameters().clear();
        this.commandParameters.put("clear", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET),
                CommandParameter.newEnum("clear", false, new String[]{"clear"})
        });
        this.commandParameters.put("fade", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET),
                CommandParameter.newEnum("fade", false, new String[]{"fade"}),
                CommandParameter.newEnum("facing", true, new String[]{"facing"}),
                CommandParameter.newType("players", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("fade-color", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET),
                CommandParameter.newEnum("fade", false, new String[]{"fade"}),
                CommandParameter.newEnum("color", false, new String[]{"color"}),
                CommandParameter.newType("red", false, CommandParamType.FLOAT),
                CommandParameter.newType("green", false, CommandParamType.FLOAT),
                CommandParameter.newType("blue", false, CommandParamType.FLOAT),
                CommandParameter.newEnum("facing", true, new String[]{"facing"}),
                CommandParameter.newType("players", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("fade-time-color", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET),
                CommandParameter.newEnum("fade", false, new String[]{"fade"}),
                CommandParameter.newEnum("time", false, new String[]{"time"}),
                CommandParameter.newType("fadeInSeconds", false, CommandParamType.FLOAT),
                CommandParameter.newType("holdSeconds", false, CommandParamType.FLOAT),
                CommandParameter.newType("fadeOutSeconds", false, CommandParamType.FLOAT),
                CommandParameter.newEnum("color", false, new String[]{"color"}),
                CommandParameter.newType("red", false, CommandParamType.FLOAT),
                CommandParameter.newType("green", false, CommandParamType.FLOAT),
                CommandParameter.newType("blue", false, CommandParamType.FLOAT),
                CommandParameter.newEnum("facing", true, new String[]{"facing"}),
                CommandParameter.newType("players", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("set-default", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("default", true, new String[]{"default"}),
                CommandParameter.newEnum("facing", true, new String[]{"facing"}),
                CommandParameter.newType("players", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("set-rot", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("rot", false, new String[]{"rot"}),
                CommandParameter.newType("xRot", false, CommandParamType.VALUE),
                CommandParameter.newType("yRot", false, CommandParamType.VALUE),
                CommandParameter.newEnum("facing", true, new String[]{"facing"}),
                CommandParameter.newType("players", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("set-pos", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("pos", false, new String[]{"pos"}),
                CommandParameter.newType("position", false, CommandParamType.POSITION),
                CommandParameter.newEnum("facing", true, new String[]{"facing"}),
                CommandParameter.newType("players", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("set-pos-rot", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("pos", false, new String[]{"pos"}),
                CommandParameter.newType("position", false, CommandParamType.POSITION),
                CommandParameter.newEnum("rot", false, new String[]{"rot"}),
                CommandParameter.newType("xRot", false, CommandParamType.VALUE),
                CommandParameter.newType("yRot", false, CommandParamType.VALUE),
                CommandParameter.newEnum("facing", true, new String[]{"facing"}),
                CommandParameter.newType("players", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("set-ease-default", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("ease", false, new String[]{"ease"}),
                CommandParameter.newType("easeTime", false, CommandParamType.FLOAT),
                CommandParameter.newEnum("easeType", false, EASE_TYPES),
                CommandParameter.newEnum("default", true, new String[]{"default"}),
                CommandParameter.newEnum("facing", true, new String[]{"facing"}),
                CommandParameter.newType("players", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("set-ease-rot", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("ease", false, new String[]{"ease"}),
                CommandParameter.newType("easeTime", false, CommandParamType.FLOAT),
                CommandParameter.newEnum("easeType", false, EASE_TYPES),
                CommandParameter.newEnum("rot", false, new String[]{"rot"}),
                CommandParameter.newType("xRot", false, CommandParamType.VALUE),
                CommandParameter.newType("yRot", false, CommandParamType.VALUE),
                CommandParameter.newEnum("facing", true, new String[]{"facing"}),
                CommandParameter.newType("players", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("set-ease-pos", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("ease", false, new String[]{"ease"}),
                CommandParameter.newType("easeTime", false, CommandParamType.FLOAT),
                CommandParameter.newEnum("easeType", false, EASE_TYPES),
                CommandParameter.newEnum("pos", false, new String[]{"pos"}),
                CommandParameter.newType("position", false, CommandParamType.POSITION),
                CommandParameter.newEnum("facing", true, new String[]{"facing"}),
                CommandParameter.newType("players", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("set-ease-pos-rot", new CommandParameter[]{
                CommandParameter.newType("players", false, CommandParamType.TARGET),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("preset", false, CommandEnum.CAMERA_PRESETS),
                CommandParameter.newEnum("ease", false, new String[]{"ease"}),
                CommandParameter.newType("easeTime", false, CommandParamType.FLOAT),
                CommandParameter.newEnum("easeType", false, EASE_TYPES),
                CommandParameter.newEnum("pos", false, new String[]{"pos"}),
                CommandParameter.newType("position", false, CommandParamType.POSITION),
                CommandParameter.newEnum("rot", false, new String[]{"rot"}),
                CommandParameter.newType("xRot", false, CommandParamType.VALUE),
                CommandParameter.newType("yRot", false, CommandParamType.VALUE),
                CommandParameter.newEnum("facing", true, new String[]{"facing"}),
                CommandParameter.newType("players", true, CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        List<Player> players = new ArrayList<>();
        switch (args[0]) {
            case "@s":
                if (sender.isPlayer()) {
                    players.add(sender.asPlayer());
                }
                break;
            case "@a":
                players.addAll(Server.getInstance().getOnlinePlayers().values());
                break;
            default:
                players.add(Server.getInstance().getPlayer(args[0]));
                break;
        }
        for (Player player : players) {
            if (player == null) {
                continue;
            }
            CameraInstructionPacket pk = processPacket(player, args);
            player.dataPacket(pk);
            // player.sendMessage(new TranslationContainer("nukkit.camera.success", commandLabel));
        }
        return true;
    }

    public CameraInstructionPacket processPacket(Player player, String[] args) {
        CameraInstructionPacket pk = new CameraInstructionPacket();
        for (int i = 1; i < args.length; i++) {
            int subStartIndex = i + 1;
            int remainedIndex = args.length - subStartIndex;
            if (subStartIndex > args.length) {
                break;
            }
            switch (args[i]) {
                case "clear":
                    pk.setClear(OptionalBoolean.of(true));
                    return pk;
                case "ease":
                    if (remainedIndex < 2) {
                        continue;
                    }
                    pk.getSetInstruction().setEase(new CameraSetInstruction.EaseData(CameraEase.fromName(args[subStartIndex + 1]), Float.parseFloat(args[subStartIndex])));
                    i += 2;
                    break;
                case "fade":
                    if (remainedIndex < 1) {
                        continue;
                    }
                    pk.setFadeInstruction(new CameraFadeInstruction());
                    switch (args[subStartIndex]) {
                        case "time":
                            if (remainedIndex < 3) {
                                continue;
                            }
                            pk.getFadeInstruction().setTimeData(new CameraFadeInstruction.TimeData(Float.parseFloat(args[subStartIndex + 1]), Float.parseFloat(args[subStartIndex + 2]), Float.parseFloat(args[subStartIndex + 3])));
                            i += 3;
                            if (remainedIndex == 7 && args[4].equals("fade")) {
                                pk.getFadeInstruction().setColor(new Color(Float.parseFloat(args[subStartIndex + 5]), Float.parseFloat(args[subStartIndex + 6]), Float.parseFloat(args[subStartIndex + 7])));
                                i += 4;
                            }
                            break;
                        case "color":
                            if (remainedIndex < 3) {
                                continue;
                            }
                            pk.getFadeInstruction().setColor(new Color(Math.min(1.0f, Float.parseFloat(args[subStartIndex + 1])), Math.min(1.0f, Float.parseFloat(args[subStartIndex + 2])), Math.min(1.0f, Float.parseFloat(args[subStartIndex + 3]))));
                            i += 4;
                            break;
                    }
                    break;
                case "set":
                    pk.setSetInstruction(new CameraSetInstruction());
                    pk.getSetInstruction().setPos(player.asVector3f());
                    pk.getSetInstruction().setPreset(CameraPresetManager.getPreset(args[subStartIndex]));
                    i += 1;
                    break;
                case "pos":
                    if (remainedIndex < 3) {
                        continue;
                    }
                    Vector3f vector3f = new Vector3f(
                            args[subStartIndex].equals("~") ?
                                    player.asVector3f().getX() : Float.parseFloat(args[subStartIndex]),
                            args[subStartIndex + 1].equals("~") ?
                                    player.asVector3f().getY() : Float.parseFloat(args[subStartIndex + 1]),
                            args[subStartIndex + 2].equals("~") ?
                                    player.asVector3f().getZ() : Float.parseFloat(args[subStartIndex + 2]));
                    pk.getSetInstruction().setPos(vector3f);
                    i += 3;
                    break;
                case "rot":
                    if (remainedIndex < 2) {
                        continue;
                    }
                    pk.getSetInstruction().setRot(
                            new Vector2f(
                                    args[subStartIndex].equals("~") ? (float) player.getYaw() : Float.parseFloat(args[subStartIndex]),
                                    args[subStartIndex + 1].equals("~") ? (float) player.getPitch() : Float.parseFloat(args[subStartIndex + 1])
                            ));
                    i += 2;
                    break;
                case "facing":
                    if (remainedIndex < 1) {
                        continue;
                    }
                    if (pk.getSetInstruction() == null) {
                        pk.setSetInstruction(new CameraSetInstruction());
                    }
                    Player facing;
                    if (args[subStartIndex].equals("@s")) {
                        facing = player;
                    } else {
                        facing = Server.getInstance().getPlayer(args[subStartIndex]);
                    }
                    if (facing != null) {
                        pk.getSetInstruction().setFacing(facing.asVector3f());
                    }
                    return pk;
            }
            if (i >= args.length) {
                return pk;
            }
        }
        return pk;
    }
}