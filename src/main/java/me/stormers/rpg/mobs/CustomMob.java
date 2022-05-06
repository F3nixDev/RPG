package me.stormers.rpg.mobs;

import java.util.Arrays;
import java.util.List;

import me.stormers.rpg.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
public enum CustomMob {
    DESERT_RISEN("&6Desert Risen", 15.0D, 60.0D, EntityType.HUSK, null, null, new LootItem(Utils.createItem(Material.ROTTEN_FLESH, 1, false, false, false, "&fPreserved Flesh", "&7A preserved flesh from a rotting corpse", "&7Not sure what you'd want this for, though", "&7", "&9Foodstuff"), 1, 3, 100.0D)),
    SKELETAL_MAGE("&dSkeletal Mage", 20.0D, 15.0D, EntityType.SKELETON, Utils.createItem(Material.BONE, 1, true, false, false, null), Utils.makeArmorSet(new ItemStack(Material.IRON_HELMET), null, null, null), new LootItem(Utils.createItem(Material.BONE, 1, true, false, false, "&dBone Wand", "&7A wand made from skeletal bones"), 30.0D), new LootItem(new ItemStack(Material.BONE), 1, 3, 100.0D)),
    ZOMBIE_SQUIRE("&bZombie Squire", 20.0D, 12.0D, EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD), Utils.makeArmorSet(new ItemStack(Material.CHAINMAIL_HELMET), new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.IRON_BOOTS)), new LootItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), 35.0D), new LootItem(new ItemStack(Material.CHAINMAIL_LEGGINGS), 35.0D), new LootItem(new ItemStack(Material.CHAINMAIL_HELMET), 35.0D), new LootItem(new ItemStack(Material.IRON_BOOTS), 25.0D), new LootItem(new ItemStack(Material.IRON_SWORD), 40.0D)),
    CHARRED_ARCHER("&8Charred Archer", 50.0D, 3.0D, EntityType.WITHER_SKELETON, Utils.enchantItem(new ItemStack(Material.BOW), Enchantment.ARROW_KNOCKBACK, 2), null, new LootItem(Utils.enchantItem(Utils.enchantItem(Utils.createItem(Material.BOW, 1, false, false, false, "&cBurnt Bow", "&7This bow is burnt to a crisp but remains intact", "&8due to special enchantments"), Enchantment.ARROW_FIRE, 1), Enchantment.ARROW_KNOCKBACK, 2), 100.0D), new LootItem(new ItemStack(Material.BONE), 1, 5, 100.0D));

    private final String name;

    private final double maxHealth;

    private final double spawnChance;

    private final EntityType type;

    private final ItemStack mainItem;

    private final ItemStack[] armor;

    private final List<LootItem> lootTable;

    CustomMob(String name, double maxHealth, double spawnChance, EntityType type, ItemStack mainItem, ItemStack[] armor, LootItem... lootItems) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.spawnChance = spawnChance;
        this.type = type;
        this.mainItem = mainItem;
        this.armor = armor;
        this.lootTable = Arrays.asList(lootItems);
    }

    public LivingEntity spawn(Location location) {
        LivingEntity entity = (LivingEntity)location.getWorld().spawnEntity(location, this.type);
        entity.setCustomNameVisible(true);
        entity.setCustomName(Utils.color(this.name + " &r&c" + (int)this.maxHealth + "/" + (int)this.maxHealth + ""));
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(this.maxHealth);
        entity.setHealth(this.maxHealth);
        EntityEquipment inv = entity.getEquipment();
        if (this.armor != null)
            inv.setArmorContents(this.armor);
        inv.setHelmetDropChance(0.0F);
        inv.setChestplateDropChance(0.0F);
        inv.setLeggingsDropChance(0.0F);
        inv.setBootsDropChance(0.0F);
        inv.setItemInMainHand(this.mainItem);
        inv.setItemInMainHandDropChance(0.0F);
        return entity;
    }

    public void tryDropLoot(Location location) {
        for (LootItem item : this.lootTable)
            item.tryDropItem(location);
    }

    public String getName() {
        return this.name;
    }

    public double getMaxHealth() {
        return this.maxHealth;
    }

    public double getSpawnChance() {
        return this.spawnChance;
    }
}