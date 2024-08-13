package gameapi.extensions.supplyChest.item;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Utils;
import gameapi.annotation.Experimental;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Experimental
public class RandomEnchantedSupplyItem extends SupplyItem {

    public boolean hasNegative;

    public RandomEnchantedSupplyItem(Item item, double possibility) {
        this(item, possibility, true);
    }

    public RandomEnchantedSupplyItem(Item item, double possibility, boolean hasNegative) {
        super(item, possibility);
        this.hasNegative = hasNegative;
    }

    @Override
    public Item select() {
        Item newItem = this.getItem().clone();
        double random = ThreadLocalRandom.current().nextDouble();
        if (this.getRefreshCountChanceMap().isEmpty()) {
            endowRandomEnchantmentByItem(newItem);
        } else {
            for (Map.Entry<Integer, Double> entry : this.getRefreshCountChanceMap().entrySet()) {
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
        List<Enchantment> enchantments = getSupportEnchantments(randomItem, hasNegative);
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
    public List<Enchantment> getSupportEnchantments(Item item, boolean hasNegative) {
        ArrayList<Enchantment> enchantments = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.getEnchantments()) {
            if (hasNegative) {
                switch (enchantment.id) {
                    case Enchantment.ID_BINDING_CURSE:
                    case Enchantment.ID_VANISHING_CURSE:
                        continue;
                }
            }
            if (item.getId() == Item.ENCHANTED_BOOK || enchantment.canEnchant(item)) {
                enchantments.add(enchantment);
            }
        }
        return enchantments;
    }
}