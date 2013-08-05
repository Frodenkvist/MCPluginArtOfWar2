package me.frodenkvist.artofwar;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Kit
{
	private String name;
	private ItemStack[] armorContent = new ItemStack[4];
	private ItemStack[] inventory = new ItemStack[36];
	
	public Kit(String name, ItemStack[] armorContent, ItemStack[] inventory)
	{
		this.name = name;
		this.armorContent = armorContent;
		this.inventory = inventory;
	}
	
	public void use(Player player)
	{
		PlayerInventory pinv = player.getInventory();
		pinv.setContents(inventory);
		pinv.setArmorContents(armorContent);
	}
	
	public String getName()
	{
		return name;
	}
}
