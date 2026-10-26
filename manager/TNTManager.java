package ru.loper.suntnt.manager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import lombok.Generated;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suntnt.SunTNT;
import ru.loper.suntnt.api.modules.CustomTNT;
import ru.loper.suntnt.utils.Utils;

public class TNTManager {
   private final SunTNT plugin;
   private final HashMap<String, CustomTNT> customTNTS = new HashMap();
   private final NamespacedKey tntTypeKey;

   public TNTManager(SunTNT plugin) {
      this.tntTypeKey = new NamespacedKey(plugin, "TNTType");
      this.plugin = plugin;
      this.init();
   }

   public void init() {
      this.customTNTS.clear();
      File directory = new File(this.plugin.getDataFolder(), "/tnts");
      if (!directory.exists()) {
         this.plugin.saveResource("tnts/EXAMPLE_TNT.yml", true);
      }

      for(File file : Utils.getFiles(directory)) {
         if (file.getName().endsWith(".yml")) {
            CustomConfig config = new CustomConfig(file);
            this.customTNTS.put(config.getConfig().getString("name", "default"), new CustomTNT(config, this));
         }
      }

   }

   public CustomTNT getCustomTNT(String name) {
      return (CustomTNT)this.customTNTS.get(name);
   }

   public CustomTNT getCustomTNT(Entity entity) {
      if (!entity.hasMetadata("TNTType")) {
         return null;
      } else {
         String tntType = ((MetadataValue)entity.getMetadata("TNTType").get(0)).asString();
         return (CustomTNT)this.customTNTS.get(tntType);
      }
   }

   public List<String> getCustomTNTsName() {
      return this.customTNTS.keySet().stream().toList();
   }

   @Generated
   public NamespacedKey getTntTypeKey() {
      return this.tntTypeKey;
   }
}
