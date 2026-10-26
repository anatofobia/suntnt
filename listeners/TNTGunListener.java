package ru.loper.suntnt.listeners;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import ru.loper.suncore.api.items.ItemBuilder;
import ru.loper.suntnt.SunTNT;
import ru.loper.suntnt.config.TNTConfigManager;

public class TNTGunListener implements Listener {
   private final SunTNT plugin;
   private final TNTConfigManager configManager;

   @EventHandler
   public void onHit(ProjectileHitEvent event) {
      Projectile var3 = event.getEntity();
      if (var3 instanceof Snowball snowball) {
         if (snowball.hasMetadata("TNTGunProjectile")) {
            if (event.getHitBlock() == null) {
               event.setCancelled(true);
            } else {
               for(Entity passenger : snowball.getPassengers()) {
                  if (passenger instanceof TNTPrimed) {
                     TNTPrimed tntPrimed = (TNTPrimed)passenger;
                     tntPrimed.setFuseTicks(0);
                  }
               }

               snowball.remove();
            }
         }
      }
   }

   @EventHandler
   public void onDamage(EntityDamageEvent event) {
      Entity var3 = event.getEntity();
      if (var3 instanceof Snowball snowball) {
         if (snowball.hasMetadata("TNTGunProjectile")) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onGunPlace(BlockPlaceEvent event) {
      ItemStack itemStack = event.getItemInHand();
      if (this.configManager.isTNTGunItem(itemStack)) {
         Block block = event.getBlockPlaced();
         block.setMetadata("TNTGun", new FixedMetadataValue(this.plugin, "TNTGun"));
         BlockState var5 = block.getState();
         if (var5 instanceof Dispenser) {
            Dispenser dispenser = (Dispenser)var5;
            Inventory inventory = dispenser.getInventory();
            ItemStack blockedItem = this.configManager.getTntGunBlockedItem().build();
            blockedItem.setAmount(1);
            this.configManager.getTntGunBlockedSlots().forEach((slot) -> inventory.setItem(slot, blockedItem.clone()));
         }

      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onBlockFade(BlockFadeEvent event) {
      Block block = event.getBlock();
      if (block.hasMetadata("TNTGun")) {
         event.setCancelled(true);
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onHopperMove(InventoryMoveItemEvent event) {
      if (this.isBlockedItem(event.getItem())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onDispense(BlockDispenseEvent event) {
      Block block = event.getBlock();
      if (block.hasMetadata("TNTGun")) {
         if (this.isBlockedItem(event.getItem())) {
            BlockState var4 = block.getState();
            if (var4 instanceof Dispenser) {
               Dispenser dispenser = (Dispenser)var4;
               Inventory var10 = dispenser.getInventory();
               ItemStack ammo = null;

               for(ItemStack item : var10.getContents()) {
                  if (item != null && !item.getType().isAir() && !this.isBlockedItem(item)) {
                     ammo = item;
                     break;
                  }
               }

               if (ammo == null) {
                  event.setCancelled(true);
               } else {
                  ItemStack shotItem = ammo.clone();
                  shotItem.setAmount(1);
                  event.setItem(shotItem);
                  Bukkit.getScheduler().runTask(this.plugin, () -> {
                     var10.removeItem(new ItemStack[]{shotItem});
                     ItemStack blocked = this.configManager.getTntGunBlockedItem().build();
                     blocked.setAmount(1);
                     var10.addItem(new ItemStack[]{blocked});
                  });
               }
            }
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onHopperPickup(InventoryPickupItemEvent event) {
      if (this.isBlockedItem(event.getItem().getItemStack())) {
         event.setCancelled(true);
      }
   }

   private boolean isBlockedItem(ItemStack itemStack) {
      return itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(this.configManager.getBlockedItemKey(), PersistentDataType.BYTE);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onClick(InventoryClickEvent event) {
      ItemStack itemStack = event.getCurrentItem();
      if (this.isBlockedItem(itemStack)) {
         event.setCancelled(true);
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onBlockFromTo(BlockFromToEvent event) {
      Block block = event.getBlock();
      if (block.hasMetadata("TNTGun")) {
         event.setCancelled(true);
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onBlockExplode(BlockExplodeEvent event) {
      this.cleanExplosionBlocks(event.blockList());
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onEntityExplode(EntityExplodeEvent event) {
      this.cleanExplosionBlocks(event.blockList());
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPistonRetract(BlockPistonRetractEvent event) {
      if (event.isSticky()) {
         for(Block block : event.getBlocks()) {
            if (block.hasMetadata("TNTGun")) {
               event.setCancelled(true);
               return;
            }
         }

      }
   }

   private void cleanExplosionBlocks(List<Block> blocks) {
      List<Block> blocksToRemove = new ArrayList();

      for(Block block : blocks) {
         if (block.hasMetadata("TNTGun")) {
            this.handleBreakGun(block);
            blocksToRemove.add(block);
         }
      }

      blocks.removeAll(blocksToRemove);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onEntityChangeBlock(EntityChangeBlockEvent event) {
      Block block = event.getBlock();
      if (block.hasMetadata("TNTGun")) {
         this.handleBreakGun(block);
         event.setCancelled(true);
      }
   }

   private void handleBreakGun(Block block) {
      BlockState var3 = block.getState();
      if (var3 instanceof Dispenser dispenser) {
         Inventory inv = dispenser.getInventory();
         ItemStack[] contents = inv.getContents();

         for(int i = 0; i < contents.length; ++i) {
            ItemStack itemStack = contents[i];
            if (this.isBlockedItem(contents[i])) {
               inv.setItem(i, (ItemStack)null);
            } else if (itemStack != null && itemStack.getType() != Material.AIR) {
               block.getLocation().getWorld().dropItemNaturally(block.getLocation(), itemStack);
            }
         }

         inv.clear();
      }

      ItemBuilder gunBuilder = this.configManager.getTntGunBuilder();
      block.getLocation().getWorld().dropItemNaturally(block.getLocation(), gunBuilder.amount(1).build());
      block.removeMetadata("TNTGun", this.plugin);
      block.setType(Material.AIR);
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onTNTBreak(BlockBreakEvent event) {
      Block block = event.getBlock();
      if (block.hasMetadata("TNTGun")) {
         if (this.configManager.isTntGunRegionBreak() || !event.isCancelled()) {
            this.handleBreakGun(block);
            event.setDropItems(false);
         }
      }
   }

   @Generated
   public TNTGunListener(SunTNT plugin, TNTConfigManager configManager) {
      this.plugin = plugin;
      this.configManager = configManager;
   }
}
