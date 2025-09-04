package gameapi.commands.defaults.dev;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.ByteArrayTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import gameapi.commands.base.EasySubCommand;
import gameapi.form.AdvancedFormWindowCustom;
import gameapi.form.element.ResponsiveElementDropdown;

import java.util.ArrayList;

/**
 * @author glorydark
 */
public class ShowNBTCommand extends EasySubCommand {

    public ShowNBTCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        Item item = player.getInventory().getItemInHand();
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom("物品NBT");
        custom.addElement(
                new ElementLabel(getNBT(item.getNamedTag(), 0))
        );
        custom.addElement(
                new ElementInput("修改键位")
        );
        custom.addElement(new ResponsiveElementDropdown("类型", new ArrayList<>() {
                    {
                        this.add("int");
                        this.add("byte");
                        this.add("string");
                    }
                }));
        custom.addElement(
                new ElementInput("值")
        );
        custom.onRespond((player1, formResponseCustom) -> {
           String key = formResponseCustom.getInputResponse(1);
           String type = formResponseCustom.getDropdownResponse(2).getElementContent();
           String value = formResponseCustom.getInputResponse(3);
           if (key.isEmpty() || value.isEmpty()) {
               return;
           }
           Item newItem = player1.getInventory().getItemInHand().clone();
           switch (type) {
               case "int" -> {
                   newItem.getNamedTag().putInt(key, Integer.parseInt(value));
               }
               case "byte" -> {
                   newItem.getNamedTag().putByte(key, Integer.parseInt(value));
               }
               case "string" -> {
                   newItem.getNamedTag().putString(key, value);
               }
           }
           newItem.setNamedTag(newItem.getNamedTag());
           player1.getInventory().setItemInHand(newItem);
        });
        custom.setSubmitButtonText("确认");
        custom.showToPlayer(player);
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }

    public String getNBT(CompoundTag tag, int space) {
        StringBuilder builder = new StringBuilder();
        for (Tag allTag : tag.getAllTags()) {
            if (space > 0) {
                builder.append(" ".repeat(space));
            }
            if (allTag.getId() == 7) {
                ByteArrayTag byteArrayTag = (ByteArrayTag) allTag;
                if (byteArrayTag.getData() == null) {
                    builder.append(allTag.getName()).append(": null").append("\n");
                } else {
                    if (byteArrayTag.getData().length == 0) {
                        builder.append(allTag.getName()).append(": []").append("\n");
                    } else {
                        builder.append(allTag.getName()).append(": ").append(allTag.toSNBT()).append("\n");
                    }
                }
            } else if (allTag.getId() == 10)  {
                builder.append(allTag.getName()).append(":\n");
                builder.append(getNBT((CompoundTag) allTag, space + 2));
            } else {
                builder.append(allTag.getName()).append(": ").append(allTag.toSNBT()).append("\n");
            }
        }
        return builder.toString();
    }
}
