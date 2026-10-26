package ru.loper.suntnt.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Generated;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.ItemStack;
import ru.loper.suntnt.SunTNT;

public final class Utils {
   public static List<File> getFiles(File folder) {
      ArrayList<File> files = new ArrayList();
      if (folder.listFiles() == null) {
         return files;
      } else {
         for(File file : (File[])Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
               files.addAll(getFiles(file));
            } else {
               files.add(file);
            }
         }

         return files;
      }
   }

   public static ItemStack getCustomItem(String path) {
      ItemStack item = SunTNT.getInstance().getConfigManager().getCustomConfig("customitems").getConfig().getItemStack("items." + path);
      return item != null ? item.clone() : null;
   }

   public static Location getDispenserLocation(Block dispenser) {
      Directional directional = (Directional)dispenser.getBlockData();
      BlockFace face = directional.getFacing();
      return dispenser.getLocation().add((double)0.5F + (double)face.getModX() * 0.7, (double)0.5F + (double)face.getModY() * 0.7, (double)0.5F + (double)face.getModZ() * 0.7);
   }

   @Generated
   private Utils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
