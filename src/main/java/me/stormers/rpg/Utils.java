package me.stormers.rpg;

import org.bukkit.ChatColor;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Logger;


public class Utils {

    private static final Logger logger = Main.getPluginLogger();

    public static  String color(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String decolor(String text){
        return ChatColor.stripColor(text);
    }

    public static String recolor(String text){
        return text.replace(ChatColor.COLOR_CHAR,'&');
    }



    public static ItemStack createItem(Material type, int amount, boolean glow, boolean unbreakable, boolean hideUnb, String name, String... lines) {
        ItemStack item = new ItemStack(type, amount);
        ItemMeta meta = item.getItemMeta();
        if (glow) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        if (unbreakable)
            meta.setUnbreakable(true);
        if (hideUnb)
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if (name != null)
            meta.setDisplayName(color(name));
        if (lines != null) {
            List<String> lore = new ArrayList<>();
            for (String line : lines)
                lore.add(color(line));
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack enchantItem(ItemStack item, Enchantment enchant, int level) {
        item.addUnsafeEnchantment(enchant, level);
        return item;
    }

    public static ItemStack[] makeArmorSet(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        ItemStack[] armor = new ItemStack[4];
        armor[3] = helmet;
        armor[2] = chestplate;
        armor[1] = leggings;
        armor[0] = boots;
        return armor;
    }


    public static void msgPlayer(Player player, String... strings) {
        for (String string : strings)
            player.sendMessage(color(string));
    }

    public static void titlePlayer(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(color(title), color(subtitle), fadeIn, stay, fadeOut);
    }

    public static void soundPlayer(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }


}