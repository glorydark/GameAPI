package gameapi.tools;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.*;
import gameapi.GameAPI;
import gameapi.annotation.Experimental;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author glorydark
 */
@Experimental
@Deprecated
public class GameAPIComponentParser {
    
    public static void preProcessDamageEvent(EntityDamageEvent entityDamageEvent) {
        if (!GameAPI.isExperimentalFeature()) {
            return;
        }
        Entity entity = entityDamageEvent.getEntity();
        if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) entityDamageEvent;
            Entity damager = event.getDamager();
            Item attackItem = EntityTools.getEntityItemInHand(damager);
            if (attackItem.getNamedTag().contains("gameapi:damage")) {
                if (attackItem.getId() == 0) {
                    return;
                }
                Tag tag = attackItem.getNamedTag().get("gameapi:damage");
                if (tag instanceof StringTag) {
                    String dmgFormula = ((StringTag) tag).parseValue();
                    dmgFormula =
                            dmgFormula
                                    .replace("{damage}", String.valueOf(event.getDamage()))
                                    .replace("{damager_health}", String.valueOf(damager.getHealth()))
                                    .replace("{victim_health}", String.valueOf(entity.getHealth()));

                    // 对常量进行处理
                    if (attackItem.getNamedTag().contains("gameapi:constants")) {
                        CompoundTag constants = attackItem.getNamedTag().getCompound("gameapi:constants");
                        for (Map.Entry<String, Tag> entry : constants.getTags().entrySet()) {
                            Tag constant = entry.getValue();
                            if (constant instanceof IntTag) {
                                dmgFormula = dmgFormula.replace(entry.getKey(), String.valueOf(((IntTag) constant).getData()));
                            } else if (constant instanceof DoubleTag) {
                                dmgFormula = dmgFormula.replace(entry.getKey(), String.valueOf(((DoubleTag) constant).getData()));
                            } else if (constant instanceof StringTag) {
                                String data = ((StringTag) constant).parseValue();
                                if (data.startsWith("random_int:")) {
                                    String[] splits = data.replace("random_int:", "").split("-");
                                    if (splits.length == 2) {
                                        int min = Integer.parseInt(splits[0]);
                                        int max = Integer.parseInt(splits[1]);
                                        dmgFormula = data.replace(entry.getKey(), String.valueOf(RandomTools.getRandom(min, max)));
                                    }
                                } else if (data.equals("random_double")) {
                                    dmgFormula = data.replace(entry.getKey(), String.valueOf(ThreadLocalRandom.current().nextDouble()));
                                } else if (data.startsWith("random_double:")) {
                                    String[] splits = data.replace("random_double:", "").split("-");
                                    if (splits.length == 2) {
                                        double min = Integer.parseInt(splits[0]);
                                        double max = Integer.parseInt(splits[1]);
                                        dmgFormula = data.replace(entry.getKey(), String.valueOf(RandomTools.getRandom(min, max)));
                                    }
                                } else {
                                    dmgFormula = dmgFormula.replace(entry.getKey(), data);
                                }
                            }
                        }
                    }
                    event.setDamage((float) (event.getDamage() + StringFormulaCalculator.evaluate(dmgFormula)));
                } else if (tag instanceof IntTag) {
                    int dmg = ((IntTag) tag).getData();
                    event.setDamage(dmg);
                }
            }
        }
        float baseDamage = entityDamageEvent.getDamage();
        for (Map.Entry<Integer, Item> armorEntry : EntityTools.getEntityArmorInventory(entity).entrySet()) {
            Item item = armorEntry.getValue();
            if (item.getNamedTag().contains("gameapi:defense")) {
                if (item.getId() == 0) {
                    return;
                }
                Tag tag = item.getNamedTag().get("gameapi:defense");
                if (tag instanceof StringTag) {
                    String defenseFormula = ((StringTag) tag).parseValue();
                    defenseFormula =
                            defenseFormula
                                    .replace("{damage}", String.valueOf(entityDamageEvent.getDamage()))
                                    .replace("{victim_health}", String.valueOf(entity.getHealth()));

                    // 对常量进行处理
                    if (item.getNamedTag().contains("gameapi:constants")) {
                        CompoundTag constants = item.getNamedTag().getCompound("gameapi:constants");
                        for (Map.Entry<String, Tag> entry : constants.getTags().entrySet()) {
                            Tag constant = entry.getValue();
                            if (constant instanceof IntTag) {
                                defenseFormula = defenseFormula.replace(entry.getKey(), String.valueOf(((IntTag) constant).getData()));
                            } else if (constant instanceof DoubleTag) {
                                defenseFormula = defenseFormula.replace(entry.getKey(), String.valueOf(((DoubleTag) constant).getData()));
                            } else if (constant instanceof StringTag) {
                                String data = ((StringTag) constant).parseValue();
                                if (data.startsWith("random_int:")) {
                                    String[] splits = data.replace("random_int:", "").split("-");
                                    if (splits.length == 2) {
                                        int min = Integer.parseInt(splits[0]);
                                        int max = Integer.parseInt(splits[1]);
                                        defenseFormula = data.replace(entry.getKey(), String.valueOf(RandomTools.getRandom(min, max)));
                                    }
                                } else if (data.equals("random_double")) {
                                    defenseFormula = data.replace(entry.getKey(), String.valueOf(ThreadLocalRandom.current().nextDouble()));
                                } else if (data.startsWith("random_double")) {
                                    String[] splits = data.replace("random_double:", "").split("-");
                                    if (splits.length == 2) {
                                        double min = Integer.parseInt(splits[0]);
                                        double max = Integer.parseInt(splits[1]);
                                        defenseFormula = data.replace(entry.getKey(), String.valueOf(RandomTools.getRandom(min, max)));
                                    }
                                } else {
                                    defenseFormula = defenseFormula.replace(entry.getKey(), data);
                                }
                            }
                        }
                    }
                    entityDamageEvent.setDamage((float) (entityDamageEvent.getDamage() - StringFormulaCalculator.evaluate(defenseFormula)));
                } else if (tag instanceof IntTag) {
                    int dfs = item.getNamedTag().getInt("gameapi:defense");
                    baseDamage -= dfs;
                    entityDamageEvent.setDamage(baseDamage);
                }
            }
        }
    }
}
