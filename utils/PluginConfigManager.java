package ru.loper.suntnt.utils;

import lombok.Generated;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.config.ConfigManager;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.utils.Colorize;

public class PluginConfigManager extends ConfigManager {
   private String giveSender;
   private String givePlayer;
   private String addCustomItem;
   private String removeCustomItem;
   private String errorCommand;
   private String errorItem;
   private String offlineTarget;
   private String invalidTnt;
   private String noPermissions;

   public PluginConfigManager(Plugin plugin) {
      super(plugin);
   }

   public void loadConfigs() {
      this.addCustomConfig(new CustomConfig("customItems", this.plugin));
   }

   public void loadValues() {
      this.giveSender = this.getConfigMessage("messages.give-sender");
      this.givePlayer = this.getConfigMessage("messages.give-player");
      this.addCustomItem = this.getConfigMessage("messages.add-custom-item");
      this.removeCustomItem = this.getConfigMessage("messages.remove-custom-item");
      this.errorCommand = this.getConfigMessage("messages.error-command");
      this.errorItem = this.getConfigMessage("messages.error-item");
      this.offlineTarget = this.getConfigMessage("messages.offline-target");
      this.invalidTnt = this.getConfigMessage("messages.invalid-tnt");
      this.noPermissions = this.getConfigMessage("messages.no-permissions");
   }

   public String getConfigMessage(String path) {
      return Colorize.parse(this.plugin.getConfig().getString(path, "unknown path " + path));
   }

   @Generated
   public String getGiveSender() {
      return this.giveSender;
   }

   @Generated
   public String getGivePlayer() {
      return this.givePlayer;
   }

   @Generated
   public String getAddCustomItem() {
      return this.addCustomItem;
   }

   @Generated
   public String getRemoveCustomItem() {
      return this.removeCustomItem;
   }

   @Generated
   public String getErrorCommand() {
      return this.errorCommand;
   }

   @Generated
   public String getErrorItem() {
      return this.errorItem;
   }

   @Generated
   public String getOfflineTarget() {
      return this.offlineTarget;
   }

   @Generated
   public String getInvalidTnt() {
      return this.invalidTnt;
   }

   @Generated
   public String getNoPermissions() {
      return this.noPermissions;
   }
}
