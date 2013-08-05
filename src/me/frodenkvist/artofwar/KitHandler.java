package me.frodenkvist.artofwar;

import java.util.ArrayList;
import java.util.List;

import me.frodenkvist.armoreditor.Store;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class KitHandler
{
	private static List<Kit> kits = new ArrayList<Kit>();
	
	public static void load()
	{
		for(String s : ArtOfWar.plugin.getConfig().getConfigurationSection("Kits").getKeys(false))
		{
			ItemStack[] armorContent = new ItemStack[4];
			
			String item = ArtOfWar.plugin.getConfig().getString("Kits." + s + ".Helmet");
			if(item.contains(","))
			{
				String[] items = item.split(",");
				ItemStack is = null;
				if(isInt(items[0]))
					is = new ItemStack(Integer.valueOf(items[0]));
				else
				{
					if(ArtOfWar.armorEditorCheck)
						is = Store.getEpicGear(items[0]).getItem();
				}
				for(int i = 1;i<items.length;++i)
				{
					String[] spitedItems = items[i].split(":");
					is.addUnsafeEnchantment(Enchantment.getByName(spitedItems[0]), Integer.valueOf(spitedItems[1]));
				}
				armorContent[0] = is;
			}
			else
			{
				ItemStack is = null;
				if(isInt(item))
					is = new ItemStack(Integer.valueOf(item));
				else
				{
					if(ArtOfWar.armorEditorCheck)
						is = Store.getEpicGear(item).getItem();
				}
				armorContent[0] = is;
			}
			
			item = ArtOfWar.plugin.getConfig().getString("Kits." + s + ".Chestplate");
			if(item.contains(","))
			{
				String[] items = item.split(",");
				ItemStack is = null;
				if(isInt(items[0]))
					is = new ItemStack(Integer.valueOf(items[0]));
				else
				{
					if(ArtOfWar.armorEditorCheck)
						is = Store.getEpicGear(items[0]).getItem();
				}
				for(int i = 1;i<items.length;++i)
				{
					String[] spitedItems = items[i].split(":");
					is.addUnsafeEnchantment(Enchantment.getByName(spitedItems[0]), Integer.valueOf(spitedItems[1]));
				}
				armorContent[1] = is;
			}
			else
			{
				ItemStack is = null;
				if(isInt(item))
					is = new ItemStack(Integer.valueOf(item));
				else
				{
					if(ArtOfWar.armorEditorCheck)
						is = Store.getEpicGear(item).getItem();
				}
				armorContent[1] = is;
			}
			
			item = ArtOfWar.plugin.getConfig().getString("Kits." + s + ".Leggings");
			if(item.contains(","))
			{
				String[] items = item.split(",");
				ItemStack is = null;
				if(isInt(items[0]))
					is = new ItemStack(Integer.valueOf(items[0]));
				else
				{
					if(ArtOfWar.armorEditorCheck)
						is = Store.getEpicGear(items[0]).getItem();
				}
				for(int i = 1;i<items.length;++i)
				{
					String[] spitedItems = items[i].split(":");
					is.addUnsafeEnchantment(Enchantment.getByName(spitedItems[0]), Integer.valueOf(spitedItems[1]));
				}
				armorContent[2] = is;
			}
			else
			{
				ItemStack is = null;
				if(isInt(item))
					is = new ItemStack(Integer.valueOf(item));
				else
				{
					if(ArtOfWar.armorEditorCheck)
						is = Store.getEpicGear(item).getItem();
				}
				armorContent[2] = is;
			}
			
			item = ArtOfWar.plugin.getConfig().getString("Kits." + s + ".Boots");
			if(item.contains(","))
			{
				String[] items = item.split(",");
				ItemStack is = null;
				if(isInt(items[0]))
					is = new ItemStack(Integer.valueOf(items[0]));
				else
				{
					if(ArtOfWar.armorEditorCheck)
						is = Store.getEpicGear(items[0]).getItem();
				}
				for(int i = 1;i<items.length;++i)
				{
					String[] spitedItems = items[i].split(":");
					is.addUnsafeEnchantment(Enchantment.getByName(spitedItems[0]), Integer.valueOf(spitedItems[1]));
				}
				armorContent[3] = is;
			}
			else
			{
				ItemStack is = null;
				if(isInt(item))
					is = new ItemStack(Integer.valueOf(item));
				else
				{
					if(ArtOfWar.armorEditorCheck)
						is = Store.getEpicGear(item).getItem();
				}
				armorContent[3] = is;
			}
			
			List<ItemStack> things = new ArrayList<ItemStack>();
			List<String> items = ArtOfWar.plugin.getConfig().getStringList("Kits." + s + ".Items");
			for(String s2 : items)
			{
				String[] s3 = s2.split(",");
				if(s3.length == 2)
				{
					if(s3[0].contains(":"))
					{
						String[] s4 = s3[0].split(":");
						things.add(new ItemStack(Integer.valueOf(s4[0]),Integer.valueOf(s3[1]),Short.valueOf(s4[1])));
						//inv.addItem(new ItemStack(Integer.valueOf(s4[0]),Integer.valueOf(s3[1]),Short.valueOf(s4[1])));
					}
					else
					{
						ItemStack is = null;
						if(isInt(s3[0]))
							is = new ItemStack(Integer.valueOf(s3[0]));
						else
						{
							if(ArtOfWar.armorEditorCheck)
								is = Store.getEpicGear(s3[0]).getItem();
						}
						is.setAmount(Integer.valueOf(s3[1]));
						things.add(is);
						//inv.addItem(new ItemStack(Integer.valueOf(s3[0]),Integer.valueOf(s3[1])));
					}
				}
				else
				{
					if(s3[0].contains(":"))
					{
						String[] s4 = s3[0].split(":");
						ItemStack is = new ItemStack(Integer.valueOf(s4[0]),Integer.valueOf(s3[1]),Short.valueOf(s4[1]));
						for(int i = 2;i<s3.length;++i)
						{
							s4 = s3[i].split(":");
							is.addUnsafeEnchantment(Enchantment.getByName(s4[0]), Integer.valueOf(s4[1]));
						}
						things.add(is);
						//inv.addItem(is);
					}
					else
					{
						ItemStack is = null;
						if(isInt(s3[0]))
							is = new ItemStack(Integer.valueOf(s3[0]));
						else
						{
							if(ArtOfWar.armorEditorCheck)
								is = Store.getEpicGear(s3[0]).getItem();
						}
						is.setAmount(Integer.valueOf(s3[1]));
						things.add(is);
						for(int i = 2;i<s3.length;++i)
						{
							String[] s4 = s3[i].split(":");
							is.addUnsafeEnchantment(Enchantment.getByName(s4[0]), Integer.valueOf(s4[1]));
						}
						things.add(is);
						//inv.addItem(is);
					}
				}
				
			}
			ItemStack[] inventory = new ItemStack[things.size()]; 
			for(int i = 0;i<things.size();++i)
			{
				inventory[i] = things.get(i);
			}
			
			kits.add(new Kit(s, armorContent, inventory));
		}
	}
	
	public static Kit getKit(String name)
	{
		for(Kit kit : kits)
		{
			if(kit.getName().equalsIgnoreCase(name))
				return kit;
		}
		return null;
	}
	
	private static boolean isInt(String s)
	{
		try
		{
			Integer.valueOf(s);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
