package gameapi.commands.defaults.manage;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.tools.PlayerTempStateManager;

import java.io.File;

/**
 * @author glorydark
 */
public class RecoverTempDataCommand extends EasySubCommand {

    public RecoverTempDataCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = Server.getInstance().getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(TextFormat.RED + "找不到玩家！");
            return false;
        }
        String backup = args[1];
        File file = new File(GameAPI.getPath() + File.separator + "player_caches_old" + File.separator + player.getName() + File.separator + backup + ".yml");
        if (file.exists()) {
            Config config = new Config(file, Config.YAML);
            PlayerTempStateManager.loadInventoryCaches(config, player);
            PlayerTempStateManager.loadOffhandCaches(config, player);
            PlayerTempStateManager.loadEnderChestCaches(config, player);
            PlayerTempStateManager.loadExpCaches(config, player);
            commandSender.sendMessage(TextFormat.GREEN + "恢复玩家 " + player.getName() + " 小游戏背包数据成功，数据名称: " + args[1]);
        } else {
            commandSender.sendMessage(TextFormat.RED + "文件不存在！");
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
