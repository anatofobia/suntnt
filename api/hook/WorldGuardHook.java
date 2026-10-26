package ru.loper.suntnt.api.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.Set;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class WorldGuardHook {
   public static boolean hasRegionAtLocation(Location location) {
      if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
         return false;
      } else {
         return !getRegionsAtLocation(location).isEmpty();
      }
   }

   public static Set<ProtectedRegion> getRegionsAtLocation(Location location) {
      com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(location);
      RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
      RegionQuery query = container.createQuery();
      ApplicableRegionSet regions = query.getApplicableRegions(weLocation);
      return regions.getRegions();
   }

   @Generated
   private WorldGuardHook() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
