package com.github.CubieX.MailTest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
            /*Session session = Session.getInstance(properties, new Authenticator()
            {
               @Override
               protected PasswordAuthentication getPasswordAuthentication()
               {
                  return new PasswordAuthentication(MailTest.userName, MailTest.password);
               }
            });*/

            Session session = Session.getInstance(properties, null);

            try
            {
               Transport transport = session.getTransport("smtps");
               transport.connect(MailTest.smtpHostName, 465, MailTest.userName, MailTest.password);

               // Create a default MimeMessage object.
               MimeMessage message = new MimeMessage(session);

               // Set From: header field of the header.
               message.setFrom(new InternetAddress(from));

               // Set To: header field of the header.
               message.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); // multiple recipoents possible

               // Set Subject: header field
               message.setSubject(MailTest.logPrefix + "Das ist ein Betreff!");

               // Set message (HTML possible -> use <html>content</html>)
               message.setText("<html>Hi! Das ist eine Nachricht.<br>2. Zeile.<br> unf 3. Zeile mit HTML gemacht.<br><b>Jetzt mal fett.</b></html>", "utf-8", "html");

               // Send message
               //Transport.send(message);
               if(transport.isConnected())
               {
                  transport.sendMessage(message, message.getAllRecipients());
               }
               else
               {
                  sender.sendMessage(ChatColor.RED + "Fehler beim verbinden mit dem SMTP server!");
               }

               transport.close();

               schedHandler.sendSyncMessage(sender, ChatColor.GREEN + "eMail erfolgreich an " + ChatColor.WHITE + to + ChatColor.GREEN + " gesendet!");         
            }
            catch (MessagingException mex)
            {
               schedHandler.sendSyncMessage(sender, ChatColor.RED + "Fehler beim Senden der eMail!");
               mex.printStackTrace();
            }           
         }
      });
   }

   /*public void sendSMSAsync(final CommandSender sender) // INFO: Sending SMS may take up to 1 minute!
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
            properties.setProperty("mail.smtp.host", MailTest.smtpHostName);*/

   // create a session with an Authenticator
   /*Session session = Session.getInstance(properties, new Authenticator()
            {
               @Override
               protected PasswordAuthentication getPasswordAuthentication()
               {
                  return new PasswordAuthentication(MailTest.userName, MailTest.password);
               }
            });*/

   /*Session session = Session.getInstance(properties, null);

            try
            {
               Transport transport = session.getTransport("smtps");
               transport.connect(MailTest.smtpHostName, 465, MailTest.userName, MailTest.password);           
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
               //Transport.send(message);
               if(transport.isConnected())
               {
                  transport.sendMessage(message, message.getAllRecipients());
               }
               else
               {
                  sender.sendMessage(ChatColor.RED + "Fehler beim verbinden mit dem SMTP server!");
               }

               transport.close();

               schedHandler.sendSyncMessage(sender, ChatColor.GREEN + "SMS erfolgreich an " + ChatColor.WHITE + MailTest.smsReceiverNumber + ChatColor.GREEN + " gesendet!");         
            }
            catch (MessagingException mex)
            {
               schedHandler.sendSyncMessage(sender, ChatColor.RED + "Fehler beim Senden der SMS!");
               mex.printStackTrace();
            }
         }
      });
   }*/

   public void sendSMShttpAsync(final CommandSender sender, String message)
   {
      final String msgToSend;

      if((null != message) && !message.isEmpty())
      {
         message.trim();
         
         if(message.length() <= 160)
         {
            msgToSend = message;
         }
         else
         {
            sender.sendMessage(ChatColor.RED + "Die SMS darf max. 160 Zeichen haben!\nAktuell hat sie: " + ChatColor.WHITE + message.length());
            return;
         }
      }
      else
      {
         sender.sendMessage(ChatColor.RED + "Die SMS muss Text enthalten!");
         return;
      }

      plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
      {
         @Override
         public void run()
         {
            String targetURL = MailTest.gatewayURL;
            String urlParameters = "";

            try
            {
               urlParameters = "/?u=" + URLEncoder.encode(MailTest.gatewayUser, "UTF-8") +
                     "&p=" + URLEncoder.encode(MailTest.gatewayPassword, "UTF-8") +
                     "&to=" + MailTest.smsReceiverNumber +
                     "&text=" + URLEncoder.encode(msgToSend, "UTF-8") +
                     "&type=" + URLEncoder.encode("basicplus", "UTF-8") + 
                     "&from=" + URLEncoder.encode(MailTest.senderMailAddress, "UTF-8") + 
                     //"&debug=1" +
                     "&utf8=1"; // password may be given as md5 for security reasons

               if(MailTest.debug){sender.sendMessage("Request: " + MailTest.gatewayURL + urlParameters);}
            }
            catch (UnsupportedEncodingException ex)
            {
               ex.printStackTrace();
               return;
            }

            URL url;
            HttpURLConnection connection = null;

            try
            {                              
               //Create connection               
               url = new URL(targetURL + urlParameters);
               connection = (HttpURLConnection)url.openConnection(); // will execute the request
               connection.setRequestMethod("GET");
               //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
               //connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
               //connection.setRequestProperty("Content-Language", "en-US");  

               connection.setUseCaches (false);
               connection.setDoInput(true);
               connection.setDoOutput(true);

               //Get Response 
               InputStream is = connection.getInputStream();
               BufferedReader rd = new BufferedReader(new InputStreamReader(is));
               String line;
               StringBuffer response = new StringBuffer(); 

               while((line = rd.readLine()) != null)
               {
                  response.append(line);
                  response.append('\r');
               }

               rd.close();               

               if(response.toString().startsWith("100")) // 100 is good, everything else is bad
               {
                  schedHandler.sendSyncMessage(sender, ChatColor.GREEN + "SMS erfolgreich an " + ChatColor.WHITE + MailTest.smsReceiverNumber + ChatColor.GREEN + " gesendet!");  
               }
               else
               {
                  schedHandler.sendSyncMessage(sender, ChatColor.RED + "Fehler beim Senden der SMS! Fehlercode: " + response.toString());
               }
            }
            catch (Exception e)
            {
               schedHandler.sendSyncMessage(sender, ChatColor.RED + "Fehler beim Senden der SMS!");
               e.printStackTrace();               
            }
            finally
            {
               if(connection != null)
               {
                  connection.disconnect(); 
               }
            }
         }
      });
   }
}
