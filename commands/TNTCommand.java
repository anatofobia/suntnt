package ru.loper.suntnt.commands;

import org.bukkit.permissions.Permission;
import ru.loper.suncore.api.command.AdvancedSmartCommandExecutor;
import ru.loper.suntnt.SunTNT;
import ru.loper.suntnt.commands.impl.CustomItemsArgument;
import ru.loper.suntnt.commands.impl.GiveArgument;
import ru.loper.suntnt.commands.impl.TNTGunGiveArgument;
import ru.loper.suntnt.config.TNTConfigManager;

public class TNTCommand extends AdvancedSmartCommandExecutor {
   private final TNTConfigManager configManager;

   public TNTCommand(SunTNT plugin) {
      this.configManager = plugin.getConfigManager();
      this.addSubCommand(new CustomItemsArgument(this.configManager), new Permission("suntnt.command.customitems"), "customitems", new String[0]);
      this.addSubCommand(new GiveArgument(plugin.getTntManager()), new Permission("suntnt.command.give"), "give", new String[0]);
      this.addSubCommand(new TNTGunGiveArgument(plugin.getConfigManager()), new Permission("suntnt.command.givegun"), "givegun", new String[0]);
   }

   public String getDontPermissionMessage() {
      return this.configManager.getNoPermissionsMessage();
   }
}
