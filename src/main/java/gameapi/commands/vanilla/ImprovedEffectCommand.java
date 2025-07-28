package gameapi.commands.vanilla;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.entity.EntityPotionEffectEvent;
import cn.nukkit.potion.Effect;
import gameapi.commands.base.EasySubCommand;

public class ImprovedEffectCommand extends EasySubCommand {

    public ImprovedEffectCommand(String name) {
        super(name);
        this.getCommandParameters().clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("effect", CommandEnum.ENUM_EFFECT),
                CommandParameter.newType("ticks", true, CommandParamType.INT),
                CommandParameter.newType("amplifier", true, CommandParamType.INT),
                CommandParameter.newEnum("hideParticle", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("clear", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("clear", new CommandEnum("ClearEffects", "clear"))
        });
    }

    // xp player level exp
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length >= 2) {
            Player player = Server.getInstance().getPlayer(args[0]);
            if (args[1].equals("clear")) {
                player.removeAllEffects(EntityPotionEffectEvent.Cause.PLUGIN);
            } else {
                Effect effect;
                try {
                    String str = args[1];
                    effect = Effect.getEffectByName(str);
                } catch (Exception e) {
                    int id = Integer.parseInt(args[1]);
                    effect = Effect.getEffect(id);
                }
                if (effect == null) {
                    sender.sendMessage("Effect not found: " + args[1]);
                    return false;
                }
                int duration = 300;
                int amplification = 0;
                boolean hideParticle = false;
                if (args.length >= 3) {
                    duration = Integer.parseInt(args[2]);
                    if (args.length >= 4) {
                        amplification = Integer.parseInt(args[3]);
                        if (args.length >= 5) {
                            hideParticle = Boolean.parseBoolean(args[4]);
                        }
                    }
                }
                effect.setDuration(duration);
                effect.setAmplifier(amplification);
                effect.setVisible(!hideParticle);
                player.addEffect(effect);
                sender.sendMessage("Effect applied to " + player.getName());
            }
        }
        return true;
    }
}