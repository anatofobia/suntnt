package ru.loper.suntnt.listeners;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ru.loper.liteutils.HolyLiteUtils;
import ru.loper.liteutils.api.goldspawner.data.GoldSpawnerManager;
import ru.loper.liteutils.api.goldspawner.listeners.GoldSpawnerListener;
import ru.loper.liteutils.item.SpawnerItem;
import ru.loper.suncore.api.items.ItemBuilder;
import ru.loper.sunprotectionstones.PSRegion;
import ru.loper.sunprotectionstones.SunProtectionStones;
import ru.loper.suntnt.SunTNT;
import ru.loper.suntnt.tnt.CustomTNT;
import ru.loper.suntnt.utils.WGUtils;

public class TNTListener implements Listener {
   private final SunTNT plugin;
   private final Cache<Block, CustomTNT> cachedTNTs;
   private final Cache<Location, Integer> waterBlocks;
   private static final Set<Material> PROTECTED_BLOCKS;
   private static final Set<Material> OBSIDIAN_TYPE_BLOCKS;

   public TNTListener(SunTNT plugin) {
      this.cachedTNTs = CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.SECONDS).build();
      this.waterBlocks = CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.MINUTES).build();
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.LOW,
      ignoreCancelled = true
   )
   public void onEntityExplode(EntityExplodeEvent event) {
      Entity entity = event.getEntity();
      CustomTNT customTnt = this.plugin.getTntManager().getCustomTNT(entity);
      if (customTnt == null) {
         event.blockList().removeIf((blockx) -> blockx.getType().equals(Material.SPAWNER));
      } else {
         if (customTnt.getSpawnerChance() > 0) {
            this.handleSpawnerTnt(event, customTnt);
            event.blockList().clear();
         } else {
            event.blockList().removeIf((blockx) -> blockx.getType().equals(Material.SPAWNER));
         }

         Location location = entity.getLocation();
         if (customTnt.getObsidianChance() > 0) {
            event.blockList().addAll(this.getNearbyBlocks(location, 2).stream().filter((blockx) -> OBSIDIAN_TYPE_BLOCKS.contains(blockx.getType())).toList());
         }

         if (customTnt.getLiquidChance() > 0) {
            event.blockList().addAll(this.getNearbyBlocks(location, 2).stream().filter((blockx) -> !blockx.getType().isAir() && !PROTECTED_BLOCKS.contains(blockx.getType())).toList());
         } else {
            event.blockList().removeIf(Block::isLiquid);
         }

         if (customTnt.getBlocksRadius() > 0) {
            event.blockList().addAll(this.getNearbyBlocks(location, customTnt.getBlocksRadius()).stream().filter((blockx) -> !blockx.getType().isAir() && !PROTECTED_BLOCKS.contains(blockx.getType()) && !WGUtils.hasRegionAtLocation(blockx.getLocation())).toList());
         }

         if (customTnt.isIce()) {
            this.createIceSphere(entity.getLocation(), customTnt.getIceRadius(), customTnt.getIceDelay());
         }

         if (customTnt.isBreakPSRegion() && SunTNT.getInstance().isProtectionStonesStatus()) {
            Iterator<Block> iterator = event.blockList().iterator();

            while(iterator.hasNext()) {
               Block block = (Block)iterator.next();
               if (SunProtectionStones.isProtectBlock(block)) {
                  PSRegion region = PSRegion.fromLocation(block.getLocation());
                  if (region != null) {
                     region.removeStrength(1);
                     iterator.remove();
                  }
               }
            }
         }

      }
   }

   private void handleSpawnerTnt(EntityExplodeEvent event, CustomTNT customTnt) {
      ThreadLocalRandom random = ThreadLocalRandom.current();
      if (random.nextInt(0, 101) <= customTnt.getSpawnerChance()) {
         for(Block block : event.blockList()) {
            if (block.getType().equals(Material.SPAWNER)) {
               ItemStack spawnerItem = this.getSpawner(customTnt, block, random);
               if (spawnerItem != null) {
                  block.getWorld().dropItemNaturally(block.getLocation(), spawnerItem);
               }

               block.setType(Material.AIR);
               break;
            }
         }

      }
   }

   private ItemStack getSpawner(CustomTNT customTnt, Block block, ThreadLocalRandom random) {
      if (this.plugin.isHolyLiteUtilsStatus()) {
         GoldSpawnerManager goldSpawnerManager = HolyLiteUtils.getInstance().getGoldSpawnerManager();
         Location goldSpawnerLocation = block.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F);
         if (goldSpawnerManager.contains(goldSpawnerLocation)) {
            goldSpawnerManager.removeSpawner(goldSpawnerLocation);
            GoldSpawnerListener.stopSpawnerTask(goldSpawnerLocation);
            if (random.nextInt(0, 101) <= customTnt.getGoldSpawnerChance()) {
               ItemBuilder dropBuilder = SpawnerItem.getSpawnerItem();
               if (dropBuilder != null) {
                  return dropBuilder.build();
               }
            }
         }
      }

      if (random.nextInt(0, 101) > customTnt.getSpawnerChance() && !customTnt.isSpawnerAlwaysSaveMob()) {
         return new ItemStack(Material.SPAWNER);
      } else {
         CreatureSpawner creatureSpawner = (CreatureSpawner)block.getState();
         EntityType entityType = creatureSpawner.getSpawnedType();
         return this.createSpawnerItemStack(entityType);
      }
   }

   public ItemStack createSpawnerItemStack(EntityType entityType) {
      ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
      BlockStateMeta blockStateMeta = (BlockStateMeta)spawnerItem.getItemMeta();
      if (blockStateMeta == null) {
         return null;
      } else {
         CreatureSpawner spawner = (CreatureSpawner)blockStateMeta.getBlockState();
         spawner.setSpawnedType(entityType);
         blockStateMeta.setBlockState(spawner);
         spawnerItem.setItemMeta(blockStateMeta);
         return spawnerItem;
      }
   }

   private void createIceSphere(Location center, int radius, long delay) {
      World world = center.getWorld();
      if (world != null) {
         for(int y = -radius; y <= radius; ++y) {
            for(int x = -radius; x <= radius; ++x) {
               for(int z = -radius; z <= radius; ++z) {
                  Block block;
                  if (Math.sqrt((double)(x * x + y * y + z * z)) <= (double)radius && ((block = world.getBlockAt(center.clone().add((double)x, (double)y, (double)z))).getType().isAir() || block.isLiquid())) {
                     BlockData blockData;
                     if (block.getType().equals(Material.WATER) && (blockData = block.getBlockData()) instanceof Levelled) {
                        Levelled levelled = (Levelled)blockData;
                        if (levelled.getLevel() == 0) {
                           this.waterBlocks.put(block.getLocation(), levelled.getLevel());
                        }
                     } else if (block.getType().equals(Material.BUBBLE_COLUMN)) {
                        this.waterBlocks.put(block.getLocation(), 0);
                     }

                     block.setType(Material.ICE);
                     this.scheduleIceRemoval(block.getLocation(), delay);
                  }
               }
            }

            --delay;
         }
      }

   }

   private void scheduleIceRemoval(Location location, long delay) {
      Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
         Block block = location.getBlock();
         if (block.getType() == Material.ICE) {
            if (this.waterBlocks.getIfPresent(block.getLocation()) != null) {
               block.setBlockData(Material.WATER.createBlockData(), false);
            } else {
               block.setType(Material.AIR);
            }

            location.getNearbyEntities((double)15.0F, (double)15.0F, (double)15.0F).forEach((entity) -> {
               if (entity instanceof Player nearbyPlayer) {
                  nearbyPlayer.playSound(location, Sound.BLOCK_STONE_PLACE, 1.0F, 1.0F);
               }

            });
         }

      }, delay);
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
      priority = EventPriority.LOW,
      ignoreCancelled = true
   )
   public void onTNTPrime(ExplosionPrimeEvent event) {
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

   @EventHandler
   public void onSpawn(EntitySpawnEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof TNTPrimed tntPrimed) {
         CustomTNT customTnt = (CustomTNT)this.cachedTNTs.getIfPresent(event.getLocation().getBlock());
         if (customTnt != null) {
            tntPrimed.setFuseTicks(customTnt.getFuseTicks());
            entity.setMetadata("TNTType", new FixedMetadataValue(this.plugin, customTnt.getName()));
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onPlace(BlockPlaceEvent event) {
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

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onBreak(BlockBreakEvent event) {
      Block block = event.getBlock();
      if (block.getType().equals(Material.TNT)) {
         if (block.hasMetadata("TNTType")) {
            String tntType = ((MetadataValue)block.getMetadata("TNTType").get(0)).asString();
            CustomTNT customTnt = this.plugin.getTntManager().getCustomTNT(tntType);
            event.setDropItems(false);
            block.getLocation().getWorld().dropItemNaturally(block.getLocation(), customTnt.getItemStack());
            block.removeMetadata("TNTType", this.plugin);
         }
      }
   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent event) {
      Entity entity = event.getEntity();
      EntityDamageEvent.DamageCause cause = event.getCause();
      if (entity.getType().equals(EntityType.DROPPED_ITEM)) {
         if (cause.equals(DamageCause.ENTITY_EXPLOSION) || cause.equals(DamageCause.BLOCK_EXPLOSION)) {
            event.setCancelled(true);
         }

      }
   }

   private List<Block> getNearbyBlocks(Location location, int radius) {
      ArrayList<Block> blocks = new ArrayList();

      for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; ++x) {
         for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; ++y) {
            for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; ++z) {
               Location loc = new Location(location.getWorld(), (double)x, (double)y, (double)z);
               blocks.add(loc.getBlock());
            }
         }
      }

      return blocks;
   }

   static {
      PROTECTED_BLOCKS = EnumSet.of(Material.BEDROCK, Material.BARRIER, Material.COMMAND_BLOCK, Material.END_PORTAL_FRAME, Material.END_PORTAL, Material.ANCIENT_DEBRIS, Material.NETHERITE_BLOCK, Material.OBSIDIAN, Material.CRYING_OBSIDIAN);
      OBSIDIAN_TYPE_BLOCKS = EnumSet.of(Material.OBSIDIAN, Material.CRYING_OBSIDIAN, Material.ANCIENT_DEBRIS, Material.NETHERITE_BLOCK, Material.ENDER_CHEST, Material.ENCHANTING_TABLE, Material.ANVIL);
   }
}
