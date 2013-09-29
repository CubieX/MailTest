/*
 * MailTest - A CraftBukkit plugin that can send eMails via some SMTP remote server
 * Copyright (C) 2013  CubieX
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not,
 * see <http://www.gnu.org/licenses/>.
 */
package com.github.CubieX.MailTest;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MailTest extends JavaPlugin
{
   public static final Logger log = Bukkit.getServer().getLogger();
   static final String logPrefix = "[MailTest] "; // Prefix to go in front of all log entries

   private MTCommandHandler comHandler = null;
   private MTConfigHandler cHandler = null;
   private MTMailHandler mailHandler = null;
   private MTEntityListener eListener = null;
   private MTSchedulerHandler schedHandler = null;
   
   // config values
   static boolean debug = false;
   static String smtpHostName = " ";         // every valid host where given user has access to will work.
   static String userName = " ";             // mail account on given host
   static String password = " ";             // password for given mail account
   static String senderMailAddress = " ";    // address to send FROM
   static String receiverMailAddress = " ";  // address to send TO -> must match the given mail2sms gateway hosts setup if SMS function is used
   
   static String mail2smsGateway = " ";      // gateway to convert email to SMS
   static String mail2smsGatewayKey = " ";   // key for gateway to ensure issuing sender is valid -> setup at given gateway host
   static String smsReceiverNumber = " ";    // mobile phone number of receiver of the SMS

   //*************************************************
   static String usedConfigVersion = "1"; // Update this every time the config file version changes, so the plugin knows, if there is a suiting config present
   //*************************************************

   @Override
   public void onEnable()
   {            
      cHandler = new MTConfigHandler(this);
      // FIXME wie die java.mail jar ins Plugin includen oder so machen, dass Bukkit sie findet beim start?? (lib-Verzeichnis z.B.)???
      if(!checkConfigFileVersion())
      {
         log.severe(logPrefix + "Outdated or corrupted config file(s). Please delete your config files."); 
         log.severe(logPrefix + "will generate a new config for you.");
         log.severe(logPrefix + "will be disabled now. Config file is outdated or corrupted.");
         getServer().getPluginManager().disablePlugin(this);
         return;
      }

      readConfigValues();

      schedHandler = new MTSchedulerHandler(this);
      mailHandler = new MTMailHandler(this, schedHandler);
      eListener = new MTEntityListener(this);
      comHandler = new MTCommandHandler(this, cHandler, mailHandler);      
      getCommand("mt").setExecutor(comHandler);      
     
      log.info(logPrefix + " version " + getDescription().getVersion() + " is enabled!");
   }   

   private boolean checkConfigFileVersion()
   {      
      boolean configOK = false;     

      if(cHandler.getConfig().isSet("config_version"))
      {
         String configVersion = getConfig().getString("config_version");

         if(configVersion.equals(usedConfigVersion))
         {
            configOK = true;
         }
      }

      return (configOK);
   }  

   public void readConfigValues()
   {
      boolean exceed = false;
      boolean invalid = false;

      if(getConfig().isSet("debug")){debug = getConfig().getBoolean("debug");}else{invalid = true;}
      
      // mail options
      if(getConfig().isSet("smtpHostName")){smtpHostName = getConfig().getString("smtpHostName");}else{invalid = true;}
      if(getConfig().isSet("userName")){userName = getConfig().getString("userName");}else{invalid = true;}
      if(getConfig().isSet("password")){password = getConfig().getString("password");}else{invalid = true;}
      if(getConfig().isSet("senderMailAddress")){senderMailAddress = getConfig().getString("senderMailAddress");}else{invalid = true;}
      if(getConfig().isSet("receiverMailAddress")){receiverMailAddress = getConfig().getString("receiverMailAddress");}else{invalid = true;}
      
      // mail2SMS options
      if(getConfig().isSet("mail2smsGateway")){mail2smsGateway = getConfig().getString("mail2smsGateway");}else{invalid = true;}
      if(getConfig().isSet("mail2smsGatewayKey")){mail2smsGatewayKey = getConfig().getString("mail2smsGatewayKey");}else{invalid = true;}
      if(getConfig().isSet("smsReceiverNumber")){smsReceiverNumber = getConfig().getString("smsReceiverNumber");}else{invalid = true;}
     
      if(exceed)
      {
         log.warning(logPrefix + "One or more config values are exceeding their allowed range. Please check your config file!");
      }

      if(invalid)
      {
         log.warning(logPrefix + "One or more config values are invalid. Please check your config file!");
      }
   }

   @Override
   public void onDisable()
   {     
      this.getServer().getScheduler().cancelTasks(this);
      cHandler = null;
      eListener = null;
      comHandler = null;
      mailHandler = null;
      schedHandler = null;
      log.info(logPrefix + "version " + getDescription().getVersion() + " is disabled!");
   }

   // #########################################################

   
}


