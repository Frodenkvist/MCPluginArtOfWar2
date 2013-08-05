package me.frodenkvist.artofwar;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.object.Town;

public class WarPlayer
{
	private Player player;
	private String name;
	private boolean challanged;
	private Town challangeTown;
	private double challangeAmount;
	private int warID;
	private boolean attacking;
	private boolean allyAsked;
	private boolean allyAccept;
	private int quitID;
	private Arena arena;
	private boolean dueling;
	private String challangedBy;
	
	public WarPlayer(Player player)
	{
		this.player = player;
		name = player.getName();
		challanged = false;
		challangeAmount = 0;
		warID = 0;
		attacking = false;
		allyAsked = false;
		allyAccept = false;
		quitID = 0;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public boolean ischallanged()
	{
		return challanged;
	}
	
	public void setchallanged(boolean challanged)
	{
		this.challanged = challanged;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setchallangeTown(Town town)
	{
		this.challangeTown = town;
	}
	
	public Town getchallangeTown()
	{
		return challangeTown;
	}
	
	public void setchallangeAmount(double amount)
	{
		challangeAmount = amount;
	}
	
	public double getchallangeAmount()
	{
		return challangeAmount;
	}
	
	public void setWarID(int id)
	{
		warID = id;
	}
	
	public int getWarID()
	{
		return warID;
	}
	
	public boolean isAttacking()
	{
		return attacking;
	}
	
	public void setAttacking(boolean value)
	{
		attacking = value;
	}
	
	public boolean getAllyAsked()
	{
		return allyAsked;
	}
	
	public void setAllyAsked(boolean value)
	{
		allyAsked = value;
	}
	
	public boolean getAllyAccept()
	{
		return allyAccept;
	}
	
	public void setAllyAccept(boolean value)
	{
		allyAccept = value;
	}
	
	public void setQuitID(int ID)
	{
		quitID = ID;
	}
	
	public int getQuitID()
	{
		return quitID;
	}
	
	public void setArena(Arena a)
	{
		arena = a;
	}
	
	public Arena getArena()
	{
		return arena;
	}
	
	public boolean isDueling()
	{
		return dueling;
	}
	
	public void setDueling(boolean value)
	{
		dueling = value;
	}
	
	public void setChallangedBy(String name)
	{
		challangedBy = name;
	}
	
	public String getChallangedBy()
	{
		return challangedBy;
	}
}
