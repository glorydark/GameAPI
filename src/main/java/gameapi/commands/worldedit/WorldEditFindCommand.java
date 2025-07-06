package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;
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
            boolean summary = args.length == 3 && Boolean.parseBoolean(args[2]);
            if (!WorldEditCommand.isTwoPosHasUndefined(player)) {
                PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);

                if (args.length > 0) {
                    Block fillFiller = BlockTools.getBlockfromString(args[0]);
                    List<Vector3> vector3s = new ArrayList<>();
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
                                    vector3s.add(new Vector3(i, i1, i2));
                                }
                            });

                            if (summary) {
                                AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("Result");
                                simple.setContent("Check " + count.get() + " blocks. Results are as follows: \n" + String.join("\n", posList));
                                simple.showToPlayer(player);
                            } else {
                                AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("Result", "Find " + posList.size() + " specific blocks. The top 50 are listed below!");
                                for (int i = 0; i < Math.min(50, vector3s.size() - 1); i++) {
                                    Vector3 vector3 = vector3s.get(i);
                                    simple.addButton(
                                            new ResponsiveElementButton(vector3.getFloorX() + ":" + vector3.getFloorY() + ":" + vector3.getFloorZ())
                                                    .onRespond(player1 -> {
                                                        player1.teleport(vector3);
                                                        player1.sendMessage(TextFormat.GREEN + "Teleport to the block pos!");
                                                    })
                                    );
                                }
                                simple.showToPlayer(player);
                            }
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
