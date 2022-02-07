package gameapi.sound;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.network.protocol.ModalFormRequestPacket;
import gameapi.FormWindowFactory;
import gameapi.MainClass;

public class SoundTest extends Command {
    public SoundTest(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender.isOp() || !commandSender.isPlayer())
        if(strings.length > 0){
            switch (strings[0]){
                case "playResSound":
                    if(strings.length > 2) {
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        if (player != null) {
                            Sound.playResourcePackOggMusic(player, strings[2]);
                        }else{
                            MainClass.plugin.getLogger().warning("Can not find the chosen player!");
                        }
                    }
                    break;
                case "playAmbientSound":
                    if(strings.length > 2) {
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        if (player != null) {
                            cn.nukkit.level.Sound sound = cn.nukkit.level.Sound.valueOf(strings[2]);
                            if(sound.getSound() != null) {
                                Sound.addAmbientSound(player.level, player, sound);
                            }else{
                                MainClass.plugin.getLogger().warning("Can not find the chosen sound!");
                            }
                        }else{
                            MainClass.plugin.getLogger().warning("Can not find the chosen player!");
                        }
                    }
                    break;
                case "test":
                    if(strings.length > 1) {
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        FormWindowFactory factory = new FormWindowFactory();
                        player.showFormWindow(factory);

                    }
                    break;
            }
        }
        return true;
    }
}
