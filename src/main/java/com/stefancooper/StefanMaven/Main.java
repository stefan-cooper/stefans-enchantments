package com.stefancooper.StefanMaven;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static com.stefancooper.StefanMaven.SmeltMap.getMappedSmeltedItem;


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
            addCustomEnchantToItem(item, CustomEnchants.MAGNET, true);
            player.getInventory().addItem(item);
        } else if (label.equalsIgnoreCase("autoSmelt")) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            ItemStack item = new ItemStack (Material.STONE_PICKAXE);
            item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 4);
            addCustomEnchantToItem(item, CustomEnchants.AUTO_SMELT, true);
            player.getInventory().addItem(item);
        }
        return true;
    }

    @EventHandler()
    public void onBlockBreak(BlockBreakEvent event) {
        if (HOLDING_MAGNET(event) || HOLDING_AUTOSMELT(event)) {
            Block block = event.getBlock();
            Player player = event.getPlayer();
            Location location = event.getBlock().getLocation();

            if (event.getPlayer().getInventory().getItemInMainHand() == null) return;
            if (!event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) return;
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
            if (event.getPlayer().getInventory().firstEmpty() == -1) {
                boolean spaceAvailable = false;
                for (ItemStack item : event.getPlayer().getInventory()) {
                    Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
                    ItemStack drop = drops.iterator().next();
                    if (HOLDING_AUTOSMELT(event)) drop = getMappedSmeltedItem(drop.getType());
                    if (item != null && drop.getType() == item.getType() && item.getAmount() < item.getMaxStackSize()) {
                        spaceAvailable = true;
                        break;
                    }
                }
                if (HOLDING_AUTOSMELT(event) && !spaceAvailable) location.getWorld().dropItemNaturally(location, getMappedSmeltedItem(block.getType()));
                else if (!spaceAvailable) return;
            }
            if (event.getBlock().getState() instanceof Container) return;

            event.setDropItems(false);

            Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
            if (drops.isEmpty()) return;

            if (HOLDING_AUTOSMELT(event)) {
                for (ItemStack drop : drops) {
                    Material dropped = drop.getType();
                    if (HOLDING_MAGNET(event)) {
                        player.getInventory().addItem(getMappedSmeltedItem(dropped));
                    } else {
                        location.getWorld().dropItemNaturally(location, getMappedSmeltedItem(dropped));
                    }
                }
            } else {
                player.getInventory().addItem(drops.iterator().next());
            }
        }
    }

    @EventHandler()
    public void addCustomEnchantFromEnchantTable(EnchantItemEvent e) {
        Random rn = new Random();

        // Adding Magnet
        if (e.getExpLevelCost() > 27 && isTool(e.getItem().getType()) && rn.nextInt(11) < 2) {
            ItemStack item = e.getItem();
            addCustomEnchantToItem(item, CustomEnchants.MAGNET, true);
        } else if (e.getExpLevelCost() > 27 && isBook(e.getItem().getType()) && rn.nextInt(20) < 2) {
            ItemStack item = e.getItem();
            addCustomEnchantToItem(item, CustomEnchants.MAGNET, true);
        }

        // Adding AutoSmelt
        if (e.getExpLevelCost() > 27 && isPickaxe(e.getItem().getType()) && rn.nextInt(11) < 2 && !e.getItem().containsEnchantment(EnchantmentWrapper.SILK_TOUCH)) {
            ItemStack item = e.getItem();
            addCustomEnchantToItem(item, CustomEnchants.AUTO_SMELT, true);
        } else if (e.getExpLevelCost() > 27 && isBook(e.getItem().getType()) && rn.nextInt(20) < 2 && !e.getItem().containsEnchantment(EnchantmentWrapper.SILK_TOUCH)) {
            ItemStack item = e.getItem();
            addCustomEnchantToItem(item, CustomEnchants.AUTO_SMELT, true);
        }

        // Make mending possible
        if (e.getExpLevelCost() == 30 && rn.nextInt(51) < 2) {
            ItemStack item = e.getItem();
            item.addEnchantment(EnchantmentWrapper.MENDING, 1);
        }
    }

    @EventHandler()
    public void addCustomEnchantFromAnvil(PrepareAnvilEvent e) {
        try {
            if( (e.getInventory().getContents().length > 0 && e.getInventory().getContents()[0] != null && e.getInventory().getContents()[0].getItemMeta().hasEnchant(CustomEnchants.MAGNET)) ||
                (e.getInventory().getContents().length > 1 && e.getInventory().getContents()[1] != null && e.getInventory().getContents()[1].getItemMeta().hasEnchant(CustomEnchants.MAGNET)) ||
                (e.getInventory().getContents().length > 0 && e.getInventory().getContents()[0] != null && e.getInventory().getContents()[0].getItemMeta().hasEnchant(CustomEnchants.AUTO_SMELT)) ||
                (e.getInventory().getContents().length > 1 && e.getInventory().getContents()[1] != null && e.getInventory().getContents()[1].getItemMeta().hasEnchant(CustomEnchants.AUTO_SMELT)))
            {
                boolean anvilInventoryFull = e.getInventory().getContents().length > 1
                        && e.getInventory().getContents()[0] != null
                        && e.getInventory().getContents()[1] != null;

                if (anvilInventoryFull) {
                    boolean hasAutoSmeltFirst = e.getInventory().getContents()[0].getItemMeta().hasEnchant(CustomEnchants.AUTO_SMELT);
                    boolean hasAutoSmeltSecond = e.getInventory().getContents()[1].getItemMeta().hasEnchant(CustomEnchants.AUTO_SMELT);
                    boolean hasMagnetFirst = e.getInventory().getContents()[0].getItemMeta().hasEnchant(CustomEnchants.MAGNET);
                    boolean hasMagnetSecond = e.getInventory().getContents()[1].getItemMeta().hasEnchant(CustomEnchants.MAGNET);
                    boolean hasAutoSmelt = hasAutoSmeltFirst || hasAutoSmeltSecond;
                    boolean hasMagnet = hasMagnetFirst || hasMagnetSecond;

                    ItemStack item = e.getResult();

                    if (hasAutoSmelt && item.containsEnchantment(EnchantmentWrapper.SILK_TOUCH)) {
                        item.removeEnchantment(EnchantmentWrapper.SILK_TOUCH);
                    } else if (hasAutoSmelt){
                        item.removeEnchantment(CustomEnchants.AUTO_SMELT);
                        addCustomEnchantToItem(item, CustomEnchants.AUTO_SMELT, hasAutoSmeltSecond);
                    }

                    if (hasMagnet) {
                        item.removeEnchantment(CustomEnchants.MAGNET);
                        addCustomEnchantToItem(item, CustomEnchants.MAGNET, hasMagnetSecond);
                    }
                    e.setResult(item);
                }


            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private void addCustomEnchantToItem(ItemStack item, Enchantment customEnchant, boolean withLore) {
        item.addUnsafeEnchantment(customEnchant, 1);
        if (withLore) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GRAY + customEnchant.getName());
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

    private boolean isPickaxe(Material item) {
        return item.equals(Material.WOODEN_PICKAXE)
                || item.equals(Material.STONE_PICKAXE)
                || item.equals(Material.IRON_PICKAXE)
                || item.equals(Material.DIAMOND_PICKAXE)
                || item.equals(Material.NETHERITE_PICKAXE)
                || item.equals(Material.GOLDEN_PICKAXE);
    }

    private boolean isBook(Material item){
        return item.equals(Material.BOOK);
    }

    private boolean HOLDING_MAGNET(BlockBreakEvent event) {
        return event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.MAGNET);
    }

    private boolean HOLDING_AUTOSMELT(BlockBreakEvent event) {
        return event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.AUTO_SMELT);
    }
}