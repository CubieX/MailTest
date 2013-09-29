package com.github.CubieX.MailTest;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MTMailHandler
{
   MTSchedulerHandler schedHandler = null;
   MailTest plugin = null;

   public MTMailHandler(MailTest plugin, MTSchedulerHandler schedHandler)
   {
      this.plugin = plugin;
      this.schedHandler = schedHandler;
   }

   /**
    * Sends an eMail to configured receiver
    * This is done in an async task!
    * 
    * @param sender The player who issues the sending command
    * 
    * */
   public void sendMailAsync(final CommandSender sender)
   {
      plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
      {
         @Override
         public void run()
         {
            // Recipient's email
            String to = MailTest.receiverMailAddress;

            // Sender's email
            String from = MailTest.senderMailAddress;

            // Get system properties
            Properties properties = System.getProperties();

            // Setup mail server
            properties.setProperty("mail.smtp.host", MailTest.smtpHostName);

            // create a session with an Authenticator
            Session session = Session.getInstance(properties, new Authenticator()
            {
               @Override
               protected PasswordAuthentication getPasswordAuthentication()
               {
                  return new PasswordAuthentication(MailTest.userName, MailTest.password);
               }
            });

            try
            {
               // Create a default MimeMessage object.
               MimeMessage message = new MimeMessage(session);

               // Set From: header field of the header.
               message.setFrom(new InternetAddress(from));

               // Set To: header field of the header.
               message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

               // Set Subject: header field
               message.setSubject(MailTest.logPrefix + "Das ist ein Betreff!");

               // Set message (HTML possible -> use <html>content</html>)
               message.setText("<html>Hi! Das ist eine Nachricht.<br>2. Zeile.<br> unf 3. Zeile mit HTML gemacht.<br><b>Jetzt mal fett.</b></html>", "utf-8", "html");

               // Send message
               Transport.send(message);

               schedHandler.sendSyncMessage(sender, ChatColor.GREEN + "eMail erfolgreich an " + ChatColor.WHITE + to + ChatColor.GREEN + " gesedet!");         
            }
            catch (MessagingException mex)
            {
               schedHandler.sendSyncMessage(sender, ChatColor.RED + "Fehler beim Senden der eMail!");
               mex.printStackTrace();
            }           
         }
      });
   }
   
   public void sendSMSAsync(final CommandSender sender) // INFO: Sending SMS may take up to 1 minute!
   {
      plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
      {
         @Override
         public void run()
         {  
            // mail2sms Gateway mail address.
            String to = MailTest.mail2smsGateway;

            // Sender's email
            String from = MailTest.senderMailAddress;

            // Get system properties
            Properties properties = System.getProperties();

            // Setup mail server
            properties.setProperty("mail.smtp.host", MailTest.smtpHostName);

            // create a session with an Authenticator
            Session session = Session.getInstance(properties, new Authenticator()
            {
               @Override
               protected PasswordAuthentication getPasswordAuthentication()
               {
                  return new PasswordAuthentication(MailTest.userName, MailTest.password);
               }
            });

            try
            {               
               // Create a default MimeMessage object.
               MimeMessage message = new MimeMessage(session);

               // Set From: header field of the header.
               message.setFrom(new InternetAddress(from));

               // Set To: header field of the header.
               message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

               // Set Subject: for SMS77.de this is the 160 char message.
               // State Subject in first line because senders mobile phone number in received SMS will be random! 
               message.setSubject(MailTest.logPrefix + "Das ist eine Nachricht!\n2.Zeile?");

               // Set Message:  for SMS77.de this is the authentication and configuration info for the sms
               String msg = MailTest.mail2smsGatewayKey + "#" + MailTest.smsReceiverNumber + "#" + "basicplus#" + MailTest.senderMailAddress;
               message.setText(msg);
               if(MailTest.debug){sender.sendMessage(msg);}

               // Send message
               Transport.send(message);

               schedHandler.sendSyncMessage(sender, ChatColor.GREEN + "SMS erfolgreich an " + ChatColor.WHITE + MailTest.smsReceiverNumber + ChatColor.GREEN + " gesedet!");         
            }
            catch (MessagingException mex)
            {
               schedHandler.sendSyncMessage(sender, ChatColor.RED + "Fehler beim Senden der SMS!");
               mex.printStackTrace();
            }           
         }
      });
   }
}
