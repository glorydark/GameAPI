package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.tools.BlockTools;
import gameapi.utils.PosSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author glorydark
 */
public class WorldEditFindCommand extends EasySubCommand {

    public WorldEditFindCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender.isPlayer()) {
            Player player = commandSender.asPlayer();
            if (player == null) {
                return false;
            }
            boolean metaCheck = args.length == 2 && Boolean.parseBoolean(args[1]);
            if (!WorldEditCommand.isTwoPosHasUndefined(player)) {
                PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);

                if (args.length > 0) {
                    Block fillFiller = BlockTools.getBlockfromString(args[0]);
                    CompletableFuture.runAsync(() -> {
                        try {
                            List<String> posList = new ArrayList<>();
                            AtomicInteger count = new AtomicInteger();
                            AxisAlignedBB bb = new SimpleAxisAlignedBB(posSet.getPos1(), posSet.getPos2());
                            bb.forEach((i, i1, i2) -> {
                                count.addAndGet(1);
                                Block b = player.getLevel().getBlock(i, i1, i2);
                                if (b.getId() == fillFiller.getId()) {
                                    if (metaCheck && b.getDamage() != fillFiller.getDamage()) {
                                        return;
                                    }
                                    posList.add(i + ":" + i1 + ":" + i2);
                                }
                            });

                            AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("Result");
                            simple.setContent("Check " + count.get() + " blocks. Results are as follows: \n" + String.join("\n", posList));
                            simple.showToPlayer(player);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    });
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
