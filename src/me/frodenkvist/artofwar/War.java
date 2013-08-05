package me.frodenkvist.artofwar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.kitteh.tag.TagAPI;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;

public class War
{
	private Town defendingTown;
	private Town attackingTown;
	//private double betAmount;
	private int ID;
	private int attackersBlockPlaceCount;
	private int attackersBlockBreakCount;
	private ArtOfWar plugin;
	private final int attackerWinValue;
	private final int defenderWinValue;
	private int attackerPointCount;
	private int defenderPointCount;
	private final int TNTAmount;
	private int TNTCounter;
	private Location attackerSpawn;
	private Location enderChestLoc;
	List<Resident> defendingResidents;
	List<Resident> attackingResidents;
	private Resident attackingMayor;
	private Resident defendingMayor;
	
	public War(ArtOfWar plugin,Town challangeTown,Town acceptTown,double betAmount)
	{
		this.plugin = plugin;
		if(Math.random() <= 0.5D)
		{
			defendingTown = challangeTown;
			attackingTown = acceptTown;
		}
		else
		{
			defendingTown = acceptTown;
			attackingTown = challangeTown;
		}
		
		attackingMayor = attackingTown.getMayor();
		defendingMayor = defendingTown.getMayor();
		
		ID = WarHandler.getWarID();
		//this.betAmount = betAmount;
		attackersBlockPlaceCount = 0;
		TNTAmount = plugin.getConfig().getInt("War.TNTAmount");
		TNTCounter = 0;
		defendingResidents = new ArrayList<Resident>();
		attackingResidents = new ArrayList<Resident>();
		
		for(Resident r: defendingTown.getResidents())
		{
			if(plugin.getServer().getPlayer(r.getName()) != null)
				defendingResidents.add(r);
		}
		if(defendingTown.hasNation())
		{
			Nation nation = null;
			try
			{
				nation = defendingTown.getNation();
			}
			catch(Exception e)
			{
			}
			
			for(Town t : nation.getTowns())
			{
				for(Resident r : t.getResidents())
				{
					if(!defendingResidents.contains(r))
					{
						if(plugin.getServer().getPlayer(r.getName()) != null)
						{
							WarPlayer wp = WarHandler.getWarPlayer(r.getName());
							if(wp.getAllyAccept())
							{
								wp.getPlayer().sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + "Your Town Is Defending!");
								defendingResidents.add(r);
							}
						}
					}
				}
				
			}
			
		}
		
		
		for(Resident r: attackingTown.getResidents())
		{
			if(plugin.getServer().getPlayer(r.getName()) != null)
				attackingResidents.add(r);
		}
		if(attackingTown.hasNation())
		{
			Nation nation = null;
			try
			{
				nation = attackingTown.getNation();
			}
			catch(Exception e)
			{
			}
			
			for(Town t : nation.getTowns())
			{
				for(Resident r : t.getResidents())
				{
					if(!attackingResidents.contains(r))
					{
						if(plugin.getServer().getPlayer(r.getName()) != null)
						{
							WarPlayer wp = WarHandler.getWarPlayer(r.getName());
							if(wp.getAllyAccept())
							{
								wp.getPlayer().sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + "Your Town Is Attacking!");
								attackingResidents.add(r);
							}
						}
					}
				}
			}
			
		}
		
		attackerWinValue = (attackingResidents.size() + defendingResidents.size()) * plugin.getConfig().getInt("War.AttackersPointValue");
		defenderWinValue = (attackingResidents.size() + defendingResidents.size()) * plugin.getConfig().getInt("War.DefendersPointValue");
	}
	
	public int getID()
	{
		return ID;
	}
	
	public void startWar()
	{
		plugin.getServer().getPlayer(defendingTown.getMayor().getName()).sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + "Your Town Is Defending!");
		plugin.getServer().getPlayer(attackingTown.getMayor().getName()).sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + "Your Town Is Attacking!");
		
		//ArtOfWar.economy.withdrawPlayer(defendingTown.getMayor().getName(), betAmount);
		//ArtOfWar.economy.withdrawPlayer(attackingTown.getMayor().getName(), betAmount);
		defendingTown.setPVP(true);
		//snapshot
		
		/*plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				for(Resident r : attackingResidents)
				{
					PlayerRespawnEvent pre = new PlayerRespawnEvent(Bukkit.getPlayer(r.getName()), attackerSpawn,false);
					plugin.getServer().getPluginManager().callEvent(pre);
					//TagAPI.refreshPlayer(Bukkit.getPlayer(r.getName()));
				}
				for(Resident r : defendingResidents)
				{
					PlayerRespawnEvent pre = new PlayerRespawnEvent(Bukkit.getPlayer(r.getName()), attackerSpawn,false);
					plugin.getServer().getPluginManager().callEvent(pre);
					//TagAPI.refreshPlayer(WarHandler.getWarPlayer(r.getName()).getPlayer());
				}
			}
		},20L * 60L);*/
		
		//setDestroy
		defendingTown.getPermissions().residentDestroy = false;
		defendingTown.getPermissions().outsiderDestroy = true;
		defendingTown.getPermissions().allyDestroy = false;
		
		final Location spawnLoc;
		try
		{
			spawnLoc = defendingTown.getSpawn();
		}
		catch(Exception townyexception)
		{
			//TownyMessaging.sendErrorMsg(player, townyexception.getMessage());
			return;
		}
		
		for(Resident r : defendingResidents)
		{
			final Player player = Bukkit.getServer().getPlayer(r.getName());
			
			player.teleport(spawnLoc);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
			{
				@Override
				public void run()
				{
					player.teleport(spawnLoc);
				}
			},20L);
			WarPlayer wp = WarHandler.getWarPlayer(player.getName());
			wp.setWarID(ID);
			wp.setAttacking(false);
			wp.getPlayer().setGameMode(GameMode.SURVIVAL);
		}
		
		int x = 0;
		int y = 0;
		int z = 0;
		World world = null;
		try
		{
			x = defendingTown.getSpawn().getBlockX();
			y = defendingTown.getSpawn().getBlockY();
			z = defendingTown.getSpawn().getBlockZ();
			world = defendingTown.getSpawn().getWorld();
		}
		catch (Exception e)
		{
			//Bukkit.getServer().broadcastMessage("WORLDCOORD ERROR!");
		}
		
		while(true)
		{
			boolean check = true;
			WorldCoord wc = null;
			try
			{
				wc = WorldCoord.parseWorldCoord(new Location(world,x,y,z));
			}
			catch (Exception e)
			{
				//Bukkit.getServer().broadcastMessage("WORLDCOORD ERROR!");
			}
			
			TownBlock t = null;
			try
			{
				t = wc.getTownBlock();
				
			}
			catch(Exception e)
			{
			}
			
			if(t != null && defendingTown.getTownBlocks().contains(t))
			{
				//Bukkit.getServer().broadcastMessage("++COORD!");
				//Bukkit.getServer().broadcastMessage(String.valueOf(wc.getX()));
				//Bukkit.getServer().broadcastMessage(String.valueOf(wc.getZ()));
				++x;
				//wc.setX(coord);
				check = false;
			}
			
			if(check)
			{
				//Bukkit.getServer().broadcastMessage(String.valueOf(wc.getX()));
				//Bukkit.getServer().broadcastMessage(String.valueOf(wc.getZ()));
				x += plugin.getConfig().getInt("War.AttackerSpawnDistance");
				y = 64;
				Location loc = new Location(world,x,y,z);
				while(true)
				{
					Block b = loc.getBlock();
					if(b.getType().equals(Material.AIR))
					{
						Block b2 = new Location(world,x,y-1,z).getBlock();
						if(!b2.getType().equals(Material.AIR))
						{
							attackerSpawn = loc;
							enderChestLoc = new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ()+1);
							enderChestLoc.getBlock().setType(Material.ENDER_CHEST);
							break;
						}
						else
						{
							y -= 1;
							loc = new Location(world,x,y,z);
						}
					}
					else
					{
						y += 1;
						loc = new Location(world,x,y,z);
					}
				}
				break;
			}
			
		}
		
		
		for(Resident r : attackingResidents)
		{
			final WarPlayer wp = WarHandler.getWarPlayer(r.getName());
			if(wp != null)
			{
				wp.setWarID(ID);
				wp.setAttacking(true);
				wp.getPlayer().teleport(attackerSpawn);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						wp.getPlayer().teleport(attackerSpawn);
					}
				},20L);
				wp.getPlayer().setGameMode(GameMode.SURVIVAL);
			}
			
		}
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				for(Resident r : attackingResidents)
				{
					//PlayerRespawnEvent pre = new PlayerRespawnEvent(Bukkit.getPlayer(r.getName()), attackerSpawn,false);
					//plugin.getServer().getPluginManager().callEvent(pre);
					TagAPI.refreshPlayer(Bukkit.getPlayer(r.getName()));
				}
				for(Resident r : defendingResidents)
				{
					//PlayerRespawnEvent pre = new PlayerRespawnEvent(Bukkit.getPlayer(r.getName()), attackerSpawn,false);
					//plugin.getServer().getPluginManager().callEvent(pre);
					TagAPI.refreshPlayer(WarHandler.getWarPlayer(r.getName()).getPlayer());
				}
			}
		},20L*3L);
		
		
	}
	
	public Town getAttackingTown()
	{
		return attackingTown;
	}
	
	public Town getDefendingTown()
	{
		return defendingTown;
	}
	
	public boolean attackersCanPlaceBlock()
	{
		if(attackersBlockPlaceCount == plugin.getConfig().getInt("War.AttackerBlockPlaceAmount"))
		{
			return false;
		}
		return true;
	}
	
	public void attackerPlaceBlock()
	{
		++attackersBlockPlaceCount;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				--attackersBlockPlaceCount;
			}
		},20L * plugin.getConfig().getLong("War.AttackerBlockPlaceDelay"));
	}
	
	public boolean attackersCanBreakBlock()
	{
		if(attackersBlockBreakCount == plugin.getConfig().getInt("War.AttackerBlockBreakAmount"))
		{
			return false;
		}
		return true;
	}
	
	public void attackerBreakBlock()
	{
		++attackersBlockBreakCount;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				--attackersBlockBreakCount;
			}
		},20L * plugin.getConfig().getLong("War.AttackerBlockBreakDelay"));
	}
	
	public int getAttackerWinValue()
	{
		return attackerWinValue;
	}
	
	public int getDefenderWinValue()
	{
		return defenderWinValue;
	}
	
	public int getAttackerPointCount()
	{
		return attackerPointCount;
	}
	
	public int getDefenderPointCount()
	{
		return defenderPointCount;
	}
	
	public void addAttackerPointCount()
	{
		++attackerPointCount;
	}
	
	public void addDefenderPointCount()
	{
		++defenderPointCount;
	}
	
	public void addTNTCounter()
	{
		++TNTCounter;
	}
	
	public int getTNTCounter()
	{
		return TNTCounter;
	}
	
	public int getTNTAmount()
	{
		return TNTAmount;
	}
	
	public void defenderWin()
	{
		//double amount = ArtOfWar.economy.getBalance(defendingTown.getMayor().getName());
		//plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "money set " + defendingTown.getMayor().getName() + " " + String.valueOf(amount+betAmount*2));
		defendingTown.setPVP(false);
		//ArtOfWar.economy.depositPlayer(defendingTown.getMayor().getName(),betAmount*2D);
		for(Resident r : defendingResidents)
		{
			WarPlayer wp = WarHandler.getWarPlayer(r.getName());
			wp.setWarID(0);
			wp.getPlayer().sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Your Town Won The War!");
			for(String s : plugin.getConfig().getStringList("War.RewardItems"))
			{
				String[] item = s.split(",");
				wp.getPlayer().getInventory().addItem(new ItemStack(Integer.valueOf(item[0]),Integer.valueOf(item[1])));
			}
			wp.getPlayer().teleport(plugin.lobby);
		}
		for(Resident r : attackingResidents)
		{
			WarPlayer wp = WarHandler.getWarPlayer(r.getName());
			wp.setWarID(0);
			wp.setAttacking(false);
			wp.getPlayer().sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Your Town Lost The War!");
			wp.getPlayer().teleport(plugin.lobby);
		}
		
		enderChestLoc.getBlock().setType(Material.AIR);
		
		for(Resident r : attackingResidents)
		{
			TagAPI.refreshPlayer(Bukkit.getPlayer(r.getName()));
		}
		for(Resident r : defendingResidents)
		{
			TagAPI.refreshPlayer(WarHandler.getWarPlayer(r.getName()).getPlayer());
		}
		defendingTown.getPermissions().residentDestroy = true;
		defendingTown.getPermissions().outsiderDestroy = false;
		defendingTown.getPermissions().allyDestroy = true;
	}
	
	public void attackerWin()
	{
		defendingTown.setPVP(false);
		//double amount = ArtOfWar.economy.getBalance(attackingTown.getMayor().getName());
		//plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "money set " + attackingTown.getMayor().getName() + " " + String.valueOf(amount+betAmount*2));
		//ArtOfWar.economy.depositPlayer(attackingTown.getMayor().getName(),betAmount*2D);
		for(Resident r : attackingResidents)
		{
			WarPlayer wp = WarHandler.getWarPlayer(r.getName());
			wp.setWarID(0);
			wp.getPlayer().sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Your Town Won The War!");
			for(String s : plugin.getConfig().getStringList("War.RewardItems"))
			{
				String[] item = s.split(",");
				wp.getPlayer().getInventory().addItem(new ItemStack(Integer.valueOf(item[0]),Integer.valueOf(item[1])));
			}
			wp.getPlayer().teleport(plugin.lobby);
		}
		for(Resident r : defendingResidents)
		{
			WarPlayer wp = WarHandler.getWarPlayer(r.getName());
			wp.setWarID(0);
			wp.setAttacking(false);
			wp.getPlayer().sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Your Town Lost The War!");
			wp.getPlayer().teleport(plugin.lobby);
		}
		
		enderChestLoc.getBlock().setType(Material.AIR);
		
		for(Resident r : attackingResidents)
		{
			TagAPI.refreshPlayer(Bukkit.getPlayer(r.getName()));
		}
		for(Resident r : defendingResidents)
		{
			TagAPI.refreshPlayer(Bukkit.getPlayer(r.getName()));
		}
		defendingTown.getPermissions().residentDestroy = true;
		defendingTown.getPermissions().outsiderDestroy = false;
		defendingTown.getPermissions().allyDestroy = true;
	}
	
	public Location getAttackerSpawn()
	{
		return attackerSpawn;
	}
	
	public Location getEnderChestLoc()
	{
		return enderChestLoc;
	}
	
	public Resident getAttackingMayor()
	{
		return attackingMayor;
	}
	
	public Resident getDefendingMayor()
	{
		return defendingMayor;
	}
	
	public List<Resident> getAttackingResidents()
	{
		return attackingResidents;
	}
	
	public List<Resident> getDefendingResidents()
	{
		return defendingResidents;
	}
	
}
