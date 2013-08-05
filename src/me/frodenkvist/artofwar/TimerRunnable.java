package me.frodenkvist.artofwar;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TimerRunnable implements Runnable
{
	private long TimeLimit;
	private Arena arena;
	
	public TimerRunnable(long TimeLimit,Arena a)
	{
		this.TimeLimit = TimeLimit;
		arena = a;
	}
	
	@Override
	public void run()
	{
		if(TimeLimit == 0)
		{
			arena.defenderWin();
			return;
		}
		for(Player p : arena.getAttackingTeam())
		{
			p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Your Team Has " + ChatColor.AQUA + TimeLimit + ChatColor.GREEN + " Minutes Left To Reach Your Goal!");
		}
		for(Player p : arena.getDefendingTeam())
		{
			p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "The Attackers Has " + ChatColor.AQUA + TimeLimit + ChatColor.GREEN + " Minutes Left To Reach Their Goal!");
		}
		for(Player p : arena.getSpectaters())
		{
			p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "The Attackers Has " + ChatColor.AQUA + TimeLimit + ChatColor.GREEN + " Minutes Left To Reach Their Goal!");
		}
		
		arena.setTimerID(ArtOfWar.plugin.getServer().getScheduler().scheduleSyncDelayedTask(ArtOfWar.plugin, new TimerRunnable(TimeLimit-1,arena),20L*60L));
	}
}
