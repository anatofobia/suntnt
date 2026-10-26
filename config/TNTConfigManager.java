package ru.loper.suntnt.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.config.ConfigManager;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.api.items.ItemBuilder;
import ru.loper.suncore.utils.Colorize;

public class TNTConfigManager extends ConfigManager {
   private String giveSenderMessage;
   private String givePlayerMessage;
   private String addCustomItemMessage;
   private String removeCustomItemMessage;
   private String errorCommandMessage;
   private String errorItemMessage;
   private String offlineTargetMessage;
   private String invalidTntMessage;
   private String noPermissionsMessage;
   private boolean disableItemExplosion;
   private boolean disableSpawnerExplosion;
   private boolean disableProtectionBlocksExplosion;
   private boolean defaultCustomNameVisible;
   private String defaultCustomName;
   private Map<EntityType, String> entityTranslations;
   private NamespacedKey tntGunNamespacedKey;
   private ItemBuilder tntGunBuilder;
   private boolean tntGunRegionBreak;
   private double tntGunSpeed;
   private NamespacedKey blockedItemKey;
   private List<Integer> tntGunBlockedSlots;
   private ItemBuilder tntGunBlockedItem;

   public TNTConfigManager(Plugin plugin) {
      super(plugin);
   }

   public void loadConfigs() {
      this.plugin.saveDefaultConfig();
      this.addCustomConfig(new CustomConfig("customItems", true, this.plugin));
      this.addCustomConfig(new CustomConfig("translation", this.plugin));
      this.blockedItemKey = new NamespacedKey(this.plugin, "blocked-item");
      this.tntGunNamespacedKey = new NamespacedKey(this.plugin, "tnt-gun");
   }

   public void loadValues() {
      this.entityTranslations = new HashMap();
      ConfigurationSection entityTranslationsSection = this.getTranslationConfig().getConfig().getConfigurationSection("entities");
      if (entityTranslationsSection != null) {
         for(String key : entityTranslationsSection.getKeys(false)) {
            try {
               EntityType entityType = EntityType.valueOf(key);
               this.entityTranslations.put(entityType, entityTranslationsSection.getString(key));
            } catch (IllegalArgumentException var6) {
               this.plugin.getLogger().warning("Invalid entity type: " + key);
            }
         }
      }

      this.giveSenderMessage = this.getConfigMessage("messages.give-sender");
      this.givePlayerMessage = this.getConfigMessage("messages.give-player");
      this.addCustomItemMessage = this.getConfigMessage("messages.add-custom-item");
      this.removeCustomItemMessage = this.getConfigMessage("messages.remove-custom-item");
      this.errorCommandMessage = this.getConfigMessage("messages.error-command");
      this.errorItemMessage = this.getConfigMessage("messages.error-item");
      this.offlineTargetMessage = this.getConfigMessage("messages.offline-target");
      this.invalidTntMessage = this.getConfigMessage("messages.invalid-tnt");
      this.noPermissionsMessage = this.getConfigMessage("messages.no-permissions");
      ConfigurationSection settingsSection = this.plugin.getConfig().getConfigurationSection("settings");
      if (settingsSection != null) {
         this.disableItemExplosion = settingsSection.getBoolean("disable_item_explosion", false);
         this.disableSpawnerExplosion = settingsSection.getBoolean("disable_spawner_explosion", false);
         this.disableProtectionBlocksExplosion = settingsSection.getBoolean("disable_protection_blocks_explosion", false);
         this.defaultCustomNameVisible = settingsSection.getBoolean("default_custom_name.visible", false);
         this.defaultCustomName = Colorize.parse(settingsSection.getString("default_custom_name.name", ""));
      } else {
         this.disableItemExplosion = false;
         this.defaultCustomNameVisible = false;
         this.disableProtectionBlocksExplosion = false;
         this.defaultCustomName = "";
      }

      ConfigurationSection tntGunSection = this.plugin.getConfig().getConfigurationSection("tnt_gun");
      if (tntGunSection != null) {
         this.tntGunSpeed = tntGunSection.getDouble("speed");
         this.tntGunRegionBreak = tntGunSection.getBoolean("region_break");
         ConfigurationSection itemBuilderSection = tntGunSection.getConfigurationSection("item");
         this.tntGunBuilder = itemBuilderSection == null ? new ItemBuilder(Material.DISPENSER) : ItemBuilder.fromConfig(itemBuilderSection);
         this.tntGunBuilder.namespacedKey(this.tntGunNamespacedKey, PersistentDataType.STRING, "value");
         ConfigurationSection tntGunBlockedItemSection = tntGunSection.getConfigurationSection("blocked_item");
         this.tntGunBlockedItem = tntGunBlockedItemSection == null ? new ItemBuilder(Material.RED_STAINED_GLASS_PANE) : ItemBuilder.fromConfig(tntGunBlockedItemSection);
         this.tntGunBlockedItem.namespacedKey(this.blockedItemKey, PersistentDataType.BYTE, (byte)0);
         this.tntGunBlockedSlots = tntGunSection.getIntegerList("blocked_slots");
      } else {
         this.tntGunSpeed = (double)1.0F;
         this.tntGunRegionBreak = true;
         this.tntGunBuilder = new ItemBuilder(Material.DISPENSER);
         this.tntGunBuilder.namespacedKey(this.tntGunNamespacedKey, PersistentDataType.STRING, "value");
         this.tntGunBlockedItem = new ItemBuilder(Material.RED_STAINED_GLASS_PANE);
         this.tntGunBlockedItem.namespacedKey(this.blockedItemKey, PersistentDataType.BYTE, (byte)0);
         this.tntGunBlockedSlots = List.of(0, 1, 2, 3, 5, 6, 7, 8);
      }

   }

   public String getConfigMessage(String path) {
      return Colorize.parse(this.plugin.getConfig().getString(path, "unknown path " + path));
   }

   public CustomConfig getTranslationConfig() {
      return this.getCustomConfig("translation");
   }

   public String getEntityTranslation(EntityType entityType) {
      return (String)this.entityTranslations.getOrDefault(entityType, entityType.name().toLowerCase());
   }

   public boolean isTNTGunItem(ItemStack itemStack) {
      return itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(this.tntGunNamespacedKey, PersistentDataType.STRING);
   }

   @Generated
   public String getGiveSenderMessage() {
      return this.giveSenderMessage;
   }

   @Generated
   public String getGivePlayerMessage() {
      return this.givePlayerMessage;
   }

   @Generated
   public String getAddCustomItemMessage() {
      return this.addCustomItemMessage;
   }

   @Generated
   public String getRemoveCustomItemMessage() {
      return this.removeCustomItemMessage;
   }

   @Generated
   public String getErrorCommandMessage() {
      return this.errorCommandMessage;
   }

   @Generated
   public String getErrorItemMessage() {
      return this.errorItemMessage;
   }

   @Generated
   public String getOfflineTargetMessage() {
      return this.offlineTargetMessage;
   }

   @Generated
   public String getInvalidTntMessage() {
      return this.invalidTntMessage;
   }

   @Generated
   public String getNoPermissionsMessage() {
      return this.noPermissionsMessage;
   }

   @Generated
   public boolean isDisableItemExplosion() {
      return this.disableItemExplosion;
   }

   @Generated
   public boolean isDisableSpawnerExplosion() {
      return this.disableSpawnerExplosion;
   }

   @Generated
   public boolean isDisableProtectionBlocksExplosion() {
      return this.disableProtectionBlocksExplosion;
   }

   @Generated
   public boolean isDefaultCustomNameVisible() {
      return this.defaultCustomNameVisible;
   }

   @Generated
   public String getDefaultCustomName() {
      return this.defaultCustomName;
   }

   @Generated
   public Map<EntityType, String> getEntityTranslations() {
      return this.entityTranslations;
   }

   @Generated
   public NamespacedKey getTntGunNamespacedKey() {
      return this.tntGunNamespacedKey;
   }

   @Generated
   public ItemBuilder getTntGunBuilder() {
      return this.tntGunBuilder;
   }

   @Generated
   public boolean isTntGunRegionBreak() {
      return this.tntGunRegionBreak;
   }

   @Generated
   public double getTntGunSpeed() {
      return this.tntGunSpeed;
   }

   @Generated
   public NamespacedKey getBlockedItemKey() {
      return this.blockedItemKey;
   }

   @Generated
   public List<Integer> getTntGunBlockedSlots() {
      return this.tntGunBlockedSlots;
   }

   @Generated
   public ItemBuilder getTntGunBlockedItem() {
      return this.tntGunBlockedItem;
   }
}
