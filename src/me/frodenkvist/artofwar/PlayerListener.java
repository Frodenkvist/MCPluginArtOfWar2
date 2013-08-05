package me.frodenkvist.artofwar;

import java.util.ArrayList;
import java.util.List;

import me.frodenkvist.armoreditor.Store;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;

public class PlayerListener implements Listener
{
	public ArtOfWar plugin;
	
	public PlayerListener(ArtOfWar instance)
	{
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event)
	{
		if(!plugin.getConfig().getBoolean("MoveChecksOn"))
			return;
		Player player = event.getPlayer();
		WarPlayer wp = WarHandler.getWarPlayer(player.getName());
		int warID = wp.getWarID();
		if(warID != 0)
		{
			War war = WarHandler.getWar(warID);
			if(!wp.isAttacking())
			{
				WorldCoord wc = WorldCoord.parseWorldCoord(event.getTo());
				TownBlock tb = null;
				try
				{
					tb = wc.getTownBlock();
				}
				catch(Exception e)
				{
				}
				if(tb == null)
				{
					event.setTo(event.getFrom());
					return;
				}
				for(TownBlock t : war.getDefendingTown().getTownBlocks())
				{
					if(t.equals(tb))
						return;
				}
				event.setTo(event.getFrom());
				return;
			}
			else
			{
				WorldCoord wc = WorldCoord.parseWorldCoord(event.getTo());
				TownBlock tb = null;
				try
				{
					tb = wc.getTownBlock();
				}
				catch(Exception e)
				{
					//Bukkit.getServer().broadcastMessage("ERROR TOWNBLOCK!");
				}
				if(tb == null)
				{
					//Bukkit.getServer().broadcastMessage("TOWNBLOCK NULL!");


					if(isWithingBorder(war,event.getTo()))
					{
						return;
					}
					else
					{
						event.setTo(event.getFrom());
					}
				}
				else
				{
					//Bukkit.getServer().broadcastMessage("TONWBLOCK != NULL");
					Town town = null;
					try
					{
						town = tb.getTown();
					}
					catch (Exception e)
					{
						//e.printStackTrace();
					}
					if(!town.equals(war.getDefendingTown()))
					{
						event.setTo(event.getFrom());
					}
					else
						return;
				}
				/*for(TownBlock t : war.getDefendingTown().getTownBlocks())
				{
					if(t.equals(tb))
						return;
				}
				event.setTo(event.getFrom());
				return;*/
			}
		}
		else if(wp.getArena() != null && wp.getArena().hasStarted())
		{
			Arena a = wp.getArena();
			if(!a.containsLoc(event.getTo()))
			{
				event.setTo(event.getFrom());
			}
			if(a.getDefendingTeam().contains(player))
			{
				if(a.isInSubzone(event.getTo()))
				{
					player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,200,1));
				}
				else if(a.isInSubzone(event.getFrom()))
				{
					player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "You Have Left The Saftey Of The Castle And Lost Your Damage Resistance Buff!");
				}
			}
		}
		else
		{
			for(Portal p : WarHandler.getPortals())
			{
				if(p.containsLoc(player.getLocation()))
				{
					Arena a = WarHandler.getActiveArenas().get(0);
					if(!a.isInQueue(player))
					{
						if(!a.hasStarted())
						{
							if(plugin.getConfig().getBoolean("Arena." + a.getName() + ".CheckEmptyInventoryOnJoin"))
							{
								if(!isInventoryEmpty(player))
								{
									player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Please Empty Your Inventory Before Joining!");
									return;
								}
									
							}
							for(Player pla : a.getInQueuePlayers())
							{
								pla.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + player.getName() + " Joined The Game!");
								pla.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] [" + ChatColor.GOLD + String.valueOf((a.getInQueuePlayers().size() + 1)) + "/" + ArtOfWar.plugin.getConfig().getInt("Arena." + a.getName() + ".QueueSize") + ChatColor.AQUA + "] " + ChatColor.GREEN + "Will Start Automaticly When Full");
							}
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + player.getName() + " Joined The Game!");
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] [" + ChatColor.GOLD + String.valueOf((a.getInQueuePlayers().size() + 1)) + "/" + ArtOfWar.plugin.getConfig().getInt("Arena." + a.getName() + ".QueueSize") + ChatColor.AQUA + "] " + ChatColor.GREEN + "Will Start Automaticly When Full");
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + player.getName() + " Type: " + ChatColor.AQUA + "/war leave " + ChatColor.GREEN + "To Leave The Queue");
							
							a.addToQueue(player);
						}
						else
						{
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "The Arena Has Already Started!");
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlaceEvent(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		WarPlayer wp = WarHandler.getWarPlayer(player.getName());
		int warID = wp.getWarID();
		Arena a = wp.getArena();
		if(warID != 0)
		{
			War war = WarHandler.getWar(warID);
			Town defendingTown = war.getDefendingTown();
			List<TownBlock> townBlocks = defendingTown.getTownBlocks();
			WorldCoord wc = WorldCoord.parseWorldCoord(event.getBlock().getLocation());
			TownBlock tb = null;
			try
			{
				tb = wc.getTownBlock();
			}
			catch(Exception e)
			{
			}
			if(tb == null)
				return;
			for(TownBlock t : townBlocks)
			{
				if(t.equals(tb))
				{
					if(event.getBlock().getType().equals(Material.TNT) && war.getTNTAmount() != war.getTNTCounter())
					{
						event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.PRIMED_TNT);
						return;
					}
					else if(!war.attackersCanPlaceBlock())
					{
						event.setCancelled(true);
						return;
					}
					else
					{
						war.attackerPlaceBlock();
						event.getBlock().setType(event.getBlockPlaced().getType());
					}
				}
			}
		}
		else if(a != null && a.isInArena(player))
		{
			if(a.getAttackingTeam().contains(player))
			{
				if(event.getBlock().getType().equals(Material.TNT) && a.getTNTCounter() != plugin.getConfig().getInt("Arena." + a.getName() + ".TNTAmount"))
				{
					event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.PRIMED_TNT);
					a.addTNTCounter();
					return;
				}
				if(event.getBlock().getType().equals(Material.TNT) && a.getTNTCounter() >= plugin.getConfig().getInt("Arena." + a.getName() + ".TNTAmount"))
				{
					player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Your Team has Already Placed " + plugin.getConfig().getInt("Arena." + a.getName() + ".TNTAmount") + " TNT During This Battle");
					player.setItemInHand(new ItemStack(Material.AIR));
					event.setCancelled(true);
					return;
				}
				else if(a.attackersCanPlaceBlock())
				{
					
					a.attackerPlaceBlock();
					event.getBlock().setType(event.getBlockPlaced().getType());
				}
				else
				{
					player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Your Team Has Already Placed " + plugin.getConfig().getInt("Arena." + a.getName() + ".AttackerBlockPlaceAmount") + " Blocks In The Past " + plugin.getConfig().getInt("Arena." + a.getName() + ".AttackerBlockPlaceDelay") + " Seconds!");
					event.setCancelled(true);
					return;
				}
			}
			else if(a.getDefendingTeam().contains(player))
			{
				if(a.defendersCanPlaceBlock())
				{
					
					a.defenderPlaceBlock();
					event.getBlock().setType(event.getBlockPlaced().getType());
				}
				else
				{
					player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Your Team Has Already Placed " + plugin.getConfig().getInt("Arena." + a.getName() + ".DefenderBlockPlaceAmount") + " Blocks In The Past " + plugin.getConfig().getInt("Arena." + a.getName() + ".DefenderBlockPlaceDelay") + " Seconds!");
					event.setCancelled(true);
					return;
				}
			}
		}
		else if(a != null && a.containsLoc(event.getBlock().getLocation()))
		{
			if(player.hasPermission("artofwar.place.arena"))
				return;
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event)
	{
		for(War w : WarHandler.getWars())
		{
			if(event.getBlock().getLocation().equals(w.getEnderChestLoc()))
			{
				event.setCancelled(true);
				return;
			}
		}
		Player player = event.getPlayer();
		WarPlayer wp = WarHandler.getWarPlayer(player.getName());
		int warID = wp.getWarID();
		Arena a = wp.getArena();
		if(warID != 0)
		{
			if(isProtected(event.getBlock()))
			{
				event.setCancelled(true);
				return;
			}
			if(!wp.isAttacking())
			{
				event.setCancelled(true);
				return;
			}
			War war = WarHandler.getWar(warID);
			Town defendingTown = war.getDefendingTown();
			List<TownBlock> townBlocks = defendingTown.getTownBlocks();
			WorldCoord wc = WorldCoord.parseWorldCoord(event.getBlock().getLocation());
			TownBlock tb = null;
			try
			{
				tb = wc.getTownBlock();
			}
			catch(Exception e)
			{
			}
			if(tb == null)
				return;
			for(TownBlock t : townBlocks)
			{
				if(t.equals(tb))
				{
					if(!war.attackersCanBreakBlock())
					{
						event.setCancelled(true);
						return;
					}
					else
					{
						war.attackerBreakBlock();
						event.getBlock().setType(Material.AIR);
						event.setCancelled(true);
					}
				}
			}
		}
		else if(a != null && a.isInArena(player))
		{
			if(event.getBlock().getType().equals(Material.SIGN) || event.getBlock().getType().equals(Material.SIGN_POST))
			{
				event.setCancelled(true);
				return;
			}
			if(isArenaProtected(event.getBlock(),a))
			{
				event.setCancelled(true);
				return;
			}
			if(a.getAttackingTeam().contains(player))
			{
				if(a.attackersCanBreakBlock())
				{
					a.attackerBreakBlock();
					event.getBlock().setType(Material.AIR);
					event.setCancelled(true);
				}
				else
				{
					player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Your Team Has Already Broken " + plugin.getConfig().getInt("Arena." + a.getName() + ".AttackerBlockBreakAmount") + " Blocks In The Past " + plugin.getConfig().getInt("Arena." + a.getName() + ".AttackerBlockBreakDelay") + " Seconds!");
					event.setCancelled(true);
					return;
				}
			}
			else if(a.getDefendingTeam().contains(player))
			{
				if(a.defendersCanBreakBlock())
				{
					a.defenderBreakBlock();
					event.getBlock().setType(Material.AIR);
					event.setCancelled(true);
				}
				else
				{
					player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Your Team Has Already Broken " + plugin.getConfig().getInt("Arena." + a.getName() + ".DefenderBlockBreakAmount") + " Blocks In The Past " + plugin.getConfig().getInt("Arena." + a.getName() + ".DefenderBlockBreakDelay") + " Seconds!");
					event.setCancelled(true);
					return;
				}
			}
		}
		else if(a != null && a.containsLoc(event.getBlock().getLocation()))
		{
			if(player.hasPermission("artofwar.break.arena"))
				return;
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if(WarHandler.getWarPlayer(player.getName()) != null)
		{
			plugin.getServer().getScheduler().cancelTask(WarHandler.getWarPlayer(player.getName()).getQuitID());
		}
		else
		{
			WarPlayer wp = new WarPlayer(player);
			WarHandler.addWarPlayer(wp);
		}
		
		for(Arena a : WarHandler.getActiveArenas())
		{
			if(a.getQuitPlayer().contains(player.getName()))
			{
				a.removeQuitPlayer(player.getName());
				player.getInventory().clear();
				player.getInventory().setHelmet(new ItemStack(Material.AIR));
				player.getInventory().setChestplate(new ItemStack(Material.AIR));
				player.getInventory().setLeggings(new ItemStack(Material.AIR));
				player.getInventory().setBoots(new ItemStack(Material.AIR));
				player.teleport(a.getLobby());
				return;
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeathEvent(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		WarPlayer wp = WarHandler.getWarPlayer(player.getName());
		int warID = wp.getWarID();
		Arena a = wp.getArena();
		if(warID != 0)
		{
			War war = WarHandler.getWar(warID);
			if(wp.isAttacking())
			{
				war.addDefenderPointCount();
				if(war.getDefenderWinValue() == war.getDefenderPointCount())
				{
					//Defender WIN!
					war.defenderWin();
					WarHandler.remove(war);
				}
				else
				{
					for(Resident r : war.getAttackingResidents())
					{
						plugin.getServer().getPlayer(r.getName()).sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Attackers " + ChatColor.AQUA + "["
								+ ChatColor.GOLD + war.getAttackerPointCount() + "/" + war.getAttackerWinValue() + ChatColor.AQUA + "] " + ChatColor.BLUE + "Defenders " + ChatColor.AQUA + "[" +
								ChatColor.GOLD + war.getDefenderPointCount() + "/" + war.getDefenderWinValue() + ChatColor.AQUA + "]");
					}
					for(Resident r : war.getDefendingResidents())
					{
						plugin.getServer().getPlayer(r.getName()).sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Attackers " + ChatColor.AQUA + "["
								+ ChatColor.GOLD + war.getAttackerPointCount() + "/" + war.getAttackerWinValue() + ChatColor.AQUA + "] " + ChatColor.BLUE + "Defenders " + ChatColor.AQUA + "[" +
								ChatColor.GOLD + war.getDefenderPointCount() + "/" + war.getDefenderWinValue() + ChatColor.AQUA + "]");
					}
				}
				
			}
			else
			{
				war.addAttackerPointCount();
				if(war.getAttackerWinValue() == war.getAttackerPointCount())
				{
					//Attacker WIN!
					war.attackerWin();
					WarHandler.remove(war);
				}
				else
				{
					for(Resident r : war.getAttackingResidents())
					{
						plugin.getServer().getPlayer(r.getName()).sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Attackers " + ChatColor.AQUA + "["
								+ ChatColor.GOLD + war.getAttackerPointCount() + "/" + war.getAttackerWinValue() + ChatColor.AQUA + "] " + ChatColor.BLUE + "Defenders " + ChatColor.AQUA + "[" +
								ChatColor.GOLD + war.getDefenderPointCount() + "/" + war.getDefenderWinValue() + ChatColor.AQUA + "]");
					}
					for(Resident r : war.getDefendingResidents())
					{
						plugin.getServer().getPlayer(r.getName()).sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Attackers " + ChatColor.AQUA + "["
								+ ChatColor.GOLD + war.getAttackerPointCount() + "/" + war.getAttackerWinValue() + ChatColor.AQUA + "] " + ChatColor.BLUE + "Defenders " + ChatColor.AQUA + "[" +
								ChatColor.GOLD + war.getDefenderPointCount() + "/" + war.getDefenderWinValue() + ChatColor.AQUA + "]");
					}
					
				}
				
			}
		}
		else if(a != null)
		{
			if(a.hasStarted() && a.isInArena(player))
			{
				event.getDrops().clear();
				//player.getInventory().clear();
				//player.getInventory().setHelmet(new ItemStack(Material.AIR));
				//player.getInventory().setChestplate(new ItemStack(Material.AIR));
				//player.getInventory().setLeggings(new ItemStack(Material.AIR));
				//player.getInventory().setBoots(new ItemStack(Material.AIR));
				if(!a.getAttackingTeam().contains(player))
				{
					a.addAttackersPointCounter();
					if(WarHandler.getSign() != null)
					{
						Sign s = WarHandler.getSign();
						s.setLine(3, ChatColor.BLUE + "B:" + a.getDefendersPointCounter() + ChatColor.WHITE + " " + ChatColor.RED + "R:" + a.getAttackersPointCounter());
						s.update();
					}
					
					if(a.getAttackersPointValue() == a.getAttackersPointCounter())
					{
						a.attackerWin();
					}
					else
					{
						for(Player p : a.getAttackingTeam())
						{
							p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Attackers " + ChatColor.AQUA + "["
									+ ChatColor.GOLD + a.getAttackersPointCounter() + "/" + a.getAttackersPointValue() + ChatColor.AQUA + "] " + ChatColor.BLUE + "Defenders " + ChatColor.AQUA + "[" +
									ChatColor.GOLD + a.getDefendersPointCounter() + "/" + a.getDefendersPointValue() + ChatColor.AQUA + "]");
						}
						for(Player p : a.getDefendingTeam())
						{
							p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Attackers " + ChatColor.AQUA + "["
									+ ChatColor.GOLD + a.getAttackersPointCounter() + "/" + a.getAttackersPointValue() + ChatColor.AQUA + "] " + ChatColor.BLUE + "Defenders " + ChatColor.AQUA + "[" +
									ChatColor.GOLD + a.getDefendersPointCounter() + "/" + a.getDefendersPointValue() + ChatColor.AQUA + "]");
						}
						for(Player p : a.getSpectaters())
						{
							p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Attackers " + ChatColor.AQUA + "["
									+ ChatColor.GOLD + a.getAttackersPointCounter() + "/" + a.getAttackersPointValue() + ChatColor.AQUA + "] " + ChatColor.BLUE + "Defenders " + ChatColor.AQUA + "[" +
									ChatColor.GOLD + a.getDefendersPointCounter() + "/" + a.getDefendersPointValue() + ChatColor.AQUA + "]");
						}
					}
					
				}
				else
				{
					a.addDefendersPointCounter();
					if(WarHandler.getSign() != null)
					{
						Sign s = WarHandler.getSign();
						s.setLine(3, ChatColor.BLUE + "B:" + a.getDefendersPointCounter() + ChatColor.WHITE + " " + ChatColor.RED + "R:" + a.getAttackersPointCounter());
						s.update();
					}
					if(a.getDefendersPointValue() == a.getDefendersPointCounter())
					{
						a.defenderWin();
					}
					else
					{
						for(Player p : a.getAttackingTeam())
						{
							p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Attackers " + ChatColor.AQUA + "["
									+ ChatColor.GOLD + a.getAttackersPointCounter() + "/" + a.getAttackersPointValue() + ChatColor.AQUA + "] " + ChatColor.BLUE + "Defenders " + ChatColor.AQUA + "[" +
									ChatColor.GOLD + a.getDefendersPointCounter() + "/" + a.getDefendersPointValue() + ChatColor.AQUA + "]");
						}
						for(Player p : a.getDefendingTeam())
						{
							p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Attackers " + ChatColor.AQUA + "["
									+ ChatColor.GOLD + a.getAttackersPointCounter() + "/" + a.getAttackersPointValue() + ChatColor.AQUA + "] " + ChatColor.BLUE + "Defenders " + ChatColor.AQUA + "[" +
									ChatColor.GOLD + a.getDefendersPointCounter() + "/" + a.getDefendersPointValue() + ChatColor.AQUA + "]");
						}
						for(Player p : a.getSpectaters())
						{
							p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Attackers " + ChatColor.AQUA + "["
									+ ChatColor.GOLD + a.getAttackersPointCounter() + "/" + a.getAttackersPointValue() + ChatColor.AQUA + "] " + ChatColor.BLUE + "Defenders " + ChatColor.AQUA + "[" +
									ChatColor.GOLD + a.getDefendersPointCounter() + "/" + a.getDefendersPointValue() + ChatColor.AQUA + "]");
						}
					}
					
				}
			}
		}
		else if(wp.isDueling())
		{
			event.getDrops().clear();
			DuelArena da = WarHandler.getDuelArena();
			DuelingPair dp = da.getDueling();
			player.getInventory().clear();
			player.getInventory().setHelmet(null);
			player.getInventory().setChestplate(null);
			player.getInventory().setLeggings(null);
			player.getInventory().setBoots(null);
			
			if(dp.getPlayer1().getName().equalsIgnoreCase(player.getName()))
			{
				event.getDrops().clear();
				player.sendMessage(ChatColor.RED + "You Lost The Duel!");
				Player winner = dp.getPlayer2();
				WarHandler.getWarPlayer(dp.getPlayer1().getName()).setDueling(false);
				WarHandler.getWarPlayer(dp.getPlayer2().getName()).setDueling(false);
				winner.sendMessage(ChatColor.GREEN + "You Won The Duel!");
				winner.getInventory().clear();
				winner.getInventory().setHelmet(null);
				winner.getInventory().setChestplate(null);
				winner.getInventory().setLeggings(null);
				winner.getInventory().setBoots(null);
				winner.teleport(Bukkit.getWorld("world").getSpawnLocation());
				da.startNext();
				event.getDrops().clear();
				for(Entity en : player.getNearbyEntities(100, 100, 100))
				{
					if(en instanceof Player)
					{
						((Player)en).sendMessage(ChatColor.GREEN + winner.getName() + " Won The Duel!");
					}
				}
				return;
			}
			else
			{
				event.getDrops().clear();
				player.sendMessage(ChatColor.RED + "You Lost The Duel!");
				Player winner = dp.getPlayer1();
				WarHandler.getWarPlayer(dp.getPlayer1().getName()).setDueling(false);
				WarHandler.getWarPlayer(dp.getPlayer2().getName()).setDueling(false);
				winner.sendMessage(ChatColor.GREEN + "You Won The Duel!");
				winner.getInventory().clear();
				winner.getInventory().setHelmet(null);
				winner.getInventory().setChestplate(null);
				winner.getInventory().setLeggings(null);
				winner.getInventory().setBoots(null);
				winner.teleport(Bukkit.getWorld("world").getSpawnLocation());
				da.startNext();
				for(Entity en : player.getNearbyEntities(100, 100, 100))
				{
					if(en instanceof Player)
					{
						((Player)en).sendMessage(ChatColor.GREEN + winner.getName() + " Won The Duel!");
					}
				}
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawnEvent(PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();
		WarPlayer wp = WarHandler.getWarPlayer(player.getName());
		int warID = wp.getWarID();
		Arena a = wp.getArena();
		if(warID != 0)
		{
			if(!wp.isAttacking())
			{
				Resident res;
				Town town;
				Location spawnLoc;
				try
				{
					res = TownyUniverse.getDataSource().getResident(player.getName());
					town = res.getTown();
					spawnLoc = town.getSpawn();
				}
				catch(Exception townyexception)
				{
					TownyMessaging.sendErrorMsg(player, townyexception.getMessage());
					return;
				}
				event.setRespawnLocation(spawnLoc);
			}
			else
			{
				War war = WarHandler.getWar(warID);
				event.setRespawnLocation(war.getAttackerSpawn());
			}
		}
		else if(a != null)
		{
			if(a.hasStarted())
			{
				if(a.isInArena(player))
				{
					if(a.getAttackingTeam().contains(player))
					{
						event.setRespawnLocation(a.getAttackerSpawn());
					}
					else
					{
						event.setRespawnLocation(a.getDefenderSpawn());
					}
				}
			}
		}
		
	}
	
	@EventHandler
	public void onEntityExplodeEvent(EntityExplodeEvent event)
	{
		if(event.getEntity() instanceof TNTPrimed)
		{
			Location loc = event.getLocation();
			WorldCoord wc = WorldCoord.parseWorldCoord(loc);
			TownBlock tb;
			try
			{
				tb = wc.getTownBlock();
			}
			catch(Exception e)
			{
				return;
			}
			for(War w : WarHandler.getWars())
			{
				for(TownBlock t : w.getDefendingTown().getTownBlocks())
				{
					if(t.equals(tb))
					{
						if(w.getTNTAmount() > w.getTNTCounter())
						{
							for(Block b : event.blockList())
							{
								boolean check = true;
								String[] itemIDs = plugin.getConfig().getString("War.ProtectedBlocks").split(",");
								for(String s : itemIDs)
								{
									if(b.getType().equals(Material.getMaterial(s)))
									{
										event.blockList().remove(b);
										check = false;
										break;
									}
										
								}
								if(check)
									b.setType(Material.AIR);
							}
							w.addTNTCounter();
							return;
						}
						else
						{
							event.setCancelled(true);
							return;
						}
					}
				}
			}
			for(Arena a : WarHandler.getActiveArenas())
			{
				if(a.hasStarted())
				{
					if(a.containsLoc(loc))
					{
						//if(a.getTNTCounter() < plugin.getConfig().getInt("Arena." + a.getName() + ".TNTAmount"))
						//{
							for(Block b : event.blockList())
							{
								boolean check = true;
								String[] itemIDs = plugin.getConfig().getString("Arena." + a.getName() + ".ProtectedBlocks").split(",");
								for(String s : itemIDs)
								{
									if(b.getTypeId() == Integer.valueOf(s))
									{
										event.blockList().remove(b);
										check = false;
										break;
									}
										
								}
								if(check)
									b.setType(Material.AIR);
							}
							//a.addTNTCounter();
							return;
						//}
						//else
						//{
						//	event.setCancelled(true);
						//	return;
						//}
					}
				}
			}
			
		}
	}
	
	/*@EventHandler(priority = EventPriority.HIGHEST)
	public void onNameTag(PlayerReceiveNameTagEvent event)
	{
		//Bukkit.getServer().broadcastMessage("THINGS!");
		WarPlayer wp = WarHandler.getWarPlayer(event.getNamedPlayer().getName());
		Arena a = wp.getArena();
		if(wp.getWarID() != 0)
		{
			WarPlayer wp2 = WarHandler.getWarPlayer(event.getPlayer().getName());
			if(wp2.getWarID() == wp.getWarID())
			{
				if(wp.isAttacking())
				{
					event.setTag(ChatColor.RED + event.getNamedPlayer().getName());
					//TagAPI.refreshPlayer(event.getNamedPlayer());
					//Bukkit.getServer().broadcastMessage("RED!");
				}
				else
				{
					event.setTag(ChatColor.BLUE + event.getNamedPlayer().getName());
					//TagAPI.refreshPlayer(event.getNamedPlayer());
					//Bukkit.getServer().broadcastMessage("BLUE!");
				}
			}
			else
				return;
			
		}
		else if(a != null)
		{
			if(a.isAboutToEnd() && a.isInArena(event.getNamedPlayer()))
			{
				event.setTag(ChatColor.RESET + event.getNamedPlayer().getName());
			}
			else if(a.isInArena(event.getNamedPlayer()))
			{
				if(a.getAttackingTeam().contains(event.getNamedPlayer()))
				{
					event.setTag(ChatColor.RED + event.getNamedPlayer().getName());
				}
				else
				{
					event.setTag(ChatColor.BLUE + event.getNamedPlayer().getName());
					//TagAPI.refreshPlayer(event.getNamedPlayer());
					//Bukkit.getServer().broadcastMessage("BLUE!");
				}
			}
		}
		
		
	}*/
	
	private boolean isProtected(Block block)
	{
		String[] pbs = plugin.getConfig().getString("War.ProtectedBlocks").split(",");
		{
			for(String s : pbs)
			{
				if(block.getTypeId() == Integer.valueOf(s))
					return true;
			}
			
		}
		return false;
	}
	
	private boolean isArenaProtected(Block block, Arena a)
	{
		String[] pbs = plugin.getConfig().getString("Arena." + a.getName() + ".ProtectedBlocks").split(",");
		{
			for(String s : pbs)
			{
				if(block.getTypeId() == Integer.valueOf(s))
					return true;
			}
			
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		if(player.hasPermission("artofwar.cmdoverride"))
			return;
		WarPlayer wp = WarHandler.getWarPlayer(player.getName());
		Arena a = wp.getArena();
		if(wp.getWarID() != 0)
		{
			String[] command = event.getMessage().split(" ");
			if(command[0].equalsIgnoreCase("/t") || command[0].equalsIgnoreCase("/town"))
			{
				player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "You Cant Use Town Commands While In A War!");
				event.setCancelled(true);
			}
		}
		else if(a != null)
		{
			if(a.isInArena(player))
			{
				String cmd = event.getMessage();
				if(a.hasStarted() && !cmd.equalsIgnoreCase("/war leave"))
					event.setCancelled(true);
			}
		}
		else if(wp.isDueling())
		{
			player.sendMessage("You Cant Use Commands While In A Duel!");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		final WarPlayer wp = WarHandler.getWarPlayer(player.getName());
		Arena a = wp.getArena();
		if(wp.getWarID() != 0)
		{
			final War war = WarHandler.getWar(wp.getWarID());
			final Resident resident = null;
			try
			{
				if(war.getAttackingResidents().contains(resident))
				{
					int ID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
					{
						@Override
						public void run()
						{
							war.getAttackingResidents().remove(resident);
							if(war.getAttackingResidents().size() == 0)
								war.defenderWin();
							WarHandler.removeWarPlayer(wp);
						}
					},20L*60L*plugin.getConfig().getLong("War.QuitWaitTime"));
					
					wp.setQuitID(ID);
				}
				else if(war.getDefendingResidents().contains(resident))
				{
					int ID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
					{
						@Override
						public void run()
						{
							war.getDefendingResidents().remove(resident);
							if(war.getDefendingResidents().size() == 0)
								war.attackerWin();
							WarHandler.removeWarPlayer(wp);
						}
					},20L*60L*plugin.getConfig().getLong("War.QuitWaitTime"));
					
					wp.setQuitID(ID);
				}
			}
			catch(Exception e)
			{
			}
		}
		else
		{
			WarHandler.removeWarPlayer(wp);
		}
		if(a != null)
		{
			if(a.isInQueue(player))
			{
				a.removeInQueue(player);
				if(a.hasStarted())
				{
					if(a.getAttackingTeam().contains(player))
					{
						a.removeAttacker(player);
						a.addQuitPlayer(player.getName());
					}
					else
					{
						a.removeDefender(player);
						a.addQuitPlayer(player.getName());
					}
				}
			}
		}
		if(wp.isDueling())
		{
			DuelArena da = WarHandler.getDuelArena();
			DuelingPair dp = da.getDueling();
			if(dp.getPlayer1().getName().equalsIgnoreCase(player.getName()))
			{
				player.setHealth(0);
				Player winner = dp.getPlayer2();
				WarHandler.getWarPlayer(dp.getPlayer2().getName()).setDueling(false);
				winner.sendMessage(ChatColor.GREEN + "You Won The Duel!");
				winner.getInventory().clear();
				winner.getInventory().setHelmet(null);
				winner.getInventory().setChestplate(null);
				winner.getInventory().setLeggings(null);
				winner.getInventory().setBoots(null);
				player.getInventory().clear();
				player.getInventory().setHelmet(null);
				player.getInventory().setChestplate(null);
				player.getInventory().setLeggings(null);
				player.getInventory().setBoots(null);
				winner.teleport(Bukkit.getWorld("world").getSpawnLocation());
				da.startNext();
				return;
			}
			else
			{
				player.setHealth(0);
				Player winner = dp.getPlayer1();
				WarHandler.getWarPlayer(dp.getPlayer1().getName()).setDueling(false);
				winner.sendMessage(ChatColor.GREEN + "You Won The Duel!");
				winner.getInventory().clear();
				winner.getInventory().setHelmet(null);
				winner.getInventory().setChestplate(null);
				winner.getInventory().setLeggings(null);
				winner.getInventory().setBoots(null);
				player.getInventory().clear();
				player.getInventory().setHelmet(null);
				player.getInventory().setChestplate(null);
				player.getInventory().setLeggings(null);
				player.getInventory().setBoots(null);
				winner.teleport(Bukkit.getWorld("world").getSpawnLocation());
				da.startNext();
				return;
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event)
	{
		WarPlayer wp = null;
		if(event.getDamager() instanceof Player)
		{
			Player p = (Player)event.getDamager();
			wp = WarHandler.getWarPlayer(p.getName());
		}
		else
			return;
		Arena a = wp.getArena();
		if(a != null)
		{
			if(a.hasStarted())
			{
				if(!plugin.getConfig().getBoolean("Arena." + a.getName() + ".FriendlyFire"))
				{
					Entity enDmg = event.getDamager();
					Entity en = event.getEntity();
					if((enDmg instanceof Player) && (en instanceof Player))
					{
						Player dmgPlayer = (Player)enDmg;
						Player player = (Player)en;
						if(a.isInArena(dmgPlayer) && a.isInArena(player))
						{
							if(a.getAttackingTeam().contains(dmgPlayer) && a.getAttackingTeam().contains(player))
							{
								event.setCancelled(true);
								return;
							}
							else if(a.getDefendingTeam().contains(dmgPlayer) && a.getDefendingTeam().contains(player))
							{
								event.setCancelled(true);
								return;
							}
						}
					}
					else if((enDmg instanceof Arrow) && (en instanceof Player))
					{
						Arrow arrow = (Arrow)enDmg;
						final Player player = (Player)en;
						Entity shooter = arrow.getShooter();
						if(shooter instanceof Player)
						{
							Player pSooter = (Player)shooter;
							if(a.isInArena(pSooter) && a.isInArena(player))
							{
								if(a.getAttackingTeam().contains(pSooter) && a.getAttackingTeam().contains(player))
								{
									arrow.remove();
									event.setCancelled(true);
									ItemStack is = pSooter.getItemInHand();
									if(is.containsEnchantment(Enchantment.ARROW_FIRE))
									{
										plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
										{
											@Override
											public void run()
											{
												player.setFireTicks(0);
											}
										},5L);
									}
									
									
									return;
								}
								else if(a.getDefendingTeam().contains(pSooter) && a.getDefendingTeam().contains(player))
								{
									arrow.remove();
									event.setCancelled(true);
									plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
									{
										@Override
										public void run()
										{
											player.setFireTicks(0);
										}
									},5L);
									
									return;
								}
							}
						}
					}
				}
			}
			else if(a.getInQueuePlayers().contains(wp))
			{
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			Player player = (Player)event.getEntity();
			WarPlayer wp = WarHandler.getWarPlayer(player.getName());
			Arena a = wp.getArena();
			if(a != null)
			{
				if(a.hasStarted() && a.isInArena(player))
				{
					if(a.getPotionHitList().contains(player))
					{
						a.removePotionHitList(player);
						event.setCancelled(true);
					}
				}
			}
			
		}
	}
	
	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent event)
	{
		for(Arena a : WarHandler.getActiveArenas())
		{
			if(a.hasStarted())
			{
				if(!plugin.getConfig().getBoolean("Arena." + a.getName() + ".FriendlyFire"))
				{
					if(event.getPotion().getShooter() instanceof Player)
					{
						Player player = (Player)event.getPotion().getShooter();
						if(a.isInArena(player))
						{
							if(isOffensive(event.getPotion()))
							{
								if(a.getAttackingTeam().contains(player))
								{
									for(LivingEntity le : event.getAffectedEntities())
									{
										if(le instanceof Player)
										{
											Player p = (Player)le;
											if(p.equals(player))
											{
												continue;
											}
												
											if(a.getAttackingTeam().contains(p))
											{
												removePotionEffect(event.getPotion(),p);
											}
												
										}
									}
									return;
								}
								else
								{
									for(LivingEntity le : event.getAffectedEntities())
									{
										if(le instanceof Player)
										{
											Player p = (Player)le;
											if(p.equals(player))
											{
												continue;
											}
											if(a.getDefendingTeam().contains(p))
											{
												removePotionEffect(event.getPotion(),p);
											}
												
										}
									}
									return;
								}
							}
						}
					}
				}
			}
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		WarPlayer wp = WarHandler.getWarPlayer(player.getName());
		Arena a = wp.getArena();
		if(a != null)
		{
			if(a.hasStarted())
			{
				if(a.isInArena(player))
				{
					if(event.getAction().equals(Action.LEFT_CLICK_BLOCK))
					{
						Block b = event.getClickedBlock();
						if((b.getState() instanceof Sign) && a.containsLoc(b.getLocation()))
						{
							Sign sign = (Sign)b.getState();
							if(sign.getLine(1).equalsIgnoreCase("[war]"))
							{
								if(sign.getLine(3).equalsIgnoreCase("a") && !a.getAttackingTeam().contains(player))
								{
									return;
								}
								if(sign.getLine(3).equalsIgnoreCase("d") && !a.getDefendingTeam().contains(player))
								{
									return;
								}
								String name = sign.getLine(2);
								Kit kit = KitHandler.getKit(name);
								if(kit == null)
									return;
								kit.use(player);
								/*if(plugin.getConfig().contains("Kits." + s))
								{
									PlayerInventory inv = player.getInventory();
									List<ItemStack> things = new ArrayList<ItemStack>();
									//player.getInventory().clear();
									//player.setItemInHand(new ItemStack(Material.AIR));
									//player.getInventory().setItem(0, new ItemStack(Material.AIR));
									//player.getInventory().remove(player.getInventory().getItem(0));
									String item = plugin.getConfig().getString("Kits." + s + ".Helmet");
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
										inv.setHelmet(is);
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
										inv.setHelmet(is);
									}
									
									item = plugin.getConfig().getString("Kits." + s + ".Chestplate");
									
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
										inv.setChestplate(is);
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
										inv.setChestplate(is);
									}
									
									item = plugin.getConfig().getString("Kits." + s + ".Leggings");
									
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
										inv.setLeggings(is);
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
										inv.setLeggings(is);
									}
									
									item = plugin.getConfig().getString("Kits." + s + ".Boots");
									
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
										inv.setBoots(is);
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
										inv.setBoots(is);
									}
									
									//inv.setChestplate(new ItemStack(plugin.getConfig().getInt("Kits." + s + ".Chestplate")));
									//inv.setLeggings(new ItemStack(plugin.getConfig().getInt("Kits." + s + ".Leggings")));
									//inv.setBoots(new ItemStack(plugin.getConfig().getInt("Kits." + s + ".Boots")));
									List<String> items = plugin.getConfig().getStringList("Kits." + s + ".Items");
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
									ItemStack[] tdad = new ItemStack[things.size()]; 
									for(int i = 0;i<things.size();++i)
									{
										tdad[i] = things.get(i);
									}
									inv.setContents(tdad);
								}*/
							}
						}
					}
				}
			}
		}
		else if(wp.isDueling())
		{
			if(event.getAction().equals(Action.LEFT_CLICK_BLOCK))
			{
				Block b = event.getClickedBlock();
				if((b.getState() instanceof Sign))
				{
					Sign sign = (Sign)b.getState();
					if(sign.getLine(1).equalsIgnoreCase("[duel]"))
					{
						String s = sign.getLine(2);
						if(plugin.getConfig().contains("Kits." + s))
						{
							PlayerInventory inv = player.getInventory();
							List<ItemStack> things = new ArrayList<ItemStack>();
							//player.getInventory().clear();
							//player.setItemInHand(new ItemStack(Material.AIR));
							//player.getInventory().setItem(0, new ItemStack(Material.AIR));
							//player.getInventory().remove(player.getInventory().getItem(0));
							String item = plugin.getConfig().getString("Kits." + s + ".Helmet");
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
								inv.setHelmet(is);
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
								inv.setHelmet(is);
							}
							
							item = plugin.getConfig().getString("Kits." + s + ".Chestplate");
							
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
								inv.setChestplate(is);
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
								inv.setChestplate(is);
							}
							
							item = plugin.getConfig().getString("Kits." + s + ".Leggings");
							
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
								inv.setLeggings(is);
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
								inv.setLeggings(is);
							}
							
							item = plugin.getConfig().getString("Kits." + s + ".Boots");
							
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
								inv.setBoots(is);
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
								inv.setBoots(is);
							}
							
							//inv.setChestplate(new ItemStack(plugin.getConfig().getInt("Kits." + s + ".Chestplate")));
							//inv.setLeggings(new ItemStack(plugin.getConfig().getInt("Kits." + s + ".Leggings")));
							//inv.setBoots(new ItemStack(plugin.getConfig().getInt("Kits." + s + ".Boots")));
							List<String> items = plugin.getConfig().getStringList("Kits." + s + ".Items");
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
							ItemStack[] tdad = new ItemStack[things.size()]; 
							for(int i = 0;i<things.size();++i)
							{
								tdad[i] = things.get(i);
							}
							inv.setContents(tdad);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		WarPlayer wp = WarHandler.getWarPlayer(player.getName());
		Arena a = wp.getArena();
		if(a != null)
		{
			if(a.hasStarted())
			{
				
				if(a.isInArena(player))
				{
					//event.getItemDrop().remove();
					event.setCancelled(true);
				}
			}
		}
		if(wp.isDueling())
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerTeleportEvent(PlayerTeleportEvent event)
	{
		Player p = event.getPlayer();
		WarPlayer wp = WarHandler.getWarPlayer(p.getName());
		if(!wp.isDueling())
			return;
		if(!WarHandler.getDuelArena().containsLoc(event.getTo()))
		{
			event.setCancelled(true);
			return;
		}
	}
	
	private boolean isOffensive(ThrownPotion tp)
	{
		for(PotionEffect pe : tp.getEffects())
		{
			if(pe.getType().equals(PotionEffectType.BLINDNESS) || pe.getType().equals(PotionEffectType.CONFUSION) || pe.getType().equals(PotionEffectType.HARM) || pe.getType().equals(PotionEffectType.HUNGER) || pe.getType().equals(PotionEffectType.POISON) || pe.getType().equals(PotionEffectType.SLOW) || pe.getType().equals(PotionEffectType.WEAKNESS) || pe.getType().equals(PotionEffectType.WITHER))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean isWithingBorder(War war, Location loc)
	{
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		World world = loc.getWorld();
		
		int borderSize = plugin.getConfig().getInt("War.AttackerBorder");
		
		WorldCoord wc1 = WorldCoord.parseWorldCoord(new Location(world,x+borderSize,y,z));
		WorldCoord wc2 = WorldCoord.parseWorldCoord(new Location(world,x-borderSize,y,z));
		WorldCoord wc3 = WorldCoord.parseWorldCoord(new Location(world,x,y,z+borderSize));
		WorldCoord wc4 = WorldCoord.parseWorldCoord(new Location(world,x,y,z-borderSize));
		TownBlock tb1 = null;
		TownBlock tb2 = null;
		TownBlock tb3 = null;
		TownBlock tb4 = null;
		try
		{
			tb1 = wc1.getTownBlock();
		}
		catch(Exception e)
		{
		}
		
		try
		{
			tb2 = wc2.getTownBlock();
		}
		catch(Exception e)
		{
		}
		
		try
		{
			tb3 = wc3.getTownBlock();
		}
		catch(Exception e)
		{
		}
		
		try
		{
			tb4 = wc4.getTownBlock();
		}
		catch(Exception e)
		{
		}
		
		if(tb1 != null && war.getDefendingTown().getTownBlocks().contains(tb1))
		{
			return true;
		}
		else if(tb2 != null && war.getDefendingTown().getTownBlocks().contains(tb2))
		{
			return true;
		}
		else if(tb3 != null && war.getDefendingTown().getTownBlocks().contains(tb3))
		{
			return true;
		}
		else if(tb4 != null && war.getDefendingTown().getTownBlocks().contains(tb4))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void removePotionEffect(ThrownPotion tp, final Player p)
	{
		for(PotionEffect pe : tp.getEffects())
		{
			if(pe.getType().equals(PotionEffectType.BLINDNESS))
			{
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						p.removePotionEffect(PotionEffectType.BLINDNESS);
					}
				},5L);
			}
			else if(pe.getType().equals(PotionEffectType.CONFUSION))
			{
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						p.removePotionEffect(PotionEffectType.CONFUSION);
					}
				},5L);
			}
			else if(pe.getType().equals(PotionEffectType.HARM))
			{
				WarPlayer wp = WarHandler.getWarPlayer(p.getName());
				Arena a = wp.getArena();
				a.addPotionHitList(p);
			}
			else if(pe.getType().equals(PotionEffectType.HUNGER))
			{
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						p.removePotionEffect(PotionEffectType.HUNGER);
					}
				},5L);
			}
			else if(pe.getType().equals(PotionEffectType.POISON))
			{
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						p.removePotionEffect(PotionEffectType.POISON);
					}
				},5L);
			}
			else if(pe.getType().equals(PotionEffectType.SLOW))
			{
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						p.removePotionEffect(PotionEffectType.SLOW);
					}
				},5L);
			}
			else if(pe.getType().equals(PotionEffectType.WEAKNESS))
			{
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						p.removePotionEffect(PotionEffectType.WEAKNESS);
					}
				},5L);
			}
			else if(pe.getType().equals(PotionEffectType.WITHER))
			{
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						p.removePotionEffect(PotionEffectType.WITHER);
					}
				},5L);
			}
		}
	}
	
	private boolean isInt(String s)
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
	
	private boolean isInventoryEmpty(Player p)
	{
		for(ItemStack s : p.getInventory().getContents())
		{
			if(s != null && !s.getType().equals(Material.AIR))
				return false;
		}
		for(ItemStack s : p.getInventory().getArmorContents())
		{
			if(s != null && !s.getType().equals(Material.AIR))
				return false;
		}
		return true;
	}
	
}
