package me.frodenkvist.artofwar;

import org.bukkit.entity.Player;

public class DuelingPair
{
	private Player player1;
	private Player player2;
	
	public DuelingPair(Player p1, Player p2)
	{
		player1 = p1;
		player2 = p2;
	}
	
	public Player getPlayer1()
	{
		return player1;
	}
	
	public Player getPlayer2()
	{
		return player2;
	}
	
	public void sendMessage(String msg)
	{
		if(player1 != null)
			player1.sendMessage(msg);
		if(player2 != null)
			player2.sendMessage(msg);
	}
}
