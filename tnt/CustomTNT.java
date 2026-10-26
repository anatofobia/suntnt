package ru.loper.suntnt.tnt;

import lombok.Generated;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.api.items.ItemBuilder;
import ru.loper.suntnt.SunTNT;
import ru.loper.suntnt.utils.Utils;

public class CustomTNT {
   private final String name;
   private final int explosionRadius;
   private final int obsidianChance;
   private final int spawnerChance;
   private final int goldSpawnerChance;
   private final int blocksRadius;
   private final int liquidChance;
   private final int fuseTicks;
   private final int iceRadius;
   private final boolean spawnerAlwaysSaveMob;
   private final boolean breakPSRegion;
   private final boolean ice;
   private final long iceDelay;
   private final ItemBuilder tntBuilder;

   public CustomTNT(CustomConfig tntConfig, TNTManager tntManager) {
      FileConfiguration config = tntConfig.getConfig();
      this.name = config.getString("name", "default");
      this.obsidianChance = config.getInt("obsidian-chance", 0);
      this.liquidChance = config.getInt("liquid-chance", 0);
      this.spawnerChance = config.getInt("spawner-chance", 0);
      this.goldSpawnerChance = config.getInt("gold-spawner-chance", 0);
      this.spawnerAlwaysSaveMob = config.getBoolean("spawner-always-save-mob", false);
      this.explosionRadius = config.getInt("explosion-radius", 5);
      this.fuseTicks = config.getInt("fuse-ticks", 100);
      this.blocksRadius = config.getInt("blocks-radius", 4);
      this.ice = config.getBoolean("ice", false);
      this.iceRadius = config.getInt("ice-radius", 0);
      this.iceDelay = (long)config.getInt("ice-delay", 0);
      this.breakPSRegion = config.getBoolean("break-ps-region", false);
      ConfigurationSection builderSection = config.getConfigurationSection("item");
      if (builderSection == null) {
         this.tntBuilder = new ItemBuilder(Material.TNT);
      } else {
         this.tntBuilder = ItemBuilder.fromConfig(builderSection);
      }

      ItemMeta meta = this.tntBuilder.meta();
      meta.getPersistentDataContainer().set(tntManager.getTntTypeKey(), PersistentDataType.STRING, this.name);
      this.tntBuilder.meta(meta);
      ConfigurationSection recipeSection = config.getConfigurationSection("recipe");
      if (recipeSection != null) {
         String[] value = recipeSection.getString("shape", "").split(":");
         if (value.length != 3) {
            SunTNT.getInstance().getLogger().severe("Ошибка при загрузки крафта для " + this.name);
            return;
         }

         ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(SunTNT.getInstance(), this.name), this.getItemStack());
         recipe.shape(new String[]{value[0], value[1], value[2]});
         ConfigurationSection ingredientsSection = recipeSection.getConfigurationSection("ingredients");
         if (ingredientsSection == null) {
            SunTNT.getInstance().getLogger().severe("Ошибка при загрузки крафта для " + this.name + ", отсутствуют ингредиенты");
            return;
         }

         for(String key : ingredientsSection.getKeys(false)) {
            String shape = ingredientsSection.getString(key);
            if (shape != null) {
               if (shape.toLowerCase().startsWith("customitem:")) {
                  ItemStack item = Utils.getCustomItem(shape.replace("customitem:", ""));
                  if (item != null) {
                     recipe.setIngredient(key.charAt(0), item);
                  }
               } else {
                  try {
                     Material material = Material.valueOf(shape.toUpperCase());
                     recipe.setIngredient(key.charAt(0), material);
                  } catch (IllegalArgumentException var15) {
                     SunTNT.getInstance().getLogger().severe("Неизвестный материал - " + shape);
                  }
               }
            }
         }

         try {
            SunTNT.getInstance().getServer().addRecipe(recipe);
         } catch (IllegalStateException var14) {
            SunTNT.getInstance().getLogger().severe("Не удалось зарегистрировать крафт динамита " + this.name);
         }
      }

   }

   public ItemStack getItemStack() {
      return this.tntBuilder.amount(1).build().clone();
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public int getExplosionRadius() {
      return this.explosionRadius;
   }

   @Generated
   public int getObsidianChance() {
      return this.obsidianChance;
   }

   @Generated
   public int getSpawnerChance() {
      return this.spawnerChance;
   }

   @Generated
   public int getGoldSpawnerChance() {
      return this.goldSpawnerChance;
   }

   @Generated
   public int getBlocksRadius() {
      return this.blocksRadius;
   }

   @Generated
   public int getLiquidChance() {
      return this.liquidChance;
   }

   @Generated
   public int getFuseTicks() {
      return this.fuseTicks;
   }

   @Generated
   public int getIceRadius() {
      return this.iceRadius;
   }

   @Generated
   public boolean isSpawnerAlwaysSaveMob() {
      return this.spawnerAlwaysSaveMob;
   }

   @Generated
   public boolean isBreakPSRegion() {
      return this.breakPSRegion;
   }

   @Generated
   public boolean isIce() {
      return this.ice;
   }

   @Generated
   public long getIceDelay() {
      return this.iceDelay;
   }

   @Generated
   public ItemBuilder getTntBuilder() {
      return this.tntBuilder;
   }
}
