package com.stefancooper.StefanMaven;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.Ageable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
            addCustomEnchantToItem(item, CustomEnchants.MAGNET, true, 0);
            player.getInventory().addItem(item);
        } else if (label.equalsIgnoreCase("autoSmelt")) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            ItemStack item = new ItemStack (Material.STONE_PICKAXE);
            item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 4);
            addCustomEnchantToItem(item, CustomEnchants.AUTO_SMELT, true, 0);
            player.getInventory().addItem(item);
        } else if (label.equalsIgnoreCase("explosiveBow")) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            ItemStack item = new ItemStack (Material.BOW);
            item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 4);
            addCustomEnchantToItem(item, CustomEnchants.EXPLOSIVE, true, 0);
            player.getInventory().addItem(item);
        } else if (label.equalsIgnoreCase("swiftPlanter")) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            ItemStack item = new ItemStack (Material.STONE_HOE);
            addCustomEnchantToItem(item, CustomEnchants.SWIFT_PLANTER, true, 0);
            player.getInventory().addItem(item);
        } else if (label.equalsIgnoreCase("hunterCompass")) {
            if (!(sender instanceof Player)) return true;
            Player target = Bukkit.getPlayer(args[0]);
            Player player = (Player) sender;
            if (target == null) {
                player.sendMessage("Player not found, ensure usage is " + ChatColor.YELLOW + "/hunterCompass [targetPlayer]");
                return true;
            }
            ItemStack item = new ItemStack (Material.COMPASS);
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.RED + "Pointing at " + target.getName());
            if (meta.hasLore()) {
                for (String l : meta.getLore()) {
                    lore.add(l);
                }
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);
            player.setCompassTarget(target.getLocation());
        } else if (label.equalsIgnoreCase("bigBrainMining")) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            ItemStack item = new ItemStack (Material.STONE_PICKAXE);
            item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 4);
            addCustomEnchantToItem(item, CustomEnchants.BIG_BRAIN_MINING, true, 5);
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
    public void replantSeeds(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = event.getBlock().getType();

        if (event.getPlayer().getInventory().getItemInMainHand() == null) return;
        if (!event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;

        if (HOLDING_SWIFT_PLANTER(event) && FULLY_GROWN_CROPS(event)) {
            if (blockType.equals(Material.WHEAT) || blockType.equals(Material.POTATOES) || blockType.equals(Material.CARROTS)) {
                event.setCancelled(true);
                block.breakNaturally();
                block.setType(blockType);
            }
        }
    }

    @EventHandler()
    public void extraXPFromMining(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        if (event.getPlayer().getInventory().getItemInMainHand() == null) return;
        if (!event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;

        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
        if (drops.isEmpty()) return;

        if (HOLDING_BIG_BRAIN_MINING(event)) {
            int enchantLevel = event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getEnchantLevel(CustomEnchants.BIG_BRAIN_MINING);
            if (enchantLevel == 5) {
                event.setExpToDrop((event.getExpToDrop() + 1) * 17);
            } else if (enchantLevel == 4) {
                event.setExpToDrop((event.getExpToDrop() + 1) * 12);
            } else if (enchantLevel == 3) {
                event.setExpToDrop((event.getExpToDrop() + 1) * 8);
            } else if (enchantLevel == 2) {
                event.setExpToDrop((event.getExpToDrop() + 1) * 4);
            } else if (enchantLevel == 1) {
                event.setExpToDrop((event.getExpToDrop() + 1) * 2);
            }
        }

    }

    @EventHandler()
    public void addCustomEnchantFromEnchantTable(EnchantItemEvent e) {
        Random rn = new Random();

        ItemStack item = e.getItem();
        boolean isBook = isBook(e.getItem().getType());

        // Add Magnet
        if (e.getExpLevelCost() > 27 && isTool(item.getType()) && rn.nextInt(11) < 2) {
            addCustomEnchantToItem(item, CustomEnchants.MAGNET, true, 0);
        } else if (e.getExpLevelCost() > 27 && isBook && rn.nextInt(20) < 2) {
            addCustomEnchantToItem(item, CustomEnchants.MAGNET, true, 0);
        }

        // Add AutoSmelt
        if (e.getExpLevelCost() > 27 && isPickaxe(item.getType()) && rn.nextInt(11) < 2 && !item.containsEnchantment(EnchantmentWrapper.SILK_TOUCH)) {
            addCustomEnchantToItem(item, CustomEnchants.AUTO_SMELT, true, 0);
        } else if (e.getExpLevelCost() > 27 && isBook && rn.nextInt(20) < 2 && !item.containsEnchantment(EnchantmentWrapper.SILK_TOUCH)) {
            addCustomEnchantToItem(item, CustomEnchants.AUTO_SMELT, true, 0);
        }

        // Make mending possible
        if (e.getExpLevelCost() == 30 && rn.nextInt(51) < 2) {
            item.addEnchantment(EnchantmentWrapper.MENDING, 1);
        }

        // Add Explosive Arrows
        if (e.getExpLevelCost() == 30 && isBow(item.getType()) && rn.nextInt(21) < 2) {
            addCustomEnchantToItem(item, CustomEnchants.EXPLOSIVE, true, 0);
        } else if (e.getExpLevelCost() == 30 && isBook && rn.nextInt(41) < 2) {
            addCustomEnchantToItem(item, CustomEnchants.EXPLOSIVE, true, 0);
        }

        // Add Swift Planter
        if (e.getExpLevelCost() == 24 && isHoe(item.getType()) && rn.nextInt(11) < 2) {
            addCustomEnchantToItem(item, CustomEnchants.SWIFT_PLANTER, true, 0);
        } else if (e.getExpLevelCost() == 30 && isBook && rn.nextInt(31) < 2) {
            addCustomEnchantToItem(item, CustomEnchants.SWIFT_PLANTER, true, 0);
        }

        // Add Big Brain Mining
        if (e.getExpLevelCost() == 30 && isPickaxe(item.getType()) && rn.nextInt(51) < 2 ) {
            addCustomEnchantToItem(item, CustomEnchants.BIG_BRAIN_MINING, true, 4);
        } else if (e.getExpLevelCost() > 27 && isPickaxe(item.getType()) && rn.nextInt(31) < 2 ) {
            addCustomEnchantToItem(item, CustomEnchants.BIG_BRAIN_MINING, true, 3);
        } else if (e.getExpLevelCost() > 20 && isPickaxe(item.getType()) && rn.nextInt(31) < 2 ) {
            addCustomEnchantToItem(item, CustomEnchants.BIG_BRAIN_MINING, true, 2);
        } else if (e.getExpLevelCost() < 20 && isPickaxe(item.getType()) && rn.nextInt(11) < 2 ) {
            addCustomEnchantToItem(item, CustomEnchants.BIG_BRAIN_MINING, true, 1);
        } else if (e.getExpLevelCost() == 30 && isBook && rn.nextInt(101) < 2) {
            addCustomEnchantToItem(item, CustomEnchants.BIG_BRAIN_MINING, true, 4);
        } else if (e.getExpLevelCost() > 20 && isBook && rn.nextInt(81) < 2) {
            addCustomEnchantToItem(item, CustomEnchants.BIG_BRAIN_MINING, true, 3);
        } else if (e.getExpLevelCost() < 20 && isBook && rn.nextInt(21) < 2) {
            addCustomEnchantToItem(item, CustomEnchants.BIG_BRAIN_MINING, true, 2);
        } else if (e.getExpLevelCost() < 20 && isBook && rn.nextInt(11) < 2) {
            addCustomEnchantToItem(item, CustomEnchants.BIG_BRAIN_MINING, true, 1);
        }
    }

    @EventHandler()
    public void handleAnvils(PrepareAnvilEvent e) {
        List<Enchantment> noConflicts = new ArrayList<>();
        handleAnvilEnchantment(e, CustomEnchants.BIG_BRAIN_MINING, true, noConflicts );
        handleAnvilEnchantment(e, CustomEnchants.SWIFT_PLANTER, false, noConflicts);
        handleAnvilEnchantment(e, CustomEnchants.MAGNET, false, noConflicts);
        handleAnvilEnchantment(e, CustomEnchants.AUTO_SMELT, false, new ArrayList<Enchantment>(Arrays.asList(EnchantmentWrapper.SILK_TOUCH)));
        handleAnvilEnchantment(e, CustomEnchants.EXPLOSIVE, false, noConflicts);
    }

    private void handleAnvilEnchantment(PrepareAnvilEvent e, Enchantment enchantment, boolean withLevels, List<Enchantment> conflicts) {
        try {
            boolean anvilInventoryFull = e.getInventory().getContents().length > 1
                    && e.getInventory().getContents()[0] != null
                    && e.getInventory().getContents()[1] != null;

            if (anvilInventoryFull) {
                ItemStack item = e.getResult();
                ItemStack leftItem = e.getInventory().getContents()[0];
                ItemStack rightItem = e.getInventory().getContents()[1];

                boolean leftItemEnchanted = leftItem.getItemMeta().hasEnchant(enchantment);
                boolean rightItemEnchanted = rightItem.getItemMeta().hasEnchant(enchantment);
                int enchantLevel = 0;

                // Upgrading Enchantment
                if (leftItemEnchanted && rightItemEnchanted) {
                    if (withLevels) {
                        int max = enchantment.getMaxLevel();
                        int leftLevel = leftItem.getItemMeta().getEnchantLevel(enchantment);
                        int rightLevel = rightItem.getItemMeta().getEnchantLevel(enchantment);
                        enchantLevel = Math.min(Math.min(leftLevel, rightLevel) + 1, max);
                        if (leftLevel == rightLevel) removeFromLore(item, (ChatColor.GRAY + enchantment.getName() + " " + levelMapForLore(enchantLevel - 1)));
                        else if (rightLevel < leftLevel) removeFromLore(item, (ChatColor.GRAY + enchantment.getName() + " " + levelMapForLore(enchantLevel)));
                        else if (leftLevel < rightLevel) removeFromLore(item, (ChatColor.GRAY + enchantment.getName() + " " + levelMapForLore(leftLevel)));
                    } else {
                        removeFromLore(item, (ChatColor.GRAY + enchantment.getName()));
                    }
                    item.removeEnchantment(enchantment);
                    addCustomEnchantToItem(item, enchantment, true, enchantLevel);

                // Adding enchantment to item
                } else if (leftItemEnchanted || rightItemEnchanted) {
                    if (withLevels) {
                        if (leftItemEnchanted) enchantLevel = leftItem.getItemMeta().getEnchantLevel(enchantment);
                        else enchantLevel = rightItem.getItemMeta().getEnchantLevel(enchantment);
                    }
                    item.removeEnchantment(enchantment);
                    addCustomEnchantToItem(item, enchantment, rightItemEnchanted, enchantLevel);
                }

                // Handle Conflicting Enchantments
                for (Enchantment conflict : conflicts) {
                    if (leftItem.getItemMeta().hasEnchant(enchantment) && rightItem.getItemMeta().hasEnchant(conflict)) {
                        item.removeEnchantment(conflict);
                    } else if (leftItem.getItemMeta().hasEnchant(conflict) && rightItem.getItemMeta().hasEnchant(enchantment)) {
                        item.removeEnchantment(enchantment);
                        if (withLevels) {
                            removeFromLore(item, (ChatColor.GRAY + enchantment.getName() + " " + levelMapForLore(enchantLevel)));
                        } else {
                            removeFromLore(item, (ChatColor.GRAY + enchantment.getName() + " "));
                        }
                    }
                }

                e.setResult(item);
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @EventHandler()
    public void explodeArrow(EntityDamageByEntityEvent e) throws InterruptedException {
        if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
            Player player = ((Player) ((Projectile) e.getDamager()).getShooter()).getPlayer();
            if (player.getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.EXPLOSIVE)) {
                Location location = (Location) e.getDamager().getLocation();
                location.getWorld().createExplosion(location, 3, false);
            }
        }
    }

    private void addCustomEnchantToItem(ItemStack item, Enchantment customEnchant, boolean withLore, int level) {
        if (level == 0) {
            item.addUnsafeEnchantment(customEnchant, 1);
        } else {
            item.addUnsafeEnchantment(customEnchant, level);
        }
        if (withLore) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GRAY + customEnchant.getName() + " " + levelMapForLore(level));
            if (meta.hasLore()) {
                for (String l : meta.getLore()) {
                    lore.add(l);
                }
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    private void addToItemLore(ItemStack item, String newLore) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + newLore);
        if (meta.hasLore()) {
            for (String l : meta.getLore()) {
                lore.add(l);
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private void removeFromLore(ItemStack item, String oldLore) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = item.getItemMeta().getLore();
        Bukkit.getConsoleSender().sendMessage("old: " + lore.toString() + " vs " + oldLore);

        lore.remove(oldLore);
        Bukkit.getConsoleSender().sendMessage("new: " + lore.toString());
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private String levelMapForLore(int level) {
        switch (level) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            default:
                return "";
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

    private boolean isBow(Material item){
        return item.equals(Material.BOW);
    }

    private boolean isHoe(Material item){
        return item.equals(Material.WOODEN_HOE)
                || item.equals(Material.STONE_HOE)
                || item.equals(Material.IRON_HOE)
                || item.equals(Material.DIAMOND_HOE)
                || item.equals(Material.NETHERITE_HOE)
                || item.equals(Material.GOLDEN_HOE);
    }

    private boolean HOLDING_MAGNET(BlockBreakEvent event) {
        return event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null && event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.MAGNET);
    }

    private boolean HOLDING_AUTOSMELT(BlockBreakEvent event) {
        return event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null && event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.AUTO_SMELT);
    }

    private boolean HOLDING_BIG_BRAIN_MINING(BlockBreakEvent event) {
        return event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null && event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.BIG_BRAIN_MINING);
    }

    private boolean HOLDING_SWIFT_PLANTER(BlockBreakEvent event) {
        return event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null && event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.SWIFT_PLANTER);
    }

    private boolean FULLY_GROWN_CROPS(BlockBreakEvent event) {
        if (event.getBlock().getBlockData() instanceof Ageable) {
            Ageable age = ((Ageable) event.getBlock().getBlockData());
            return age.getAge() == age.getMaximumAge();
        } else {
            return false;
        }
    }
}