package ru.loper.suntnt.listeners;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;
import lombok.Generated;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ru.loper.suntnt.SunTNT;
import ru.loper.suntnt.api.modules.CustomTNT;
import ru.loper.suntnt.api.modules.TNTGunProjectile;
import ru.loper.suntnt.config.TNTConfigManager;
import ru.loper.suntnt.utils.Utils;

public class TNTSpawnListener implements Listener {
   private final SunTNT plugin;
   private final TNTConfigManager configManager;
   private final Cache<Block, CustomTNT> cachedTNTs;
   private final Cache<Block, TNTGunProjectile> cachedGunProjectiles;

   private static void setTntCustomName(TNTPrimed tntPrimed, String customTnt) {
      tntPrimed.setCustomName(customTnt);
      tntPrimed.setCustomNameVisible(true);
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onTNTPlace(BlockPlaceEvent event) {
      ItemStack itemStack = event.getItemInHand();
      if (itemStack.getType().equals(Material.TNT)) {
         PersistentDataContainer data = itemStack.getItemMeta().getPersistentDataContainer();
         String tntType = (String)data.get(this.plugin.getTntManager().getTntTypeKey(), PersistentDataType.STRING);
         if (tntType != null) {
            Block block = event.getBlockPlaced();
            block.setMetadata("TNTType", new FixedMetadataValue(this.plugin, tntType));
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOW,
      ignoreCancelled = true
   )
   public void onTNTPrime(TNTPrimeEvent event) {
      Block block = event.getBlock();
      if (block.hasMetadata("TNTType")) {
         String tntType = ((MetadataValue)block.getMetadata("TNTType").get(0)).asString();
         CustomTNT customTnt = this.plugin.getTntManager().getCustomTNT(tntType);
         if (customTnt != null) {
            this.cachedTNTs.put(block, customTnt);
            block.removeMetadata("TNTType", this.plugin);
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGHEST
   )
   public void omTNTDispense(BlockDispenseEvent event) {
      Block block = event.getBlock();
      if (block.getType() == Material.DISPENSER) {
         ItemStack itemStack = event.getItem();
         if (itemStack.getType().equals(Material.TNT)) {
            Location dropLocation = Utils.getDispenserLocation(block);
            if (block.hasMetadata("TNTGun")) {
               this.cachedGunProjectiles.put(dropLocation.getBlock(), new TNTGunProjectile(block));
            }

            PersistentDataContainer data = itemStack.getItemMeta().getPersistentDataContainer();
            String tntType = (String)data.get(this.plugin.getTntManager().getTntTypeKey(), PersistentDataType.STRING);
            if (tntType != null) {
               CustomTNT customTNT = this.plugin.getTntManager().getCustomTNT(tntType);
               if (customTNT != null) {
                  this.cachedTNTs.put(dropLocation.getBlock(), customTNT);
               }
            }
         }
      }
   }

   @EventHandler
   public void onTNTSpawn(EntitySpawnEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof TNTPrimed tntPrimed) {
         Block block = event.getLocation().getBlock();
         this.handleTNTPossibility(tntPrimed, block, entity);
         TNTGunProjectile tntGunProjectile = (TNTGunProjectile)this.cachedGunProjectiles.getIfPresent(block);
         if (tntGunProjectile != null) {
            tntGunProjectile.setTntPrimed(tntPrimed);
            this.cachedGunProjectiles.invalidate(block);
            SunTNT.handleTNTGun(tntGunProjectile);
         }

      }
   }

   private void handleTNTPossibility(TNTPrimed tntPrimed, Block block, Entity entity) {
      CustomTNT customTnt = (CustomTNT)this.cachedTNTs.getIfPresent(block);
      if (customTnt == null) {
         if (this.configManager.isDefaultCustomNameVisible()) {
            setTntCustomName(tntPrimed, this.configManager.getDefaultCustomName());
         }

      } else {
         this.cachedTNTs.invalidate(block);
         tntPrimed.setFuseTicks(customTnt.getFuseTicks());
         if (customTnt.isCustomNameVisible()) {
            setTntCustomName(tntPrimed, customTnt.getCustomName());
         }

         entity.setMetadata("TNTType", new FixedMetadataValue(this.plugin, customTnt.getName()));
      }
   }

   @EventHandler(
      priority = EventPriority.LOW,
      ignoreCancelled = true
   )
   public void onTNTExplosion(ExplosionPrimeEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof TNTPrimed) {
         if (entity.hasMetadata("TNTType")) {
            String tntType = ((MetadataValue)entity.getMetadata("TNTType").get(0)).asString();
            CustomTNT customTnt = this.plugin.getTntManager().getCustomTNT(tntType);
            if (customTnt != null) {
               event.setRadius((float)customTnt.getExplosionRadius());
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onTNTBreak(BlockBreakEvent event) {
      Block block = event.getBlock();
      if (block.getType().equals(Material.TNT)) {
         if (block.hasMetadata("TNTType")) {
            String tntType = ((MetadataValue)block.getMetadata("TNTType").get(0)).asString();
            CustomTNT customTnt = this.plugin.getTntManager().getCustomTNT(tntType);
            block.getLocation().getWorld().dropItemNaturally(block.getLocation(), customTnt.getItemStack());
            block.removeMetadata("TNTType", this.plugin);
            event.setDropItems(false);
         }
      }
   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent event) {
      Entity entity = event.getEntity();
      if (this.configManager.isDisableItemExplosion()) {
         EntityDamageEvent.DamageCause cause = event.getCause();
         if (entity.getType().equals(EntityType.DROPPED_ITEM)) {
            if (cause.equals(DamageCause.ENTITY_EXPLOSION) || cause.equals(DamageCause.BLOCK_EXPLOSION)) {
               event.setCancelled(true);
            }

         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onPistonExtend(BlockPistonExtendEvent event) {
      this.updateBlockMeta(event.getBlocks(), event.getDirection());
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onPistonRetract(BlockPistonRetractEvent event) {
      if (event.isSticky()) {
         this.updateBlockMeta(event.getBlocks(), event.getDirection());
      }
   }

   private void updateBlockMeta(List<Block> blocks2, BlockFace direction) {
      ListIterator<Block> blockIterator = blocks2.listIterator(blocks2.size());

      while(blockIterator.hasPrevious()) {
         Block block = (Block)blockIterator.previous();
         if (block.getType().equals(Material.TNT) && block.hasMetadata("TNTType")) {
            String tntType = ((MetadataValue)block.getMetadata("TNTType").get(0)).asString();
            block.removeMetadata("TNTType", this.plugin);
            Block nextBlock = block.getRelative(direction, 1);
            nextBlock.setMetadata("TNTType", new FixedMetadataValue(this.plugin, tntType));
         }
      }

   }

   @Generated
   public TNTSpawnListener(SunTNT plugin, TNTConfigManager configManager) {
      this.cachedTNTs = CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.SECONDS).build();
      this.cachedGunProjectiles = CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.SECONDS).build();
      this.plugin = plugin;
      this.configManager = configManager;
   }
}
