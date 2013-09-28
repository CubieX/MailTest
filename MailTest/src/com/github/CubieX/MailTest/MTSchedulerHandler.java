package com.github.CubieX.MailTest;

import org.bukkit.command.CommandSender;

public class MTSchedulerHandler
{
   private MailTest plugin = null;

   public MTSchedulerHandler(MailTest plugin)
   {
      this.plugin = plugin;
   }

   public void sendSyncMessage(final CommandSender sender, final String message)
   {
      plugin.getServer().getScheduler().runTask(plugin, new Runnable()
      {
         @Override
         public void run()
         {
            if(null != sender)
            {
               sender.sendMessage(message);  
            }            
         }
      });
   }
}
