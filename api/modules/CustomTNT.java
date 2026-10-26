package ru.loper.suntnt.api.modules;

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
import ru.loper.suncore.utils.Colorize;
import ru.loper.suntnt.SunTNT;
import ru.loper.suntnt.manager.TNTManager;
import ru.loper.suntnt.utils.Utils;

public class CustomTNT {
   private final String name;
   private final int explosionRadius;
   private final int obsidianChance;
   private final int spawnerChance;
   private final int spawnerMobSaveChance;
   private final int goldSpawnerChance;
   private final int mysteriousSpawnerChance;
   private final int blocksRadius;
   private final int liquidChance;
   private final int fuseTicks;
   private final int iceRadius;
   private final int waterChance;
   private final boolean breakPSRegion;
   private final boolean ice;
   private final long iceDelay;
   private final boolean customNameVisible;
   private final String customName;
   private final ItemBuilder tntBuilder;
   private final ItemBuilder spawnerForm;

   public CustomTNT(CustomConfig tntConfig, TNTManager tntManager) {
      FileConfiguration config = tntConfig.getConfig();
      this.name = config.getString("name", "default");
      this.obsidianChance = config.getInt("obsidian-chance", 0);
      this.liquidChance = config.getInt("liquid-chance", 0);
      this.spawnerChance = config.getInt("spawner-chance", 0);
      this.spawnerMobSaveChance = config.getInt("spawner-mob-save-chance", 0);
      this.goldSpawnerChance = config.getInt("gold-spawner-chance", 0);
      this.mysteriousSpawnerChance = config.getInt("mysterious-spawner-chance", 0);
      this.explosionRadius = config.getInt("explosion-radius", 5);
      this.fuseTicks = config.getInt("fuse-ticks", 100);
      this.blocksRadius = config.getInt("blocks-radius", 4);
      this.ice = config.getBoolean("ice", false);
      this.iceRadius = config.getInt("ice-radius", 0);
      this.waterChance = config.getInt("water-chance", 0);
      this.iceDelay = (long)config.getInt("ice-delay", 0);
      this.breakPSRegion = config.getBoolean("break-ps-region", false);
      ConfigurationSection builderSection = config.getConfigurationSection("item");
      if (builderSection == null) {
         this.tntBuilder = new ItemBuilder(Material.TNT);
      } else {
         this.tntBuilder = ItemBuilder.fromConfig(builderSection);
      }

      ConfigurationSection spawnerBuilderSection = config.getConfigurationSection("spawner_form");
      if (builderSection == null) {
         this.spawnerForm = (new ItemBuilder(Material.SPAWNER)).name("&f{mob}");
      } else {
         this.spawnerForm = ItemBuilder.fromConfig(spawnerBuilderSection);
      }

      ItemMeta meta = this.tntBuilder.meta();
      meta.getPersistentDataContainer().set(tntManager.getTntTypeKey(), PersistentDataType.STRING, this.name);
      this.tntBuilder.meta(meta);
      ConfigurationSection customNameSection = config.getConfigurationSection("custom_name");
      if (customNameSection != null) {
         this.customNameVisible = customNameSection.getBoolean("visible", false);
         this.customName = Colorize.parse(customNameSection.getString("name"));
      } else {
         this.customNameVisible = false;
         this.customName = "";
      }

      ConfigurationSection recipeSection = config.getConfigurationSection("recipe");
      if (recipeSection != null) {
         this.registerRecipe(recipeSection);
      }

   }

   private void registerRecipe(ConfigurationSection recipeSection) {
      String[] value = recipeSection.getString("shape", "").split(":");
      if (value.length != 3) {
         SunTNT.getInstance().getLogger().severe("Ошибка при загрузки крафта для " + this.name);
      } else {
         ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(SunTNT.getInstance(), this.name), this.getItemStack());
         recipe.shape(new String[]{value[0], value[1], value[2]});
         ConfigurationSection ingredientsSection = recipeSection.getConfigurationSection("ingredients");
         if (ingredientsSection == null) {
            SunTNT.getInstance().getLogger().severe("Ошибка при загрузки крафта для " + this.name + ", отсутствуют ингредиенты");
         } else {
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
                     } catch (IllegalArgumentException var10) {
                        SunTNT.getInstance().getLogger().severe("Неизвестный материал - " + shape);
                     }
                  }
               }
            }

            try {
               SunTNT.getInstance().getServer().addRecipe(recipe);
            } catch (IllegalStateException var9) {
               SunTNT.getInstance().getLogger().severe("Не удалось зарегистрировать крафт динамита " + this.name);
            }

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
   public int getSpawnerMobSaveChance() {
      return this.spawnerMobSaveChance;
   }

   @Generated
   public int getGoldSpawnerChance() {
      return this.goldSpawnerChance;
   }

   @Generated
   public int getMysteriousSpawnerChance() {
      return this.mysteriousSpawnerChance;
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
   public int getWaterChance() {
      return this.waterChance;
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
   public boolean isCustomNameVisible() {
      return this.customNameVisible;
   }

   @Generated
   public String getCustomName() {
      return this.customName;
   }

   @Generated
   public ItemBuilder getTntBuilder() {
      return this.tntBuilder;
   }

   @Generated
   public ItemBuilder getSpawnerForm() {
      return this.spawnerForm;
   }

   @Generated
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof CustomTNT)) {
         return false;
      } else {
         CustomTNT other = (CustomTNT)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (this.getExplosionRadius() != other.getExplosionRadius()) {
            return false;
         } else if (this.getObsidianChance() != other.getObsidianChance()) {
            return false;
         } else if (this.getSpawnerChance() != other.getSpawnerChance()) {
            return false;
         } else if (this.getSpawnerMobSaveChance() != other.getSpawnerMobSaveChance()) {
            return false;
         } else if (this.getGoldSpawnerChance() != other.getGoldSpawnerChance()) {
            return false;
         } else if (this.getMysteriousSpawnerChance() != other.getMysteriousSpawnerChance()) {
            return false;
         } else if (this.getBlocksRadius() != other.getBlocksRadius()) {
            return false;
         } else if (this.getLiquidChance() != other.getLiquidChance()) {
            return false;
         } else if (this.getFuseTicks() != other.getFuseTicks()) {
            return false;
         } else if (this.getIceRadius() != other.getIceRadius()) {
            return false;
         } else if (this.getWaterChance() != other.getWaterChance()) {
            return false;
         } else if (this.isBreakPSRegion() != other.isBreakPSRegion()) {
            return false;
         } else if (this.isIce() != other.isIce()) {
            return false;
         } else if (this.getIceDelay() != other.getIceDelay()) {
            return false;
         } else if (this.isCustomNameVisible() != other.isCustomNameVisible()) {
            return false;
         } else {
            Object this$name = this.getName();
            Object other$name = other.getName();
            if (this$name == null) {
               if (other$name != null) {
                  return false;
               }
            } else if (!this$name.equals(other$name)) {
               return false;
            }

            Object this$customName = this.getCustomName();
            Object other$customName = other.getCustomName();
            if (this$customName == null) {
               if (other$customName != null) {
                  return false;
               }
            } else if (!this$customName.equals(other$customName)) {
               return false;
            }

            Object this$tntBuilder = this.getTntBuilder();
            Object other$tntBuilder = other.getTntBuilder();
            if (this$tntBuilder == null) {
               if (other$tntBuilder != null) {
                  return false;
               }
            } else if (!this$tntBuilder.equals(other$tntBuilder)) {
               return false;
            }

            Object this$spawnerForm = this.getSpawnerForm();
            Object other$spawnerForm = other.getSpawnerForm();
            if (this$spawnerForm == null) {
               if (other$spawnerForm != null) {
                  return false;
               }
            } else if (!this$spawnerForm.equals(other$spawnerForm)) {
               return false;
            }

            return true;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof CustomTNT;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getExplosionRadius();
      result = result * 59 + this.getObsidianChance();
      result = result * 59 + this.getSpawnerChance();
      result = result * 59 + this.getSpawnerMobSaveChance();
      result = result * 59 + this.getGoldSpawnerChance();
      result = result * 59 + this.getMysteriousSpawnerChance();
      result = result * 59 + this.getBlocksRadius();
      result = result * 59 + this.getLiquidChance();
      result = result * 59 + this.getFuseTicks();
      result = result * 59 + this.getIceRadius();
      result = result * 59 + this.getWaterChance();
      result = result * 59 + (this.isBreakPSRegion() ? 79 : 97);
      result = result * 59 + (this.isIce() ? 79 : 97);
      long $iceDelay = this.getIceDelay();
      result = result * 59 + (int)($iceDelay >>> 32 ^ $iceDelay);
      result = result * 59 + (this.isCustomNameVisible() ? 79 : 97);
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $customName = this.getCustomName();
      result = result * 59 + ($customName == null ? 43 : $customName.hashCode());
      Object $tntBuilder = this.getTntBuilder();
      result = result * 59 + ($tntBuilder == null ? 43 : $tntBuilder.hashCode());
      Object $spawnerForm = this.getSpawnerForm();
      result = result * 59 + ($spawnerForm == null ? 43 : $spawnerForm.hashCode());
      return result;
   }

   @Generated
   public String toString() {
      String var10000 = this.getName();
      return "CustomTNT(name=" + var10000 + ", explosionRadius=" + this.getExplosionRadius() + ", obsidianChance=" + this.getObsidianChance() + ", spawnerChance=" + this.getSpawnerChance() + ", spawnerMobSaveChance=" + this.getSpawnerMobSaveChance() + ", goldSpawnerChance=" + this.getGoldSpawnerChance() + ", mysteriousSpawnerChance=" + this.getMysteriousSpawnerChance() + ", blocksRadius=" + this.getBlocksRadius() + ", liquidChance=" + this.getLiquidChance() + ", fuseTicks=" + this.getFuseTicks() + ", iceRadius=" + this.getIceRadius() + ", waterChance=" + this.getWaterChance() + ", breakPSRegion=" + this.isBreakPSRegion() + ", ice=" + this.isIce() + ", iceDelay=" + this.getIceDelay() + ", customNameVisible=" + this.isCustomNameVisible() + ", customName=" + this.getCustomName() + ", tntBuilder=" + String.valueOf(this.getTntBuilder()) + ", spawnerForm=" + String.valueOf(this.getSpawnerForm()) + ")";
   }
}
