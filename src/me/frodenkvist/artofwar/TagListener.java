package me.frodenkvist.artofwar;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

public class TagListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onNameTag(PlayerReceiveNameTagEvent event)
	{
		//Bukkit.getServer().broadcastMessage("THINGS!");
		WarPlayer wp = WarHandler.getWarPlayer(event.getNamedPlayer().getName());
		Arena a = wp.getArena();
		if(wp.getWarID() != 0)
		{
			WarPlayer wp2 = WarHandler.getWarPlayer(event.getPlayer().getName());
			if(wp2.getWarID() == wp.getWarID())
			{
				if(wp.isAttacking())
				{
					event.setTag(ChatColor.RED + event.getNamedPlayer().getName());
					//TagAPI.refreshPlayer(event.getNamedPlayer());
					//Bukkit.getServer().broadcastMessage("RED!");
				}
				else
				{
					event.setTag(ChatColor.BLUE + event.getNamedPlayer().getName());
					//TagAPI.refreshPlayer(event.getNamedPlayer());
					//Bukkit.getServer().broadcastMessage("BLUE!");
				}
			}
			else
				return;
			
		}
		else if(a != null)
		{
			if(a.isAboutToEnd() && a.isInArena(event.getNamedPlayer()))
			{
				event.setTag(ChatColor.RESET + event.getNamedPlayer().getName());
			}
			else if(a.isInArena(event.getNamedPlayer()))
			{
				if(a.getAttackingTeam().contains(event.getNamedPlayer()))
				{
					event.setTag(ChatColor.RED + event.getNamedPlayer().getName());
				}
				else
				{
					event.setTag(ChatColor.BLUE + event.getNamedPlayer().getName());
					//TagAPI.refreshPlayer(event.getNamedPlayer());
					//Bukkit.getServer().broadcastMessage("BLUE!");
				}
			}
		}
	}
}
