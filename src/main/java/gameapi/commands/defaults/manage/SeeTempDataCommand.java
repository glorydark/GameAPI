package gameapi.commands.defaults.manage;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.tools.PlayerTempStateManager;

import java.io.File;
import java.util.Map;

/**
 * @author glorydark
 */
public class SeeTempDataCommand extends EasySubCommand {

    public SeeTempDataCommand(String name) {
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
            commandSender.sendMessage("[背包]");
            int invCount = 0;
            for (Map.Entry<Integer, Item> entry : PlayerTempStateManager.loadInventoryCaches(config).entrySet()) {
                if (entry.getValue().getId() != 0) {
                    commandSender.sendMessage("[" + entry.getKey() + "] " + entry.getValue().getName());
                    invCount++;
                }
            }
            if (invCount == 0) {
                commandSender.sendMessage("无");
            }

            commandSender.sendMessage("[副手]");
            int offhandCount = 0;
            for (Map.Entry<Integer, Item> entry : PlayerTempStateManager.loadOffhandCaches(config).entrySet()) {
                if (entry.getValue().getId() != 0) {
                    commandSender.sendMessage("[" + entry.getKey() + "] " + entry.getValue().getName());
                    offhandCount++;
                }
            }
            if (offhandCount == 0) {
                commandSender.sendMessage("无");
            }

            commandSender.sendMessage("[末影箱]");
            int enderChestCount = 0;
            for (Map.Entry<Integer, Item> entry : PlayerTempStateManager.loadEnderChestCaches(config).entrySet()) {
                if (entry.getValue().getId() != 0) {
                    commandSender.sendMessage("[" + entry.getKey() + "] " + entry.getValue().getName());
                    enderChestCount++;
                }
            }
            if (enderChestCount == 0) {
                commandSender.sendMessage("无");
            }
            commandSender.sendMessage("等级: " + PlayerTempStateManager.loadExp(config));
            commandSender.sendMessage("经验: " + PlayerTempStateManager.loadExpLevel(config));
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
