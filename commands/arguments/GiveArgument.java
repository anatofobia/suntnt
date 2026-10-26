package ru.loper.suntnt.commands.arguments;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.loper.suncore.api.command.SubCommand;
import ru.loper.suncore.api.items.ItemBuilder;
import ru.loper.suncore.utils.Colorize;
import ru.loper.suntnt.tnt.CustomTNT;
import ru.loper.suntnt.tnt.TNTManager;

public class GiveArgument implements SubCommand {
   private final TNTManager tntManager;

   public void onCommand(CommandSender sender, String[] args) {
      if (args.length < 2) {
         sender.sendMessage(Colorize.parse("&c ▶ &fИспользование: /tnt give [tnt] [player] [amount]"));
      } else {
         CustomTNT customTNT = this.tntManager.getCustomTNT(args[1]);
         if (customTNT == null) {
            sender.sendMessage(Colorize.parse("&c ▶ &fДанного динамита не существует"));
         } else {
            Player player = this.resolveTargetPlayer(sender, args);
            if (player != null) {
               int amount = this.resolveAmount(args);
               if (amount <= 0) {
                  sender.sendMessage(Colorize.parse("&c ▶ &fНекорректное количество предметов"));
               }

               ItemBuilder itemBuilder = customTNT.getTntBuilder();
               ItemStack item = itemBuilder.build();
               item.setAmount(amount);
               player.getInventory().addItem(new ItemStack[]{item});
               sender.sendMessage(Colorize.parse(String.format("&a ▶ &fВыдан динамит &e%s &fигроку &e%s", itemBuilder.name(), player.getName())));
            }
         }
      }
   }

   private Player resolveTargetPlayer(CommandSender sender, String[] args) {
      if (args.length < 2) {
         if (!(sender instanceof Player)) {
            sender.sendMessage(Colorize.parse("&cДанная команда доступна только игрокам"));
            return null;
         } else {
            return (Player)sender;
         }
      } else {
         Player player = Bukkit.getPlayer(args[2]);
         if (player == null) {
            sender.sendMessage(Colorize.parse("&c ▶ &fУказанный игрок не найден или не в сети"));
            return null;
         } else {
            return player;
         }
      }
   }

   private int resolveAmount(String[] args) {
      if (args.length < 4) {
         return 1;
      } else {
         try {
            return Math.max(1, Integer.parseInt(args[3]));
         } catch (NumberFormatException var3) {
            return -1;
         }
      }
   }

   public List<String> onTabCompleter(CommandSender commandSender, String[] args) {
      List<String> completions = new ArrayList();
      if (args.length == 2) {
         completions.addAll(this.tntManager.getCustomTNTsName());
      } else if (args.length == 3) {
         Bukkit.getOnlinePlayers().forEach((player) -> completions.add(player.getName()));
      } else if (args.length == 4) {
         completions.addAll(List.of("1", "8", "16", "32", "64"));
      }

      String currentArg = args[args.length - 1].toLowerCase();
      return completions.stream().filter((s) -> s.toLowerCase().startsWith(currentArg)).toList();
   }

   @Generated
   public GiveArgument(TNTManager tntManager) {
      this.tntManager = tntManager;
   }
}
