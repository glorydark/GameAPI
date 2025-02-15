package gameapi.commands.sub;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

/**
 * @author glorydark
 */
public class PlayerEverCommand extends EasySubCommand {

    public PlayerEverCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        CompletableFuture.runAsync(() -> {
            int count = 0;
            try {
                Class<?> serverClass = Server.class;
                // 获取Server类中名为"nameLookup"的字段
                Field nameLookupField = serverClass.getDeclaredField("nameLookup");
                // 设置可访问性，以允许访问私有成员
                nameLookupField.setAccessible(true);
                // 创建Server类的实例（如果需要的话）
                DB nameLookupValue = (DB) nameLookupField.get(Server.getInstance());
                DBIterator iterator = nameLookupValue.iterator();
                for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                    count++;
                }

            } catch (IllegalAccessException | NoSuchFieldException e) {
                GameAPI.getGameDebugManager().printError(e);
                commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.player_ever.no_access"));
            }
            commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.player_ever.success", count));
        });
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
