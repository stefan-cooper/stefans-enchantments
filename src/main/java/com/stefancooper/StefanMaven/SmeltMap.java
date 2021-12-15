package com.stefancooper.StefanMaven;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SmeltMap {
    public static Map<Material, Material> getSmeltMap() {
        Map<Material, Material> smeltMap = new HashMap<>();
        smeltMap.put(Material.RAW_IRON, Material.IRON_INGOT);
        smeltMap.put(Material.RAW_GOLD, Material.GOLD_INGOT);
        smeltMap.put(Material.RAW_COPPER, Material.COPPER_INGOT);
        smeltMap.put(Material.COBBLESTONE, Material.STONE);
        smeltMap.put(Material.ACACIA_LOG, Material.CHARCOAL);
        smeltMap.put(Material.BIRCH_LOG, Material.CHARCOAL);
        smeltMap.put(Material.DARK_OAK_LOG, Material.CHARCOAL);
        smeltMap.put(Material.JUNGLE_LOG, Material.CHARCOAL);
        smeltMap.put(Material.OAK_LOG, Material.CHARCOAL);
        smeltMap.put(Material.SPRUCE_LOG, Material.CHARCOAL);
        smeltMap.put(Material.SAND, Material.GLASS);
        smeltMap.put(Material.CLAY, Material.TERRACOTTA);
        smeltMap.put(Material.NETHERRACK, Material.NETHER_BRICK);
        smeltMap.put(Material.CACTUS, Material.GREEN_DYE);
        return smeltMap;
    }

    public static ItemStack getMappedSmeltedItem(Material dropped) {
        if (getSmeltMap().containsKey(dropped)) {
            return new ItemStack(getSmeltMap().get(dropped));
        } else {
            return new ItemStack(dropped);
        }
    }
}
