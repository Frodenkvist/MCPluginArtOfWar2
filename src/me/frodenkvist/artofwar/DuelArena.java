package me.frodenkvist.artofwar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;

public class DuelArena extends CuboidArea
{
	private Location spawn1;
	private Location spawn2;
	private Location startSpawn1;
	private Location startSpawn2;
	private boolean running = false;
	private DuelingPair dueling;
	private List<DuelingPair> pairs = new ArrayList<DuelingPair>();
	
	public DuelArena(Location start, Location end)
	{
		super(start,end);
	}
	
	public void setSpawn1(Location loc)
	{
		spawn1 = loc;
	}
	
	public void setSpawn2(Location loc)
	{
		spawn2 = loc;
	}
	
	public void setStartSpawn1(Location loc)
	{
		startSpawn1 = loc;
	}
	
	public void setStartSpawn2(Location loc)
	{
		startSpawn2 = loc;
	}
	
	public boolean addPair(DuelingPair dp)
	{
		return pairs.add(dp);
	}
	
	public void startDuel(final DuelingPair dp)
	{
		dp.getPlayer1().teleport(spawn1);
		dp.getPlayer2().teleport(spawn2);
		dp.getPlayer1().getInventory().clear();
		dp.getPlayer1().getInventory().setHelmet(null);
		dp.getPlayer1().getInventory().setChestplate(null);
		dp.getPlayer1().getInventory().setLeggings(null);
		dp.getPlayer1().getInventory().setBoots(null);
		dp.getPlayer2().getInventory().clear();
		dp.getPlayer2().getInventory().setHelmet(null);
		dp.getPlayer2().getInventory().setChestplate(null);
		dp.getPlayer2().getInventory().setLeggings(null);
		dp.getPlayer2().getInventory().setBoots(null);
		dp.sendMessage(ChatColor.GREEN + "The Duel Will Start In 10 Seconds");
		dp.getPlayer1().setHealth(20);
		dp.getPlayer2().setHealth(20);
		dp.getPlayer1().setFoodLevel(20);
		dp.getPlayer1().setSaturation(20);
		dp.getPlayer2().setFoodLevel(20);
		dp.getPlayer2().setSaturation(20);
		Bukkit.getScheduler().scheduleSyncDelayedTask(ArtOfWar.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				dp.getPlayer1().teleport(startSpawn1);
				dp.getPlayer2().teleport(startSpawn2);
				FireworkEffectPlayer fep = new FireworkEffectPlayer();
				try
				{
					fep.playFirework(dp.getPlayer1().getWorld(), dp.getPlayer1().getLocation(), FireworkEffect.builder().withColor(Color.SILVER).with(Type.BALL_LARGE).build());
					fep.playFirework(dp.getPlayer1().getWorld(), dp.getPlayer2().getLocation(), FireworkEffect.builder().withColor(Color.SILVER).with(Type.BALL_LARGE).build());
				}
				catch (Exception e)
				{
					
				}
			}
		},20L*15);
		dueling = dp;
		running = true;
		WarHandler.getWarPlayer(dp.getPlayer1().getName()).setDueling(true);
		WarHandler.getWarPlayer(dp.getPlayer2().getName()).setDueling(true);
	}
	
	public void tryStartDuel(DuelingPair dp)
	{
		if(running)
		{
			pairs.add(dp);
			dp.sendMessage(ChatColor.GREEN + "The Arena Is Occupied, You Have Been Added To The Queue, Your Spot Is: " + ChatColor.AQUA + pairs.size());
			return;
		}
		startDuel(dp);
	}
	
	public DuelingPair getDueling()
	{
		return dueling;
	}
	
	public void startNext()
	{
		dueling = null;
		while(!pairs.isEmpty())
		{
			dueling = pairs.get(0);
			pairs.remove(dueling);
			if(dueling.getPlayer1() == null || dueling.getPlayer2() == null)
				continue;
			break;
		}
		if(pairs.isEmpty())
		{
			running = false;
			return;
		}
		for(int i=0;i<pairs.size();++i)
		{
			DuelingPair dp = pairs.get(i);
			dp.sendMessage(ChatColor.GREEN + "Your Spot In The Queue Is: " + ChatColor.AQUA + (i+1));
		}
		dueling.sendMessage(ChatColor.GREEN + "Get Ready! Your Duel Starts In 30 Seconds!");
		Bukkit.getScheduler().scheduleSyncDelayedTask(ArtOfWar.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				startDuel(dueling);
			}
		},20L*30);
	}
}
