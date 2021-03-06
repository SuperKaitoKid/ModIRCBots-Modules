package ircmodbot.module;
import ircmodbot.Module;
import ircmodbot.OpHelp;

import java.util.ArrayList;

import org.jibble.pircbot.User;

/**
 * Allows everything that the user says to be quoted.
 * Used with QuoteMod.
 * 
 * Demonstrates how one mod can work with another.
 * @author Charles
 *
 */
public class TQuoteMod extends Module
{
   private ArrayList<String>user;
   private ArrayList<Integer>chances;
   private QuoteMod qm;
   private int MAX_PHASE;
   private String TARGET_PHRASE;

   public TQuoteMod(QuoteMod qMod)
   {
      super("");
      qm = qMod;
      moduleName = "Target Quoting Mod";

      MAX_PHASE = 5;
      TARGET_PHRASE = "TQM";

      user = new ArrayList<String>();
      chances = new ArrayList<Integer>();
   }

   public void onMessage(String channel, String sender,
      String login, String hostname, String message, ArrayList<String> cmd)
   {
	  
      if(cmd.get(0).equalsIgnoreCase(TARGET_PHRASE))
      {
    	 if(cmd.size() < 2)
    		 return;
    	 String username = cmd.get(1);
    	 if(target(username, channel))
         {
            bot.sendNotice(sender,username + " has now been targeted.");
         }
         
      }
      else
      {
         int index = user.indexOf(sender);
         if(index != -1)
         {
            chances.add(index, chances.get(index) - 1);
            String newMessage = sender + " " + message;
            qm.displayQuote(sender, newMessage, channel);
            if(chances.get(index) <= 0)
            {
               bot.sendNotice(sender,user.get(index)+" has been lost "
                  + "from targeting.");
               chances.remove(index);
               user.remove(index);
            }
         }
      }
   }

   public void onPrivateMessage(String sender, String login, 
      String hostname, String message, ArrayList<String> cmd)
   {
      return ;
   }

   public void onJoin(String channel,String sender, 
      String login,String hostname)
   {
      return ;
   }

   public boolean target(String name, String channel)
   {
      if(user.indexOf(name) != -1)
      {
         bot.sendMessage(channel, "Cannot target a player that "
               + "is already targeted. ");
         return false;
      }
      else if(name.equals(bot.getNick()))
      {
         bot.sendMessage(channel, "No.");
         return false;
      }

      User[] names = bot.getUsers(channel);
      for(int x = 0; x < names.length; ++x)
      {
         if(names[x].getNick().equals(name))
         {
            user.add(name);
            chances.add(MAX_PHASE);
            return true;
         }
      }

      bot.sendMessage(bot.getChannelName(), name + " has not been found.");
      return false;
   }
}