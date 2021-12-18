package com.stefancooper.StefanMaven;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupedMaterials {
    public static List<Material> getPickaxes() {
        return new ArrayList<>(Arrays.asList(
                Material.WOODEN_PICKAXE,
                Material.STONE_PICKAXE, 
                Material.IRON_PICKAXE, 
                Material.GOLDEN_PICKAXE,
                Material.DIAMOND_PICKAXE,
                Material.NETHERITE_PICKAXE
        ));
    }

    public static List<Material> getTools() {
        return new ArrayList<>(Arrays.asList(
                 Material.WOODEN_SHOVEL,
                 Material.STONE_SHOVEL,
                 Material.IRON_SHOVEL,
                 Material.DIAMOND_SHOVEL,
                 Material.GOLDEN_SHOVEL,
                 Material.NETHERITE_SHOVEL,
                 Material.WOODEN_PICKAXE,
                 Material.STONE_PICKAXE,
                 Material.IRON_PICKAXE,
                 Material.DIAMOND_PICKAXE,
                 Material.NETHERITE_PICKAXE,
                 Material.GOLDEN_PICKAXE,
                 Material.WOODEN_AXE,
                 Material.STONE_AXE,
                 Material.IRON_AXE,
                 Material.DIAMOND_AXE,
                 Material.GOLDEN_AXE,
                 Material.NETHERITE_AXE,
                 Material.WOODEN_HOE,
                 Material.STONE_HOE,
                 Material.IRON_HOE,
                 Material.DIAMOND_HOE,
                 Material.NETHERITE_HOE,
                 Material.GOLDEN_HOE
        ));
    }

    public static List<Material> getHoes() {
        return new ArrayList<>(Arrays.asList(
                Material.WOODEN_HOE,
                Material.STONE_HOE,
                Material.IRON_HOE,
                Material.DIAMOND_HOE,
                Material.NETHERITE_HOE,
                Material.GOLDEN_HOE
        ));
    }

    public static List<Material> getXpBlocks() {
        return new ArrayList<>(Arrays.asList(
                Material.COAL_ORE,
                Material.DIAMOND_ORE,
                Material.REDSTONE_ORE,
                Material.LAPIS_ORE,
                Material.EMERALD_ORE,
                Material.DEEPSLATE_COAL_ORE,
                Material.DEEPSLATE_DIAMOND_ORE,
                Material.DEEPSLATE_REDSTONE_ORE,
                Material.DEEPSLATE_LAPIS_ORE,
                Material.DEEPSLATE_EMERALD_ORE,
                Material.NETHER_QUARTZ_ORE,
                Material.NETHER_GOLD_ORE
        ));
    }

    public static List<Material> getXpBlocksWithAutoSmelt() {
        return new ArrayList<>(Arrays.asList(
                Material.IRON_ORE,
                Material.COPPER_ORE,
                Material.GOLD_ORE,
                Material.DEEPSLATE_IRON_ORE,
                Material.DEEPSLATE_COPPER_ORE,
                Material.DEEPSLATE_GOLD_ORE
        ));
    }
}
