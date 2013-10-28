package com.github.CubieX.MailTest;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MTCommandHandler implements CommandExecutor
{
   private MailTest plugin = null;
   private MTConfigHandler cHandler = null;
   private MTMailHandler mailHandler = null;

   public MTCommandHandler(MailTest plugin, MTConfigHandler cHandler, MTMailHandler mailHandler) 
   {
      this.plugin = plugin;
      this.cHandler = cHandler;
      this.mailHandler = mailHandler;
   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
   {
      Player player = null;

      if (sender instanceof Player) 
      {
         player = (Player) sender;
      }

      if (cmd.getName().equalsIgnoreCase("mt"))
      {
         if (args.length == 0)
         { //no arguments, so help will be displayed
            return false;
         }
         else if(args.length == 1)
         {
            if (args[0].equalsIgnoreCase("version"))
            {
               sender.sendMessage(MailTest.logPrefix + ChatColor.GREEN + "This server is running " + plugin.getDescription().getName() + " version " + plugin.getDescription().getVersion());
               return true;
            }

            if (args[0].equalsIgnoreCase("reload"))
            {
               if(sender.isOp() || sender.hasPermission("mailtest.admin"))
               {                        
                  cHandler.reloadConfig(sender);
                  return true;
               }
               else
               {
                  sender.sendMessage(ChatColor.RED + "You do not have sufficient permission to reload " + plugin.getDescription().getName() + "!");
               }
            }

            if (args[0].equalsIgnoreCase("sendmail"))
            {
               if(sender.isOp() || sender.hasPermission("mailtest.use"))
               {                  
                  mailHandler.sendMailAsync(sender);
               }
               else
               {
                  sender.sendMessage(ChatColor.RED + "You do not have sufficient permission to send eMails!");
               }

               return true;
            }
            
            /*if (args[0].equalsIgnoreCase("sendsmsviamail"))
            {
               if(sender.isOp() || sender.hasPermission("mailtest.use"))
               {                  
                  mailHandler.sendSMSAsync(sender);
               }
               else
               {
                  sender.sendMessage(ChatColor.RED + "You do not have sufficient permission to send an SMS!");
               }

               return true;
            }*/
            
            if (args[0].equalsIgnoreCase("sendsms"))
            {
               if(sender.isOp() || sender.hasPermission("mailtest.use"))
               {
                  String message = MailTest.smsSenderName + "\r" + "Test!\rNeue Linie!\r§$%&/()=?_-.,;:öäüß#+\r/*1234567890";
                  
                  mailHandler.sendSMShttpAsync(sender, message);
               }
               else
               {
                  sender.sendMessage(ChatColor.RED + "You do not have sufficient permission to send an SMS!");
               }

               return true;
            }
         }
         else
         {
            sender.sendMessage(ChatColor.YELLOW + "Falsche Parameteranzahl.");
         }                

      }         
      return false; // if false is returned, the help for the command stated in the plugin.yml will be displayed to the player
   }
}
