package com.stefancooper.StefanMaven;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomEnchants {
    public static final Enchantment MAGNET = new EnchantmentWrapper("magnet", "Magnet", 1);
    public static final Enchantment AUTO_SMELT = new EnchantmentWrapper("autosmelt", "AutoSmelt", 1);
    public static final Enchantment EXPLOSIVE = new EnchantmentWrapper("expolosive", "Explosive", 1);

    public static void register() {
        boolean magnetRegistered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(CustomEnchants.MAGNET);
        boolean autoSmeltRegistered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(CustomEnchants.AUTO_SMELT);
        boolean explosiveRegistered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(CustomEnchants.EXPLOSIVE);

        if (!magnetRegistered) registerEnchantment(MAGNET);
        if (!autoSmeltRegistered) registerEnchantment(AUTO_SMELT);
        if (!explosiveRegistered) registerEnchantment(EXPLOSIVE);
    }

    public static void registerEnchantment(Enchantment enchantment) {
        boolean registered = true;
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchantment);
        } catch (Exception e) {
            registered = false;
            e.printStackTrace();
        }

        if (registered) {
            System.out.println(enchantment.getName() + " registered");
        }
    }
}
