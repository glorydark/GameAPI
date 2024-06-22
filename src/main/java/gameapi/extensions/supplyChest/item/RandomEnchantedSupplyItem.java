package gameapi.extensions.supplyChest.item;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Utils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomEnchantedSupplyItem extends SupplyItem {

    protected final LinkedHashMap<Integer, Double> possibilityMap = new LinkedHashMap<>();

    public RandomEnchantedSupplyItem(Item item, double possibility) {
        super(item, possibility);
    }

    public void addEnchantmentPossibility(int count, double possibility) {
        this.possibilityMap.put(count, possibility);
    }

    @Override
    public Item getItem() {
        Item newItem = this.item.clone();
        double random = ThreadLocalRandom.current().nextDouble();
        if (possibilityMap.isEmpty()) {
            endowRandomEnchantmentByItem(newItem);
        } else {
            for (Map.Entry<Integer, Double> entry : possibilityMap.entrySet()) {
                if (random < entry.getValue()) {
                    for (int i = 0; i < entry.getKey(); i++) {
                        endowRandomEnchantmentByItem(newItem);
                    }
                }
            }
        }
        return newItem;
    }

    public void endowRandomEnchantmentByItem(Item randomItem) {
        Random random = ThreadLocalRandom.current();
        List<Enchantment> enchantments = getSupportEnchantments(randomItem);
        if (!enchantments.isEmpty()) {
            Enchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
            double randomDouble = random.nextDouble();
            if (randomDouble < 0.05) { //减少高等级附魔概率
                enchantment.setLevel(Utils.rand(Math.min(3, enchantment.getLevel()), enchantment.getMaxLevel()));
            } else {
                enchantment.setLevel(Utils.rand(1, Math.min(2, enchantment.getMaxLevel())));
            }
            randomItem.addEnchantment(enchantment);
        }
    }

    /**
     * 根据物品获得支持的附魔
     *
     * @param item 物品
     * @return 支持的附魔
     */
    public List<Enchantment> getSupportEnchantments(Item item) {
        ArrayList<Enchantment> enchantments = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.getEnchantments()) {
            if (item.getId() == Item.ENCHANTED_BOOK || enchantment.canEnchant(item)) {
                enchantments.add(enchantment);
            }
        }
        return enchantments;
    }
}