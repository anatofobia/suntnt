package ru.loper.suntnt.listeners;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnerPlaceListener implements Listener {
   @EventHandler
   public void onSpawnerPlace(BlockPlaceEvent event) {
      ItemStack item = event.getItemInHand();
      if (item.getType() == Material.SPAWNER) {
         ItemMeta var4 = item.getItemMeta();
         if (var4 instanceof BlockStateMeta) {
            BlockStateMeta meta = (BlockStateMeta)var4;
            BlockState var5 = meta.getBlockState();
            if (var5 instanceof CreatureSpawner) {
               CreatureSpawner itemSpawnerState = (CreatureSpawner)var5;
               BlockState var6 = event.getBlockPlaced().getState();
               if (var6 instanceof CreatureSpawner) {
                  CreatureSpawner worldSpawner = (CreatureSpawner)var6;
                  worldSpawner.setSpawnedType(itemSpawnerState.getSpawnedType());
                  worldSpawner.update();
               }

            }
         }
      }
   }
}
