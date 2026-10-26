package ru.loper.suntnt.commands.arguments;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.loper.suncore.api.command.SubCommand;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.utils.Colorize;
import ru.loper.suntnt.utils.PluginConfigManager;

public class CustomItemsArgument implements SubCommand {
   private final PluginConfigManager configManager;

   public void onCommand(CommandSender sender, String[] args) {
      if (args.length < 3) {
         sender.sendMessage(Colorize.parse("&c ▶ &fИспользование: /tnt customitems [give/add/remove] [tnt] [player] [amount]"));
      } else {
         switch (args[1].toLowerCase()) {
            case "give" -> this.handleGiveCustomItem(sender, args);
            case "add" -> this.handleAddCustomItem(sender, args);
            case "remove" -> this.handleRemoveCustomItem(sender, args);
         }

      }
   }

   private void handleAddCustomItem(CommandSender sender, String[] args) {
      if (args.length < 3) {
         sender.sendMessage(Colorize.parse("&c ▶ &fНапишите название предмета"));
      } else {
         ConfigurationSection itemsSection = this.getItemsSection();
         if (!itemsSection.contains(args[2])) {
            sender.sendMessage(Colorize.parse("&c ▶ &fДанного предмета не существует"));
         } else {
            CustomConfig itemsConfig = this.configManager.getCustomConfig("customItems");
            itemsConfig.getConfig().set("items." + args[2], (Object)null);
            itemsConfig.saveConfig();
            sender.sendMessage(Colorize.parse("&d ▶ &fВы успешно удалили кастомный предмет %s из конфига".formatted(args[2])));
         }
      }
   }

   private void handleRemoveCustomItem(CommandSender sender, String[] args) {
      if (sender instanceof Player player) {
         if (args.length < 3) {
            sender.sendMessage(Colorize.parse("&c ▶ &fНапишите название предмета"));
         } else {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack.getType().equals(Material.AIR)) {
               sender.sendMessage(Colorize.parse("&c ▶ &fВозьмите в руку предмет, который хотите добавить"));
            } else {
               CustomConfig itemsConfig = this.configManager.getCustomConfig("customItems");
               itemsConfig.getConfig().set("items." + args[2], itemStack);
               itemsConfig.saveConfig();
               sender.sendMessage(Colorize.parse("&d ▶ &fВы успешно добавили кастомный предмет %s в конфиг".formatted(itemStack.getItemMeta().getDisplayName())));
            }
         }
      } else {
         sender.sendMessage(Colorize.parse("&cДанная команда доступна только игрокам"));
      }
   }

   private void handleGiveCustomItem(CommandSender sender, String[] args) {
      ConfigurationSection itemsSection = this.getItemsSection();
      if (itemsSection == null) {
         sender.sendMessage(Colorize.parse("&c ▶ &fОшибка получения предметов из конфига"));
      } else {
         ItemStack itemStack = itemsSection.getItemStack(args[2]);
         if (itemStack == null) {
            sender.sendMessage(Colorize.parse("&c ▶ &fДанного предмета не существует"));
         } else {
            Player player = this.resolveTargetPlayer(sender, args);
            if (player != null) {
               player.getInventory().addItem(new ItemStack[]{itemStack.clone()});
               sender.sendMessage(Colorize.parse(String.format("&a ▶ &fВыдан предмет &e%s &fигроку &e%s", itemStack.getItemMeta().getDisplayName(), player.getName())));
            }
         }
      }
   }

   private Player resolveTargetPlayer(CommandSender sender, String[] args) {
      if (args.length < 4) {
         if (!(sender instanceof Player)) {
            sender.sendMessage(Colorize.parse("&cДанная команда доступна только игрокам"));
            return null;
         } else {
            return (Player)sender;
         }
      } else {
         Player player = Bukkit.getPlayer(args[3]);
         if (player == null) {
            sender.sendMessage(Colorize.parse("&c ▶ &fУказанный игрок не найден или не в сети"));
            return null;
         } else {
            return player;
         }
      }
   }

   private ConfigurationSection getItemsSection() {
      return this.configManager.getCustomConfig("customItems").getConfig().getConfigurationSection("items");
   }

   public List<String> onTabCompleter(CommandSender sender, String[] args) {
      List<String> completions = new ArrayList();
      if (args.length == 2) {
         completions.addAll(List.of("give", "remove", "add"));
      } else if (args.length == 3) {
         switch (args[1].toLowerCase()) {
            case "give":
            case "remove":
               ConfigurationSection itemsSection = this.getItemsSection();
               if (itemsSection != null) {
                  completions.addAll(itemsSection.getKeys(false));
               }
               break;
            case "add":
               completions.add("name");
         }
      } else if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
         completions.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList());
      } else if (args.length == 5 && args[0].equalsIgnoreCase("give")) {
         completions.addAll(List.of("1", "8", "16", "32", "64"));
      }

      String currentArg = args[args.length - 1].toLowerCase();
      return completions.stream().filter((s) -> s.toLowerCase().startsWith(currentArg)).toList();
   }

   @Generated
   public CustomItemsArgument(PluginConfigManager configManager) {
      this.configManager = configManager;
   }
}
