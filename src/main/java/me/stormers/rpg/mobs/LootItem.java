package me.stormers.rpg.mobs;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class LootItem {
    private final ItemStack item;

    private int min = 1;

    private int max = 1;

    private final double dropRate;

    private static final Random randomiser = new Random();

    public LootItem(ItemStack item, double dropRate) {
        this.item = item;
        this.dropRate = dropRate;
    }

    public LootItem(ItemStack item, int min, int max, double dropRate) {
        this.item = item;
        this.min = min;
        this.max = max;
        this.dropRate = dropRate;
    }

    public void tryDropItem(Location loc) {
        if (Math.random() * 101.0D > this.dropRate)
            return;
        int amount = randomiser.nextInt(this.max - this.min + 1) + this.min;
        if (amount == 0)
            return;
        ItemStack item = this.item.clone();
        item.setAmount(amount);
        loc.getWorld().dropItemNaturally(loc, item);
    }
}