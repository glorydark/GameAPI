package gameapi.skill;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import gameapi.effect.Effect;

public class CustomSkills { //常规药水技能
    private String customName;
    private String descriptions;
    private Effect effect;
    private Boolean hasEffect;
    private Item item;
    private Integer coolDownTick;

    public CustomSkills(String customName, String descriptions, Item item, Effect effect, Boolean hasEffect, Integer coolDownTick){
        this.customName = customName;
        this.descriptions = descriptions;
        this.effect = effect;
        this.hasEffect = hasEffect;
        this.item = item;
        this.coolDownTick = coolDownTick;
    }

    @Override
    public String toString() {
        return "CustomSkills{" +
                "customName='" + customName + '\'' +
                ", descriptions='" + descriptions + '\'' +
                ", effect=" + effect +
                ", hasEffect=" + hasEffect +
                '}';
    }

    public Integer getCoolDownTick() {
        return coolDownTick;
    }

    public void setCoolDownTick(Integer coolDownTick) {
        this.coolDownTick = coolDownTick;
    }

    public void giveSkillItem(Player player, Boolean sendMsg){
        removeSkillItem(player);
        Item send = item;
        CompoundTag tag = new CompoundTag();
        tag.putString("ItemType", "skillItem");
        send.setCompoundTag(tag);
        send.setCustomName(customName);
        player.getInventory().setItem(2,send);
        if(sendMsg) {
            player.sendMessage(TextFormat.YELLOW + "您获得了技能:" + customName);
        }
    }

    public void giveSkillItem(Player player){
        Item send = item;
        CompoundTag tag = new CompoundTag();
        tag.putString("ItemType", "skillItem");
        send.setCompoundTag(tag);
        send.setCustomName(customName);
        player.getInventory().setItem(2,send);
        player.sendMessage(TextFormat.YELLOW + "您获得了技能:" + customName);
    }

    public void removeSkillItem(Player player){
        Item item = player.getInventory().getItem(2);
        if(item.hasCompoundTag()){
            if(item.getNamedTag().contains("ItemType")){
                if(item.getNamedTag().getString("ItemType").equals("skillItem")){
                    player.getInventory().setItem(2, Item.get(Block.AIR));
                }
            }
        }
    }

    public Boolean hasEffect() {
        return hasEffect;
    }

    public void hasEffect(Boolean hasEffect) {
        this.hasEffect = hasEffect;
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }
}
