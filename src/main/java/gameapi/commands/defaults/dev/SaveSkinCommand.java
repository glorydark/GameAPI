package gameapi.commands.defaults.dev;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.utils.SerializedImage;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.CalendarTools;
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
            if (pn.equals("@a")) {
                for (Player value : Server.getInstance().getOnlinePlayers().values()) {
                    Server.getInstance().dispatchCommand(commandSender, "gameapi saveskin " + "\"" + value.getName() + "\"");
                }
                return false;
            }
            Player player = Server.getInstance().getPlayerExact(pn);
            if (player != null) {
                Skin skin = player.getSkin();
                String fileName = CalendarTools.getDateStringByDefault(System.currentTimeMillis());
                new File(GameAPI.getPath() + File.separator + "skin_exports" + File.separator + pn + File.separator).mkdirs();
                if (!skin.getGeometryData().isEmpty() && !skin.getGeometryData().equals("null")) {
                    SkinTools.savePlayerJson(skin.getGeometryData(), new File(GameAPI.getPath() + File.separator + "skin_exports" + File.separator + pn + File.separator + fileName + ".json"));
                }
                if (skin.getSkinData() != SerializedImage.EMPTY) {
                    SkinTools.parseSerializedImage(skin.getSkinData(), new File(GameAPI.getPath() + File.separator + "skin_exports" + File.separator + pn + File.separator + fileName + "_skin.png"));
                }
                if (skin.getCapeData() != SerializedImage.EMPTY) {
                    SkinTools.parseSerializedImage(skin.getCapeData(), new File(GameAPI.getPath() + File.separator + "skin_exports" + File.separator + pn + File.separator + fileName + "_cape.png"));
                }
                commandSender.sendMessage(TextFormat.GREEN + "Saved in /skin_exports/" + player.getName() + "/" + fileName);
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
