package gameapi.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.SkinTools;

import java.io.File;

/**
 * @author glorydark
 */
public class SaveSkinCommand extends EasySubCommand {

    public SaveSkinCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender.isOp() && args.length == 1) {
            String pn = args[0];
            Player player = Server.getInstance().getPlayerExact(pn);
            if (player != null) {
                Skin skin = player.getSkin();
                String fileName = System.currentTimeMillis() + "";
                new File(GameAPI.getPath() + "/skin_exports/" + pn + "/").mkdirs();
                SkinTools.savePlayerJson(skin.getGeometryData(), new File(GameAPI.getPath() + "/skin_exports/" + pn + "/" + fileName + ".json"));
                SkinTools.parseSerializedImage(skin.getSkinData(), new File(GameAPI.getPath() + "/skin_exports/" + pn + "/" + fileName + "_skin.png"));
                SkinTools.parseSerializedImage(skin.getCapeData(), new File(GameAPI.getPath() + "/skin_exports/" + pn + "/" + fileName + "_cape.png"));
                commandSender.sendMessage(TextFormat.GREEN + "Saved in /skin_exports/" + fileName);
            } else {
                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.player_offline", args[0]));
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
