package me.frodenkvist.artofwar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.kitteh.tag.TagAPI;

import com.palmergames.bukkit.towny.object.Town;
//import com.palmergames.bukkit.towny.object.TownBlockOwner;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class Arena
{
	private String name;
	private boolean active;
	
	private Location sub1;
	private Location sub2;
	
	private  ArtOfWar plugin;
	private  String town;
	private  Location pos1;
	private  Location pos2;
	private  List<Player> inQueue = new ArrayList<Player>();
	private  List<Player> attackingTeam = new ArrayList<Player>();
	private  List<Player> defendingTeam = new ArrayList<Player>();
	private  Location defenderSpawn;
	private  Location attackerSpawn;
	private  Location lobby;
	private  boolean started = false;
	private  int attackersPointValue;
	private  int defendersPointValue;
	private  int attackersPointCounter = 0;
	private  int defendersPointCounter = 0;
	private  int attackersBlockPlaceCount = 0;
	private  int attackersBlockBreakCount = 0;
	private  int defendersBlockPlaceCount = 0;
	private  int defendersBlockBreakCount = 0;
	private  int TNTCounter = 0;
	private  RestorationRegion rr;
	private  boolean aboutToEnd = false;
	private  List<Player> spectaters = new ArrayList<Player>();
	private  List<String> quitPlayers = new ArrayList<String>();
	private  int timerID;
	private  List<Player> potionHitList = new ArrayList<Player>();
	//private  long TimeLimit;
	
	public Arena(String name)
	{
		this.name = name;
		active = false;
		plugin = ArtOfWar.plugin;
	}
	
	
	public  void setTown(Town town)
	{
		this.town = town.getName();
		plugin.getConfig().set("Arena." + name + ".Town", town.getName());
		plugin.saveConfig();
	}
	
	public  void setPos1(Location loc)
	{
		pos1 = loc;
	}
	
	public  void setPos2(Location loc)
	{
		pos2 = loc;
	}
	
	public  void save()
	{
		
		int highx, highy, highz, lowx, lowy, lowz;
        if (pos1.getBlockX() > pos2.getBlockX())
        {
            highx = pos1.getBlockX();
            lowx = pos2.getBlockX();
        }
        else
        {
            highx = pos2.getBlockX();
            lowx = pos1.getBlockX();
        }
        if (pos1.getBlockY() > pos2.getBlockY())
        {
            highy = pos1.getBlockY();
            lowy = pos2.getBlockY();
        }
        else
        {
            highy = pos2.getBlockY();
            lowy = pos1.getBlockY();
        }
        if (pos1.getBlockZ() > pos2.getBlockZ())
        {
            highz = pos1.getBlockZ();
            lowz = pos2.getBlockZ();
        }
        else
        {
            highz = pos2.getBlockZ();
            lowz = pos1.getBlockZ();
        }
        pos1 = new Location(pos1.getWorld(),highx,highy,highz);
        pos2 = new Location(pos1.getWorld(),lowx,lowy,lowz);
        
        int x1 = pos2.getBlockX();
		int y1 = pos2.getBlockY();
		int z1 = pos2.getBlockZ();
		int x2 = pos1.getBlockX();
		int y2 = pos1.getBlockY();
		int z2 = pos1.getBlockZ();
        
		plugin.getConfig().set("Arena." + name + ".Save.HighPoint.x", x2);
		plugin.getConfig().set("Arena." + name + ".Save.HighPoint.y", y2);
		plugin.getConfig().set("Arena." + name + ".Save.HighPoint.z", z2);
		plugin.getConfig().set("Arena." + name + ".Save.HighPoint.world", pos1.getWorld().getName());
		
		plugin.getConfig().set("Arena." + name + ".Save.LowPoint.x", x1);
		plugin.getConfig().set("Arena." + name + ".Save.LowPoint.y", y1);
		plugin.getConfig().set("Arena." + name + ".Save.LowPoint.z", z1);
		plugin.getConfig().set("Arena." + name + ".Save.LowPoint.world", pos2.getWorld().getName());
		
		plugin.saveConfig();
		
		rr = new RestorationRegion(name,pos1,pos2);
		rr.save();
		rr.saveState();
        
	}
	
	public  void loadSave()
	{
		/*int i = 0;
		int x1 = pos2.getBlockX();
		int y1 = pos2.getBlockY();
		int z1 = pos2.getBlockZ();
		int x2 = pos1.getBlockX();
		int y2 = pos1.getBlockY();
		int z2 = pos1.getBlockZ();
		for(int x = x1;x <= x2;++x)
		{
			for(int y = y1;y <= y2;++y)
			{
				for(int z = z1;z <= z2;++z)
				{
					Block block = new Location(pos1.getWorld(),x,y,z).getBlock();//.setType(save.get(i).getType());
					block.setType(save.get(i).getType());
					block.setData(save.get(i).getData());
					++i;
				}
			}
		}*/
		rr.restoreRegion();
	}
	
	public  void addToQueue(Player p)
	{
		inQueue.add(p);
		p.teleport(lobby);
		WarPlayer wp = WarHandler.getWarPlayer(p.getName());
		wp.setArena(this);
		if(WarHandler.getSign() != null)
		{
			WarHandler.getSign().setLine(2, ChatColor.AQUA + "" + inQueue.size() + "/" + plugin.getConfig().getInt("Arena." + name + ".QueueSize"));
			WarHandler.getSign().update();
		}
		if(inQueue.size()%5 == 0)
		{
			Bukkit.broadcastMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + "War Arena Is Waiting " + ChatColor.GREEN + inQueue.size() + "/" + plugin.getConfig().getInt("Arena." + name + ".QueueSize") + " Players " + "/war join" + ChatColor.AQUA + " To Play!");
		}
		if(inQueue.size() >= plugin.getConfig().getInt("Arena." + name + ".QueueSize"))
		{
			if(WarHandler.getSign() != null)
			{
				WarHandler.getSign().setLine(0, ChatColor.GREEN + "Arena Is");
				WarHandler.getSign().setLine(1, ChatColor.GREEN + "In Progress");
				WarHandler.getSign().setLine(2, ChatColor.AQUA + name);
				WarHandler.getSign().setLine(3, ChatColor.BLUE + "B:0" + ChatColor.WHITE + " " + ChatColor.RED + "R:0");
				WarHandler.getSign().update();
			}
			start();
		}
	}
	
	public  void setPlugin(ArtOfWar plugin)
	{
		this.plugin = plugin;
	}
	
	public  void start()
	{
		for(int i=0;i<inQueue.size();++i)
		{
			inQueue.get(i).sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + "The War Will Start In " + plugin.getConfig().getLong("Arena." + name + ".PrepTime") + " Minutes!");
		}
		started = true;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			
			
			@Override
			public void run()
			{
				int i = 0;
				for(Player iq : inQueue)
				{
					//WarPlayer wp = WarHandler.getWarPlayer(iq.getName());
					//setPlayerArena(wp);
					if(plugin.getConfig().getInt("Arena.AttackingTeamSize") > attackingTeam.size() && plugin.getConfig().getInt("Arena." + name + ".DefendingTeamSize") > defendingTeam.size())
					{
						if(i%2 == 0)
						{
							attackingTeam.add(iq);
							//iq.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + "The War Will Start In " + plugin.getConfig().getLong("Arena.PrepTime") + " Minutes!");
						}
						else
						{
							defendingTeam.add(iq);
							//iq.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + "The War Will Start In " + plugin.getConfig().getLong("Arena.PrepTime") + " Minutes!");
						}
						++i;
					}
					else if(plugin.getConfig().getInt("Arena." + name + ".AttackingTeamSize") > attackingTeam.size())
					{
						attackingTeam.add(iq);
					}
					else
					{
						defendingTeam.add(iq);
					}
					
				}
				for(Player p : attackingTeam)
				{
					if(plugin.getConfig().getBoolean("Arena." + name + ".ClearInventoryOnStart"))
					{
						p.getInventory().clear();
						p.getInventory().setHelmet(new ItemStack(Material.AIR));
						p.getInventory().setChestplate(new ItemStack(Material.AIR));
						p.getInventory().setLeggings(new ItemStack(Material.AIR));
						p.getInventory().setBoots(new ItemStack(Material.AIR));
					}
						
					p.teleport(attackerSpawn);
					p.setGameMode(GameMode.SURVIVAL);
					p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + "Your Team Is Attacking!");
				}
				for(Player p : defendingTeam)
				{
					if(plugin.getConfig().getBoolean("Arena." + name + ".ClearInventoryOnStart"))
					{
						p.getInventory().clear();
						p.getInventory().setHelmet(new ItemStack(Material.AIR));
						p.getInventory().setChestplate(new ItemStack(Material.AIR));
						p.getInventory().setLeggings(new ItemStack(Material.AIR));
						p.getInventory().setBoots(new ItemStack(Material.AIR));
					}
					p.teleport(defenderSpawn);
					p.setGameMode(GameMode.SURVIVAL);
					p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + "Your Team Is Defending!");
				}
				
				started = true;
				attackersPointValue = inQueue.size() * plugin.getConfig().getInt("Arena." + name + ".AttackersPointValue");
				defendersPointValue = inQueue.size() * plugin.getConfig().getInt("Arena." + name + ".DefendersPointValue");
				
				if(ArtOfWar.townyCheck)
				{
					Town town = getArenaTown();
					town.setPVP(true);
					town.getPermissions().residentDestroy = false;
					town.getPermissions().outsiderDestroy = true;
					town.getPermissions().allyDestroy = false;
					
					//TownBlockOwner tbo = town;
					
					town.getPermissions().residentBuild = false;
					town.getPermissions().outsiderBuild = true;//set("outsiderBuild", true);
					town.getPermissions().allyBuild = false;
					TownyUniverse.getDataSource().saveTown(getArenaTown());
			        TownyUniverse.getDataSource().saveTownList();
		        }
		        
				if(ArtOfWar.tagAPICheck)
				{
			        for(Player p : inQueue)
					{
						TagAPI.refreshPlayer(p);
					}
				}
		        startTimer();
		        //timerID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new TimerRunnable(plugin.getConfig().getLong("Arena.TimeLimit"),this),);
		        //inQueue.clear();
		        
		        
			}
		},20L*60L*plugin.getConfig().getLong("Arena." + name + ".PrepTime"));
		
	}
	
	public  void setAttackerSpawn(Location loc)
	{
		attackerSpawn = loc;
		
		plugin.getConfig().set("Arena." + name + ".AttackerSpawn.x", loc.getBlockX());
		plugin.getConfig().set("Arena." + name + ".AttackerSpawn.y", loc.getBlockY());
		plugin.getConfig().set("Arena." + name + ".AttackerSpawn.z", loc.getBlockZ());
		plugin.getConfig().set("Arena." + name + ".AttackerSpawn.world", loc.getWorld().getName());
		
		plugin.saveConfig();
	}
	
	public  void setDefenderSpawn(Location loc)
	{
		defenderSpawn = loc;
		
		plugin.getConfig().set("Arena." + name + ".DefenderSpawn.x", loc.getBlockX());
		plugin.getConfig().set("Arena." + name + ".DefenderSpawn.y", loc.getBlockY());
		plugin.getConfig().set("Arena." + name + ".DefenderSpawn.z", loc.getBlockZ());
		plugin.getConfig().set("Arena." + name + ".DefenderSpawn.world", loc.getWorld().getName());
		
		plugin.saveConfig();
		
		
	}
	
	public  void setLobby(Location loc)
	{
		lobby = loc;
		
		plugin.getConfig().set("Arena." + name + ".Lobby.x", loc.getBlockX());
		plugin.getConfig().set("Arena." + name + ".Lobby.y", loc.getBlockY());
		plugin.getConfig().set("Arena." + name + ".Lobby.z", loc.getBlockZ());
		plugin.getConfig().set("Arena." + name + ".Lobby.world", loc.getWorld().getName());
		
		plugin.saveConfig();
	}
	
	public  boolean hasStarted()
	{
		return started;
	}
	
	public  int getAttackersPointValue()
	{
		return attackersPointValue;
	}
	
	public  int getDefendersPointValue()
	{
		return defendersPointValue;
	}
	
	public  void addAttackersPointCounter()
	{
		++attackersPointCounter;
	}
	
	public  void addDefendersPointCounter()
	{
		++defendersPointCounter;
	}
	
	public  int getAttackersPointCounter()
	{
		return attackersPointCounter;
	}
	
	public  int getDefendersPointCounter()
	{
		return defendersPointCounter;
	}
	
	public  boolean isInArena(Player p)
	{
		if(inQueue.contains(p))
			return true;
		return false;
	}
	
	public  List<Player> getAttackingTeam()
	{
		return attackingTeam;
	}
	
	public  List<Player> getDefendingTeam()
	{
		return defendingTeam;
	}
	
	public  boolean attackersCanPlaceBlock()
	{
		if(attackersBlockPlaceCount == plugin.getConfig().getInt("Arena." + name + ".AttackerBlockPlaceAmount"))
		{
			return false;
		}
		return true;
	}
	
	public  void attackerPlaceBlock()
	{
		++attackersBlockPlaceCount;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				--attackersBlockPlaceCount;
			}
		},20L * plugin.getConfig().getLong("Arena." + name + ".AttackerBlockPlaceDelay"));
	}
	
	public  boolean defendersCanPlaceBlock()
	{
		if(defendersBlockPlaceCount == plugin.getConfig().getInt("Arena." + name + ".DefenderBlockPlaceAmount"))
		{
			return false;
		}
		return true;
	}
	
	public  void defenderPlaceBlock()
	{
		++defendersBlockPlaceCount;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				--defendersBlockPlaceCount;
			}
		},20L * plugin.getConfig().getLong("Arena." + name + ".DefenderBlockPlaceDelay"));
	}
	
	
	public  boolean attackersCanBreakBlock()
	{
		if(attackersBlockBreakCount == plugin.getConfig().getInt("Arena." + name + ".AttackerBlockBreakAmount"))
		{
			return false;
		}
		return true;
	}
	
	public  void attackerBreakBlock()
	{
		++attackersBlockBreakCount;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				--attackersBlockBreakCount;
			}
		},20L * plugin.getConfig().getLong("Arena." + name + ".AttackerBlockBreakDelay"));
	}
	
	public  boolean defendersCanBreakBlock()
	{
		if(defendersBlockBreakCount == plugin.getConfig().getInt("Arena." + name + ".DefenderBlockBreakAmount"))
		{
			return false;
		}
		return true;
	}
	
	public  void defenderBreakBlock()
	{
		++defendersBlockBreakCount;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				--defendersBlockBreakCount;
			}
		},20L * plugin.getConfig().getLong("Arena." + name + ".DefenderBlockBreakDelay"));
	}
	
	
	public  void attackerWin()
	{
		aboutToEnd = true;
		if(ArtOfWar.tagAPICheck)
		{
	        for(Player p : inQueue)
			{
				TagAPI.refreshPlayer(p);
			}
		}
		
		for(Player p : attackingTeam)
		{
			if(plugin.getConfig().getBoolean("Arena." + name + ".ClearInventoryOnEnd"))
			{
				p.getInventory().clear();
				p.getInventory().setHelmet(new ItemStack(Material.AIR));
				p.getInventory().setChestplate(new ItemStack(Material.AIR));
				p.getInventory().setLeggings(new ItemStack(Material.AIR));
				p.getInventory().setBoots(new ItemStack(Material.AIR));
			}
				
			p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Your Team Won The Arena War!");
			for(String s : plugin.getConfig().getStringList("Arena." + name + ".RewardItems"))
			{
				String[] item = s.split(",");
				p.getInventory().addItem(new ItemStack(Integer.valueOf(item[0]),Integer.valueOf(item[1])));
			}
			p.teleport(lobby);
			playVictoryTune(p);
		}
		for(Player p : defendingTeam)
		{
			if(plugin.getConfig().getBoolean("Arena." + name + ".ClearInventoryOnEnd"))
			{
				p.getInventory().clear();
				p.getInventory().setHelmet(new ItemStack(Material.AIR));
				p.getInventory().setChestplate(new ItemStack(Material.AIR));
				p.getInventory().setLeggings(new ItemStack(Material.AIR));
				p.getInventory().setBoots(new ItemStack(Material.AIR));
			}
			p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Your Team Lost The Arena War!");
			p.teleport(lobby);
			playLosingTune(p);
		}
		
		started = false;
		inQueue.clear();
		attackingTeam.clear();
		defendingTeam.clear();
		spectaters.clear();
		attackersPointCounter = 0;
		defendersPointCounter = 0;
		TNTCounter = 0;
		if(ArtOfWar.townyCheck)
		{
			Town town = getArenaTown();
			town.setPVP(false);
			town.getPermissions().residentDestroy = true;
			town.getPermissions().outsiderDestroy = false;
			town.getPermissions().allyDestroy = true;
			
			town.getPermissions().residentBuild = true;
			town.getPermissions().outsiderBuild = false;
			town.getPermissions().allyBuild = true;
			TownyUniverse.getDataSource().saveTown(getArenaTown());
	        TownyUniverse.getDataSource().saveTownList();
		}
		//TownyUniverse.getDataSource().saveTown(town);
        //TownyUniverse.getDataSource().saveTownList();
		aboutToEnd = false;
		loadSave();
		potionHitList.clear();
		plugin.getServer().getScheduler().cancelTask(timerID);
		if(plugin.getConfig().getBoolean("RandomArena"))
		{
			randomArenas();
		}
		Bukkit.broadcastMessage(ChatColor.AQUA + "War Has Ended And The" + ChatColor.RED + " Red Team " + ChatColor.AQUA + "Has Won!");
		if(WarHandler.getSign() != null)
		{
			Sign s = WarHandler.getSign();
			s.setLine(0, "");
			s.setLine(1, ChatColor.GREEN + "Arena Waiting");
			s.setLine(2, ChatColor.AQUA + "0/" + ArtOfWar.plugin.getConfig().getInt("Arena." + WarHandler.getActiveArenas().get(0).getName() + ".QueueSize"));
			s.update();
		}
	}
	
	public  void defenderWin()
	{
		aboutToEnd = true;
		if(ArtOfWar.tagAPICheck)
		{
	        for(Player p : inQueue)
			{
				TagAPI.refreshPlayer(p);
			}
		}
		for(Player p : defendingTeam)
		{
			if(plugin.getConfig().getBoolean("Arena." + name + ".ClearInventoryOnEnd"))
			{
				p.getInventory().clear();
				p.getInventory().setHelmet(new ItemStack(Material.AIR));
				p.getInventory().setChestplate(new ItemStack(Material.AIR));
				p.getInventory().setLeggings(new ItemStack(Material.AIR));
				p.getInventory().setBoots(new ItemStack(Material.AIR));
			}
			p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Your Team Won The Arena War!");
			for(String s : plugin.getConfig().getStringList("Arena." + name + ".RewardItems"))
			{
				String[] item = s.split(",");
				p.getInventory().addItem(new ItemStack(Integer.valueOf(item[0]),Integer.valueOf(item[1])));
			}
			p.teleport(lobby);
			playVictoryTune(p);
		}
		for(Player p : attackingTeam)
		{
			if(plugin.getConfig().getBoolean("Arena." + name + ".ClearInventoryOnEnd"))
			{
				p.getInventory().clear();
				p.getInventory().setHelmet(new ItemStack(Material.AIR));
				p.getInventory().setChestplate(new ItemStack(Material.AIR));
				p.getInventory().setLeggings(new ItemStack(Material.AIR));
				p.getInventory().setBoots(new ItemStack(Material.AIR));
			}
			p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Your Team Lost The Arena War!");
			p.teleport(lobby);
			playLosingTune(p);
		}
		
		started = false;
		inQueue.clear();
		attackingTeam.clear();
		defendingTeam.clear();
		spectaters.clear();
		attackersPointCounter = 0;
		defendersPointCounter = 0;
		TNTCounter = 0;
		if(ArtOfWar.townyCheck)
		{
			Town town = getArenaTown();
			town.setPVP(false);
			town.getPermissions().residentDestroy = true;
			town.getPermissions().outsiderDestroy = false;
			town.getPermissions().allyDestroy = true;
			
			town.getPermissions().residentBuild = true;
			town.getPermissions().outsiderBuild = false;
			town.getPermissions().allyBuild = true;
			TownyUniverse.getDataSource().saveTown(getArenaTown());
	        TownyUniverse.getDataSource().saveTownList();
		}
		//TownyUniverse.getDataSource().saveTown(town);
       // TownyUniverse.getDataSource().saveTownList();
		
		
		
		aboutToEnd = false;
		loadSave();
		potionHitList.clear();
		plugin.getServer().getScheduler().cancelTask(timerID);
		if(plugin.getConfig().getBoolean("RandomArena"))
		{
			randomArenas();
		}
		Bukkit.broadcastMessage(ChatColor.AQUA + "War Has Ended And The" + ChatColor.BLUE + " Blue Team " + ChatColor.AQUA + "Has Won!");
		if(WarHandler.getSign() != null)
		{
			Sign s = WarHandler.getSign();
			s.setLine(0, "");
			s.setLine(1, ChatColor.GREEN + "Arena Waiting");
			s.setLine(2, ChatColor.AQUA + "0/" + ArtOfWar.plugin.getConfig().getInt("Arena." + WarHandler.getActiveArenas().get(0).getName() + ".QueueSize"));
			s.update();
		}
	}
	
	public  void forceEnd()
	{
		aboutToEnd = true;
		if(ArtOfWar.tagAPICheck)
		{
	        for(Player p : inQueue)
			{
				TagAPI.refreshPlayer(p);
			}
		}
		for(Player p : defendingTeam)
		{
			if(plugin.getConfig().getBoolean("Arena." + name + ".ClearInventoryOnEnd"))
			{
				p.getInventory().clear();
				p.getInventory().setHelmet(new ItemStack(Material.AIR));
				p.getInventory().setChestplate(new ItemStack(Material.AIR));
				p.getInventory().setLeggings(new ItemStack(Material.AIR));
				p.getInventory().setBoots(new ItemStack(Material.AIR));
			}
			p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "The War Force Ended!");
			p.teleport(lobby);
			
		}
		for(Player p : attackingTeam)
		{
			if(plugin.getConfig().getBoolean("Arena." + name + ".ClearInventoryOnEnd"))
			{
				p.getInventory().clear();
				p.getInventory().setHelmet(new ItemStack(Material.AIR));
				p.getInventory().setChestplate(new ItemStack(Material.AIR));
				p.getInventory().setLeggings(new ItemStack(Material.AIR));
				p.getInventory().setBoots(new ItemStack(Material.AIR));
			}
			p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "The War Force Ended!");
			p.teleport(lobby);
		}
		
		started = false;
		inQueue.clear();
		attackingTeam.clear();
		defendingTeam.clear();
		spectaters.clear();
		attackersPointCounter = 0;
		defendersPointCounter = 0;
		TNTCounter = 0;
		if(ArtOfWar.townyCheck)
		{
			Town town = getArenaTown();
			town.setPVP(false);
			town.getPermissions().residentDestroy = true;
			town.getPermissions().outsiderDestroy = false;
			town.getPermissions().allyDestroy = true;
			
			town.getPermissions().residentBuild = true;
			town.getPermissions().outsiderBuild = false;
			town.getPermissions().allyBuild = true;
			
			TownyUniverse.getDataSource().saveTown(getArenaTown());
	        TownyUniverse.getDataSource().saveTownList();
		}
		//TownyUniverse.getDataSource().saveTown(town);
       // TownyUniverse.getDataSource().saveTownList();
		
		aboutToEnd = false;
		
		
		loadSave();
		
		potionHitList.clear();
		plugin.getServer().getScheduler().cancelTask(timerID);
		if(plugin.getConfig().getBoolean("RandomArena"))
		{
			randomArenas();
		}
		if(WarHandler.getSign() != null)
		{
			Sign s = WarHandler.getSign();
			s.setLine(0, "");
			s.setLine(1, ChatColor.GREEN + "Arena Waiting");
			s.setLine(2, ChatColor.AQUA + "0/" + ArtOfWar.plugin.getConfig().getInt("Arena." + WarHandler.getActiveArenas().get(0).getName() + ".QueueSize"));
			s.update();
		}
	}
	
	public  Location getAttackerSpawn()
	{
		return attackerSpawn;
	}
	
	public  Location getDefenderSpawn()
	{
		return defenderSpawn;
	}
	
	public  void load()
	{
		if(plugin.getConfig().contains("Arena." + name + ".AttackerSpawn.world"))
			attackerSpawn = new Location(plugin.getServer().getWorld(plugin.getConfig().getString("Arena." + name + ".AttackerSpawn.world")),plugin.getConfig().getInt("Arena." + name + ".AttackerSpawn.x"),plugin.getConfig().getInt("Arena." + name + ".AttackerSpawn.y"),plugin.getConfig().getInt("Arena." + name + ".AttackerSpawn.z"));
		if(plugin.getConfig().contains("Arena." + name + ".DefenderSpawn.world"))
			defenderSpawn = new Location(plugin.getServer().getWorld(plugin.getConfig().getString("Arena." + name + ".DefenderSpawn.world")),plugin.getConfig().getInt("Arena." + name + ".DefenderSpawn.x"),plugin.getConfig().getInt("Arena." + name + ".DefenderSpawn.y"),plugin.getConfig().getInt("Arena." + name + ".DefenderSpawn.z"));
		if(plugin.getConfig().contains("Arena." + name + ".Save.HighPoint.world"))
			pos1 = new Location(plugin.getServer().getWorld(plugin.getConfig().getString("Arena." + name + ".Save.HighPoint.world")),plugin.getConfig().getInt("Arena." + name + ".Save.HighPoint.x"),plugin.getConfig().getInt("Arena." + name + ".Save.HighPoint.y"),plugin.getConfig().getInt("Arena." + name + ".Save.HighPoint.z"));
		if(plugin.getConfig().contains("Arena." + name + ".Save.LowPoint.world"))
			pos2 = new Location(plugin.getServer().getWorld(plugin.getConfig().getString("Arena." + name + ".Save.LowPoint.world")),plugin.getConfig().getInt("Arena." + name + ".Save.LowPoint.x"),plugin.getConfig().getInt("Arena." + name + ".Save.LowPoint.y"),plugin.getConfig().getInt("Arena." + name + ".Save.LowPoint.z"));
		if(plugin.getConfig().contains("Arena." + name + ".Sub1.world"))
			sub1 = new Location(plugin.getServer().getWorld(plugin.getConfig().getString("Arena." + name + ".Sub1.world")),plugin.getConfig().getInt("Arena." + name + ".Sub1.x"),plugin.getConfig().getInt("Arena." + name + ".Sub1.y"),plugin.getConfig().getInt("Arena." + name + ".Sub1.z"));
		if(plugin.getConfig().contains("Arena." + name + ".Sub2.world"))
			sub2 = new Location(plugin.getServer().getWorld(plugin.getConfig().getString("Arena." + name + ".Sub2.world")),plugin.getConfig().getInt("Arena." + name + ".Sub2.x"),plugin.getConfig().getInt("Arena." + name + ".Sub2.y"),plugin.getConfig().getInt("Arena." + name + ".Sub2.z"));
		
		
		if(ArtOfWar.townyCheck)
		{
			try
			{
				town = TownyUniverse.getDataSource().getTown(plugin.getConfig().getString("Arena." + name + ".Town")).getName();
			}
			catch(Exception e)
			{
				
			}
		}
		
		if(plugin.getConfig().contains("Arena." + name + ".Lobby.world"))
			lobby = new Location(plugin.getServer().getWorld(plugin.getConfig().getString("Arena." + name + ".Lobby.world")),plugin.getConfig().getInt("Arena." + name + ".Lobby.x"),plugin.getConfig().getInt("Arena." + name + ".Lobby.y"),plugin.getConfig().getInt("Arena." + name + ".Lobby.z"));
		
		if(pos1 != null && pos2 != null)
			rr = new RestorationRegion( name,pos1,pos2);
		if(plugin.getConfig().contains("Arena." + name + ".Active"))
			active = plugin.getConfig().getBoolean("Arena." + name + ".Active");
		if(active)
			WarHandler.addActiveArena(this);
	}
	
	public  boolean isInQueue(Player p)
	{
		if(inQueue.contains(p))
			return true;
		return false;
	}
	
	public  boolean containsLoc(Location loc)
    {
        if(loc==null)
            return false;
        if(pos1==null)
        	return false;
        if(pos2==null)
        	return false;
        if(!loc.getWorld().equals(pos1.getWorld()))
            return false;
        if(pos2.getBlockX() <= loc.getBlockX() && pos1.getBlockX() >= loc.getBlockX())
        {
            if(pos2.getBlockZ() <= loc.getBlockZ() && pos1.getBlockZ() >= loc.getBlockZ())
            {
                if(pos2.getBlockY() <= loc.getBlockY() && pos1.getBlockY() >= loc.getBlockY())
                {
                    return true;
                }
            }
        }
        return false;
    }
	
	public  int getTNTCounter()
	{
		return TNTCounter;
	}
	
	public  void addTNTCounter()
	{
		++TNTCounter;
	}
	
	public  List<Player> getInQueuePlayers()
	{
		return inQueue;
	}
	
	public  boolean isAboutToEnd()
	{
		return aboutToEnd;
	}
	
	public  void addSpectater(Player p)
	{
		p.teleport(lobby);
		spectaters.add(p);
	}
	
	public  List<Player> getSpectaters()
	{
		return spectaters;
	}
	
	public  void removeSpectater(Player p)
	{
		spectaters.remove(p);
	}
	
	public  void removeInQueue(Player p)
	{
		inQueue.remove(p);
	}
	
	public  void removeAttacker(Player p)
	{
		attackingTeam.remove(p);
	}
	
	public  void removeDefender(Player p)
	{
		defendingTeam.remove(p);
	}
	
	public  void addQuitPlayer(String name)
	{
		quitPlayers.add(name);
	}
	
	public  void removeQuitPlayer(String name)
	{
		quitPlayers.remove(name);
	}
	
	public  List<String> getQuitPlayer()
	{
		return quitPlayers;
	}
	
	public  Location getLobby()
	{
		return lobby;
	}
	
	public  void setTimerID(int id)
	{
		timerID = id;
	}
	
	public  List<Player> getPotionHitList()
	{
		return potionHitList;
	}
	
	public  void addPotionHitList(Player p)
	{
		potionHitList.add(p);
	}
	
	public  void removePotionHitList(Player p)
	{
		potionHitList.remove(p);
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public void activate()
	{
		active = true;
		WarHandler.addActiveArena(this);
		plugin.getConfig().set("Arena." + name + ".Active", true);
		plugin.saveConfig();
	}
	
	public void deactivate()
	{
		active = false;
		WarHandler.removeActiveArena(this);
		plugin.getConfig().set("Arena." + name + ".Active", false);
		plugin.saveConfig();
	}
	
	public void startTimer()
	{
		timerID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new TimerRunnable(plugin.getConfig().getLong("Arena." + name + ".TimeLimit"),this));
	}
	
	public void setPlayerArena(WarPlayer wp)
	{
		wp.setArena(this);
	}
	
	private void randomArenas()
	{
		active = false;
		WarHandler.removeActiveArena(this);
		plugin.getConfig().set("Arena." + name + ".Active", false);
		plugin.saveConfig();
		Random object = new Random();
		while(true)
		{
			//int random = object.nextInt(plugin.getConfig().getStringList("ArenaNames").size());
			int random = object.nextInt(WarHandler.getArenas().size());
			Arena a = WarHandler.getArena(random);
			if(a.isReady())
			{
				a.activate();
				break;
			}
				
		}
		
		
	}
	
	public void setSub1(Location loc)
	{
		sub1 = loc;
		if(sub2 != null)
		{
			int highx, highy, highz, lowx, lowy, lowz;
	        if (sub1.getBlockX() > sub2.getBlockX())
	        {
	            highx = sub1.getBlockX();
	            lowx = sub2.getBlockX();
	        } 
	        else
	        {
	            highx = sub2.getBlockX();
	            lowx = sub1.getBlockX();
	        }
	        if (sub1.getBlockY() > sub2.getBlockY())
	        {
	            highy = sub1.getBlockY();
	            lowy = sub2.getBlockY();
	        }
	        else
	        {
	            highy = sub2.getBlockY();
	            lowy = sub1.getBlockY();
	        }
	        if (sub1.getBlockZ() > sub2.getBlockZ())
	        {
	            highz = sub1.getBlockZ();
	            lowz = sub2.getBlockZ();
	        }
	        else
	        {
	            highz = sub2.getBlockZ();
	            lowz = sub1.getBlockZ();
	        }
	        sub1 = new Location(sub1.getWorld(),highx,highy,highz);
	        sub2 = new Location(sub2.getWorld(),lowx,lowy,lowz);
	        plugin.getConfig().set("Arena." + name + ".Sub2.x", sub2.getBlockX());
			plugin.getConfig().set("Arena." + name + ".Sub2.y", sub2.getBlockY());
			plugin.getConfig().set("Arena." + name + ".Sub2.z", sub2.getBlockZ());
			plugin.getConfig().set("Arena." + name + ".Sub2.world", sub2.getWorld().getName());
	        
	        plugin.getConfig().set("Arena." + name + ".Sub1.x", sub1.getBlockX());
	        plugin.getConfig().set("Arena." + name + ".Sub1.y", sub1.getBlockY());
	        plugin.getConfig().set("Arena." + name + ".Sub1.z", sub1.getBlockZ());
	        plugin.getConfig().set("Arena." + name + ".Sub1.world", sub1.getWorld().getName());
	        plugin.saveConfig();
		}
		
	}
	
	public void setSub2(Location loc)
	{
		sub2 = loc;
		if(sub1 != null)
		{
			int highx, highy, highz, lowx, lowy, lowz;
	        if (sub1.getBlockX() > sub2.getBlockX())
	        {
	            highx = sub1.getBlockX();
	            lowx = sub2.getBlockX();
	        } 
	        else
	        {
	            highx = sub2.getBlockX();
	            lowx = sub1.getBlockX();
	        }
	        if (sub1.getBlockY() > sub2.getBlockY())
	        {
	            highy = sub1.getBlockY();
	            lowy = sub2.getBlockY();
	        }
	        else
	        {
	            highy = sub2.getBlockY();
	            lowy = sub1.getBlockY();
	        }
	        if (sub1.getBlockZ() > sub2.getBlockZ())
	        {
	            highz = sub1.getBlockZ();
	            lowz = sub2.getBlockZ();
	        }
	        else
	        {
	            highz = sub2.getBlockZ();
	            lowz = sub1.getBlockZ();
	        }
	        sub1 = new Location(sub1.getWorld(),highx,highy,highz);
	        sub2 = new Location(sub2.getWorld(),lowx,lowy,lowz);
	        plugin.getConfig().set("Arena." + name + ".Sub2.x", sub2.getBlockX());
			plugin.getConfig().set("Arena." + name + ".Sub2.y", sub2.getBlockY());
			plugin.getConfig().set("Arena." + name + ".Sub2.z", sub2.getBlockZ());
			plugin.getConfig().set("Arena." + name + ".Sub2.world", sub2.getWorld().getName());
			
			plugin.getConfig().set("Arena." + name + ".Sub1.x", sub1.getBlockX());
			plugin.getConfig().set("Arena." + name + ".Sub1.y", sub1.getBlockY());
			plugin.getConfig().set("Arena." + name + ".Sub1.z", sub1.getBlockZ());
			plugin.getConfig().set("Arena." + name + ".Sub1.world", sub1.getWorld().getName());
			plugin.saveConfig();
		}
	}
	
	public boolean isInSubzone(Location loc)
	{
		if(loc==null)
            return false;
        if(sub1==null)
        	return false;
        if(sub2==null)
        	return false;
        if(!loc.getWorld().equals(sub1.getWorld()))
            return false;
        if(sub2.getBlockX() <= loc.getBlockX() && sub1.getBlockX() >= loc.getBlockX())
        {
            if(sub2.getBlockZ() <= loc.getBlockZ() && sub1.getBlockZ() >= loc.getBlockZ())
            {
                if(sub2.getBlockY() <= loc.getBlockY() && sub1.getBlockY() >= loc.getBlockY())
                {
                    return true;
                }
            }
        }
        return false;
	}
	
	private void playVictoryTune(final Player p)
	{
		p.playSound(p.getLocation(), Sound.NOTE_PIANO, 9F, 0.9F);
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				p.playSound(p.getLocation(), Sound.NOTE_PIANO, 9F, 0.9F);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						p.playSound(p.getLocation(), Sound.NOTE_PIANO, 9F, 0.9F);
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
						{
							@Override
							public void run()
							{
								p.playSound(p.getLocation(), Sound.NOTE_PIANO, 9F, 0.9F);
								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
								{
									@Override
									public void run()
									{
										p.playSound(p.getLocation(), Sound.NOTE_PIANO, 9F, 0.7F);
										plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
										{
											@Override
											public void run()
											{
												p.playSound(p.getLocation(), Sound.NOTE_PIANO, 9F, 0.8F);
												plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
												{
													@Override
													public void run()
													{
														p.playSound(p.getLocation(), Sound.NOTE_PIANO, 9F, 0.9F);
														plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
														{
															@Override
															public void run()
															{
																p.playSound(p.getLocation(), Sound.NOTE_PIANO, 9F, 0.85F);
																plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
																{
																	@Override
																	public void run()
																	{
																		p.playSound(p.getLocation(), Sound.NOTE_PIANO, 9F, 0.9F);
																	}
																},(long) (5L/4*3.5));
															}
														},(long) (15L/4*3.5));
													}
												},(long) (15L/4*3.5));
											}
										},(long) (15L/4*3.5));
									}
								},(long) (15L/4*3.5));
							}
						},(long) (5L/4*3.5));
					}
				},(long) (10L/4*3.5));
			}
		},(long) (5L/4*3.5));
	}
	
	private void playLosingTune(final Player p)
	{
		p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 1.45F);  
        p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 1.05F);  
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()  
        {  
            @Override
            public void run()  
            {  
                p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 1.05F);  
                p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 0.9F);  
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()  
                {  
                    @Override
                    public void run()  
                    {  
                        p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 0.9F);  
                        p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 0.7F);  
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()  
                        {  
                            @Override
                            public void run()  
                            {  
                                p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 1.2F);  
                                p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 0.95F);  
                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()  
                                {
                                    @Override
                                    public void run()  
                                    {  
                                        p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 1.35F);  
                                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()  
                                        {  
                                            @Override
                                            public void run()  
                                            {
                                                p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 1.2F);  
                                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()  
                                                {
                                                    @Override
                                                    public void run()  
                                                    {  
                                                        p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 1.1F);  
                                                        p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 0.75F);  
                                                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()  
                                                        {  
                                                            @Override
                                                            public void run()  
                                                            {  
                                                                p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 1.25F);  
                                                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()  
                                                                {  
                                                                    @Override
                                                                    public void run()  
                                                                    {  
                                                                        p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 1.1F);  
                                                                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()  
                                                                        {
                                                                            @Override
                                                                            public void run()  
                                                                            {  
                                                                                p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 1.05F);  
                                                                                p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 0.7F);  
                                                                                p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 0.9F);  
                                                                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()  
                                                                                {
                                                                                    @Override
                                                                                    public void run()  
                                                                                    {  
                                                                                        p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 0.8F);  
                                                                                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()  
                                                                                        {  
                                                                                            @Override
                                                                                            public void run()  
                                                                                            {  
                                                                                                p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1F, 0.9F);  
                                                                                            }  
                                                                                        },(long) (5L/4*3.5));  
                                                                                    }  
                                                                                },(long) (5L/4*3.5));  
                                                                            }  
                                                                        },(long) (15L/4*3.5));  
                                                                    }  
                                                                },(long) (7/4*3.8));  
                                                            }  
                                                        },(long) (7/4*3.8));  
                                                    }  
                                                },(long) (15L/4*3.5));  
                                            }  
                                        },(long) (7/4*3.8));  
                                    }  
                                },(long) (7/4*3.8));  
                            }  
                        },(long) (15L/4*3.5));  
                    }  
                },(long) (15L/4*3.5));  
            }  
        },(long) (15L/4*3.5));
	}
	
	public void kick(Player p)
	{
		inQueue.remove(p);
		if(attackingTeam.contains(p))
			attackingTeam.remove(p);
		else
			defendingTeam.remove(p);
	}
	
	private boolean isReady()
	{
		if(!plugin.getConfig().contains("Arena." + name + ".AttackerSpawn.world"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".DefenderSpawn.world"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".Save.HighPoint.world"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".Save.LowPoint.world"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".Sub1.world"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".Sub2.world"))
			return false;
		
		
		if(ArtOfWar.townyCheck)
		{
			try
			{
				town = TownyUniverse.getDataSource().getTown(plugin.getConfig().getString("Arena." + name + ".Town")).getName();
			}
			catch(Exception e)
			{
				
			}
		}
		
		if(town == null)
			return false;
		
		if(!plugin.getConfig().contains("Arena." + name + ".Lobby.world"))
			return false;
		
		if(pos1 == null || pos2 == null)
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".Active"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".AttackerBlockBreakAmount"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".AttackerBlockBreakDelay"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".AttackerBlockPlaceAmount"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".AttackerBlockPlaceDelay"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".AttackingTeamSize"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".DefendingTeamSize"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".AttackersPointValue"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".DefendersPointValue"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".QueueSize"))
			return false;
		if(!plugin.getConfig().contains("Arena." + name + ".TimeLimit"))
			return false;
		//if(active)
		//	WarHandler.addActiveArena(this);
		return true;
	}
	
	public Town getArenaTown()
	{
		try
		{
			return TownyUniverse.getDataSource().getTown(town);
		}
		catch
		(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
