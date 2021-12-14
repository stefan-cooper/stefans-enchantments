package com.stefancooper.StefanMaven;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static java.lang.Math.random;

public final class Main extends JavaPlugin implements Listener { // Create the class and extend the JavaPlugin so we can implement internal methods.
    public void onEnable() { // This is called when the plugin is loaded into the server.
        CustomEnchants.register();

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    public void onDisable() { // This is called when the plugin is unloaded from the server.

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("magnet")) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            ItemStack item = new ItemStack (Material.STONE_PICKAXE);
            item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 4);
            addMagnetToItem(item, true);
            player.getInventory().addItem(item);

            return true;
        }
        return true;
    }

    @EventHandler()
    public void onBlockBreak(BlockBreakEvent event) {
        if (HOLDING_MAGNET(event)) {
            if (event.getPlayer().getInventory().getItemInMainHand() == null) return;
            if (!event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) return;
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
            if (event.getPlayer().getInventory().firstEmpty() == -1) return;
            if (event.getBlock().getState() instanceof Container) return;

            event.setDropItems(false);
            Player player = event.getPlayer();
            Block block = event.getBlock();

            Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
            if (drops.isEmpty()) return;
            player.getInventory().addItem(drops.iterator().next());
        }
    }

    @EventHandler()
    public void addCustomEnchantFromEnchantTable(EnchantItemEvent e) {
        Random rn = new Random();
        if (e.getExpLevelCost() > 27 && isTool(e.getItem().getType()) && rn.nextInt(11) < 2) {
            ItemStack item = e.getItem();
            addMagnetToItem(item, true);
        } else if (e.getExpLevelCost() > 27 && isBook(e.getItem().getType()) && rn.nextInt(20) < 2) {
            ItemStack item = e.getItem();
            addMagnetToItem(item, true);
        }
    }

    @EventHandler()
    public void addCustomEnchantFromAnvil(PrepareAnvilEvent e) {
        try {
            if( (e.getInventory().getContents().length > 0 && e.getInventory().getContents()[0] != null && e.getInventory().getContents()[0].getItemMeta().hasEnchant(CustomEnchants.MAGNET)) ||
                (e.getInventory().getContents().length > 1 && e.getInventory().getContents()[1] != null && e.getInventory().getContents()[1].getItemMeta().hasEnchant(CustomEnchants.MAGNET))) {
                ItemStack item = e.getResult();
                item.removeEnchantment(CustomEnchants.MAGNET);
                addMagnetToItem(item, false);
                e.setResult(item);
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private void addMagnetToItem(ItemStack item, boolean withLore) {
        item.addUnsafeEnchantment(CustomEnchants.MAGNET, 1);
        if (withLore) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GRAY + "Magnet");
            if (meta.hasLore()) {
                for (String l : meta.getLore()) {
                    lore.add(l);
                }
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    private boolean isTool(Material item){
        return item.equals(Material.WOODEN_SHOVEL)
                || item.equals(Material.STONE_SHOVEL)
                || item.equals(Material.IRON_SHOVEL)
                || item.equals(Material.DIAMOND_SHOVEL)
                || item.equals(Material.GOLDEN_SHOVEL)
                || item.equals(Material.NETHERITE_SHOVEL)
                || item.equals(Material.WOODEN_PICKAXE)
                || item.equals(Material.STONE_PICKAXE)
                || item.equals(Material.IRON_PICKAXE)
                || item.equals(Material.DIAMOND_PICKAXE)
                || item.equals(Material.NETHERITE_PICKAXE)
                || item.equals(Material.GOLDEN_PICKAXE)
                || item.equals(Material.WOODEN_AXE)
                || item.equals(Material.STONE_AXE)
                || item.equals(Material.IRON_AXE)
                || item.equals(Material.DIAMOND_AXE)
                || item.equals(Material.GOLDEN_AXE)
                || item.equals(Material.NETHERITE_AXE);
    }

    private boolean isBook(Material item){
        return item.equals(Material.BOOK);
    }

    private boolean HOLDING_MAGNET(BlockBreakEvent event) {
        return event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.MAGNET);
    }
}