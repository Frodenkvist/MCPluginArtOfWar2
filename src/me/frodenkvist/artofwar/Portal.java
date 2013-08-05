package me.frodenkvist.artofwar;

import org.bukkit.Location;

public class Portal extends CuboidArea
{
	private String name;
	
	public Portal(Location start, Location end, String name)
	{
		super(start,end);
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
}
