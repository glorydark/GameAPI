package gameapi.extensions.supplyChest.item;

import cn.nukkit.item.Item;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SupplyItem extends AbstractSupplyItem {

    protected final double possibility; // 0-1

    protected LinkedHashMap<Integer, Double> refreshCountChanceMap = new LinkedHashMap<>();

    public SupplyItem(Item item, double possibility) {
        super(item);
        this.possibility = possibility;
    }

    public double getPossibility() {
        return possibility;
    }

    @Override
    public Item select() {
        Item newItem = this.getItem().clone();
        if (this.refreshCountChanceMap.isEmpty()) {
            return newItem;
        } else {
            double random = ThreadLocalRandom.current().nextDouble();
            for (Map.Entry<Integer, Double> entry : this.refreshCountChanceMap.entrySet()) {
                if (random < entry.getValue()) {
                    newItem.setCount(entry.getKey());
                    return newItem;
                }
            }
        }
        return newItem;
    }

    public void addRefreshCountChanceMap(int count, double possibility) {
        this.refreshCountChanceMap.put(count, possibility);
    }
}