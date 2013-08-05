package me.frodenkvist.artofwar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;

public class ArtOfWar extends JavaPlugin
{
	public File configFile;
	static boolean townyCheck = false;
    static boolean tagAPICheck = false;
    static boolean armorEditorCheck = false;
	public final Logger logger = Logger.getLogger("Minecraft");
	public static ArtOfWar plugin;
	public final PlayerListener pl = new PlayerListener(this);
	public final TagListener tl = new TagListener();
	//public static Permission permission = null;
    //public static Economy economy = null;
    //public static Chat chat = null;
    public Location lobby;
    //public Location pos1;
    //public Location pos2;
    //public static Essentials ess;
    
    public static WorldEdit we;
	public static WorldEditPlugin wep;
	
	@Override
	public void onDisable()
	{
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Has Been Disabled!");
	}
	
	@Override
	public void onEnable()
	{
		//setupEconomy();
		plugin = this;
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Has Been Enabled!");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.pl, this);
		if(pm.isPluginEnabled("Towny"))
		{
			townyCheck = true;
			this.logger.info("Towny Found");
		}
		if(pm.isPluginEnabled("EpicGear"))
		{
			armorEditorCheck = true;
			this.logger.info("EpicGear Found");
		}
		if(pm.isPluginEnabled("TagAPI"))
		{
			tagAPICheck = true;
			pm.registerEvents(tl, this);
			this.logger.info("TagAPI Found");
		}
		loadPlayers();
		KitHandler.load();
		//loadLobby();
		configFile = new File(getDataFolder(), "config.yml");
		try
		{
			firstRun();
	    }
		catch (Exception e)
		{
	        e.printStackTrace();
	    }
		//saveDefaultConfig();
		loadArenas();
		//loadDuelArena();
		loadPortalsAndSign();
		//loadJpos();
		//ess = (Essentials) pm.getPlugin("Essentials");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			/*if(pos1 != null && pos2 != null)
			{
				if(containsLoc(player.getLocation()))
					return true;
			}*/
			if(commandLabel.equalsIgnoreCase("war"))
			{
				/*if(args[0].equalsIgnoreCase("test"))
				{
					player.setCustomName("Forkster");
					player.setCustomNameVisible(true);
					//Bukkit.broadcastMessage(player.getCustomName() + " " + player.isCustomNameVisible());
					return true;
				}*/
				/*if(args.length == 3 && args[0].equalsIgnoreCase("declare") && player.hasPermission("artofwar.use"))
				{
					Resident resident;
					Town town;
					try
					{
						resident = TownyUniverse.getDataSource().getResident(player.getName());
						town = resident.getTown();
					}
					catch(TownyException townyexception)
					{
						TownyMessaging.sendErrorMsg(player, townyexception.getMessage());
						return true;
					}
					if(resident.isMayor())
					{
						Town challangedTown;
						Resident challangedMayor;
						try
						{
							challangedTown = TownyUniverse.getDataSource().getTown(args[1]);
							challangedMayor = challangedTown.getMayor();
						}
						catch(TownyException townyexception)
						{
							TownyMessaging.sendErrorMsg(player, townyexception.getMessage());
							return true;
						}
						double challangeAmount = Double.valueOf(args[2]);
						if(economy.getBalance(player.getName()) >= challangeAmount)
						{
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "You Have Challanged " + challangedTown.getName() + " To A War!");
							getServer().getPlayer(challangedMayor.getName()).sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "The Town " + ChatColor.AQUA + town.getName() + ChatColor.GREEN + " Has challanged Your Town To A War, The Bets Are: " + ChatColor.AQUA + args[2]);
							getServer().getPlayer(challangedMayor.getName()).sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Type " + ChatColor.AQUA + "/war accept" + ChatColor.GREEN + " To Accept The Invitation");
							final WarPlayer wp = WarHandler.getWarPlayer(challangedMayor.getName());
							wp.setchallanged(true);
							wp.setchallangeTown(town);
							wp.setchallangeAmount(challangeAmount);
							getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
							{
								@Override
								public void run()
								{
									wp.setchallanged(false);
									wp.setchallangeAmount(0);
								}
							},(long) (20L * getConfig().getDouble("War.ChallangeAcceptTime")));
						}
						else
						{
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "You Dont Have Enough Money!");
							return true;
						}
						
					}
				}*/
				if(args.length == 2 && args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("duel"))
				{
					if(!player.hasPermission("artofwar.create.duel"))
					{
						player.sendMessage(ChatColor.RED + "You Don't Have Permission To Use This Command!");
						return false;
					}
					we = WorldEdit.getInstance();
					wep = (WorldEditPlugin)getServer().getPluginManager().getPlugin("WorldEdit");
					Region region = null;
					try
					{
						region = wep.getSession(player).getSelection(wep.wrapPlayer(player).getWorld());
					}
					catch (IncompleteRegionException e)
					{
						e.printStackTrace();
					}
					DuelArena da = new DuelArena(new Location(player.getWorld(),region.getMinimumPoint().getBlockX(),region.getMinimumPoint().getBlockY(),region.getMinimumPoint().getBlockZ())
					,new Location(player.getWorld(),region.getMaximumPoint().getBlockX(),region.getMaximumPoint().getBlockY(),region.getMaximumPoint().getBlockZ()));
					WarHandler.setDuelArena(da);
					getConfig().set("DuelArena.HighLoc.x", da.getHighLoc().getBlockX());
					getConfig().set("DuelArena.HighLoc.y", da.getHighLoc().getBlockY());
					getConfig().set("DuelArena.HighLoc.z", da.getHighLoc().getBlockZ());
					getConfig().set("DuelArena.HighLoc.world", da.getHighLoc().getWorld().getName());
					getConfig().set("DuelArena.LowLoc.x", da.getLowLoc().getBlockX());
					getConfig().set("DuelArena.LowLoc.y", da.getLowLoc().getBlockY());
					getConfig().set("DuelArena.LowLoc.z", da.getLowLoc().getBlockZ());
					getConfig().set("DuelArena.LowLoc.world", da.getLowLoc().getWorld().getName());
					saveConfig();
					player.sendMessage(ChatColor.GREEN + "duel Arena Created");
					return true;
				}
				else if(args.length == 3 && args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("portal"))
				{
					we = WorldEdit.getInstance();
					wep = (WorldEditPlugin)getServer().getPluginManager().getPlugin("WorldEdit");
					Region region = null;
					try
					{
						region = wep.getSession(player).getSelection(wep.wrapPlayer(player).getWorld());
					}
					catch (IncompleteRegionException e)
					{
						e.printStackTrace();
					}
					if(WarHandler.getPortal(args[2]) != null)
					{
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Portal Name Taken!");
						return false;
					}
					Portal p = new Portal(new Location(player.getWorld(),region.getMinimumPoint().getBlockX(),region.getMinimumPoint().getBlockY(),region.getMinimumPoint().getBlockZ())
					,new Location(player.getWorld(),region.getMaximumPoint().getBlockX(),region.getMaximumPoint().getBlockY(),region.getMaximumPoint().getBlockZ()),args[2]);
					WarHandler.addPortal(p);
					getConfig().set("Portals." + p.getName() + ".HighLoc.x", p.getHighLoc().getBlockX());
					getConfig().set("Portals." + p.getName() + ".HighLoc.y", p.getHighLoc().getBlockY());
					getConfig().set("Portals." + p.getName() + ".HighLoc.z", p.getHighLoc().getBlockZ());
					getConfig().set("Portals." + p.getName() + ".HighLoc.world", p.getHighLoc().getWorld().getName());
					getConfig().set("Portals." + p.getName() + ".LowLoc.x", p.getLowLoc().getBlockX());
					getConfig().set("Portals." + p.getName() + ".LowLoc.y", p.getLowLoc().getBlockY());
					getConfig().set("Portals." + p.getName() + ".LowLoc.z", p.getLowLoc().getBlockZ());
					getConfig().set("Portals." + p.getName() + ".LowLoc.world", p.getLowLoc().getWorld().getName());
					saveConfig();
					player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Portal Created");
					return true;
				}
				else if(args.length == 3 && args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("duel"))
				{
					if(args[1].equalsIgnoreCase("duel"))
					{
						if(args[2].equalsIgnoreCase("spawn1"))
						{
							if(!player.hasPermission("artofwar.set.duel.spawn1"))
							{
								player.sendMessage(ChatColor.RED + "You Don't Have Permission To Use This Command!");
								return false;
							}
							DuelArena da = WarHandler.getDuelArena();
							if(da == null)
							{
								player.sendMessage(ChatColor.RED + "You Need To Create A Duel Arena First!");
								return false;
							}
							Location loc = player.getLocation();
							da.setSpawn1(loc);
							getConfig().set("DuelArena.Spawn1.x", loc.getBlockX());
							getConfig().set("DuelArena.Spawn1.y", loc.getBlockY());
							getConfig().set("DuelArena.Spawn1.z", loc.getBlockZ());
							getConfig().set("DuelArena.Spawn1.world", loc.getWorld().getName());
							saveConfig();
							player.sendMessage(ChatColor.GREEN + "Spawn1 Set");
						}
						else if(args[2].equalsIgnoreCase("spawn2"))
						{
							if(!player.hasPermission("artofwar.set.duel.spawn2"))
							{
								player.sendMessage(ChatColor.RED + "You Don't Have Permission To Use This Command!");
								return false;
							}
							DuelArena da = WarHandler.getDuelArena();
							if(da == null)
							{
								player.sendMessage(ChatColor.RED + "You Need To Create A Duel Arena First!");
								return false;
							}
							Location loc = player.getLocation();
							da.setSpawn2(loc);
							getConfig().set("DuelArena.Spawn2.x", loc.getBlockX());
							getConfig().set("DuelArena.Spawn2.y", loc.getBlockY());
							getConfig().set("DuelArena.Spawn2.z", loc.getBlockZ());
							getConfig().set("DuelArena.Spawn2.world", loc.getWorld().getName());
							saveConfig();
							player.sendMessage(ChatColor.GREEN + "Spawn2 Set");
						}
						else if(args[2].equalsIgnoreCase("startspawn1"))
						{
							if(!player.hasPermission("artofwar.set.duel.startspawn1"))
							{
								player.sendMessage(ChatColor.RED + "You Don't Have Permission To Use This Command!");
								return false;
							}
							DuelArena da = WarHandler.getDuelArena();
							if(da == null)
							{
								player.sendMessage(ChatColor.RED + "You Need To Create A Duel Arena First!");
								return false;
							}
							Location loc = player.getLocation();
							da.setStartSpawn1(loc);
							getConfig().set("DuelArena.StartSpawn1.x", loc.getBlockX());
							getConfig().set("DuelArena.StartSpawn1.y", loc.getBlockY());
							getConfig().set("DuelArena.StartSpawn1.z", loc.getBlockZ());
							getConfig().set("DuelArena.StartSpawn1.yaw", loc.getYaw());
							getConfig().set("DuelArena.StartSpawn1.pitch", loc.getPitch());
							getConfig().set("DuelArena.StartSpawn1.world", loc.getWorld().getName());
							saveConfig();
							player.sendMessage(ChatColor.GREEN + "Start Spawn1 Set");
						}
						else if(args[2].equalsIgnoreCase("startspawn2"))
						{
							if(!player.hasPermission("artofwar.set.duel.startspawn2"))
							{
								player.sendMessage(ChatColor.RED + "You Don't Have Permission To Use This Command!");
								return false;
							}
							DuelArena da = WarHandler.getDuelArena();
							if(da == null)
							{
								player.sendMessage(ChatColor.RED + "You Need To Create A Duel Arena First!");
								return false;
							}
							Location loc = player.getLocation();
							da.setStartSpawn2(loc);
							getConfig().set("DuelArena.StartSpawn2.x", loc.getBlockX());
							getConfig().set("DuelArena.StartSpawn2.y", loc.getBlockY());
							getConfig().set("DuelArena.StartSpawn2.z", loc.getBlockZ());
							getConfig().set("DuelArena.StartSpawn2.yaw", loc.getYaw());
							getConfig().set("DuelArena.StartSpawn2.pitch", loc.getPitch());
							getConfig().set("DuelArena.StartSpawn2.world", loc.getWorld().getName());
							saveConfig();
							player.sendMessage(ChatColor.GREEN + "Start Spawn2 Set");
						}
					}
				}
				else if(args.length == 2 && args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("sign"))
				{
					Block b = player.getTargetBlock(null, 30);
					if(b.getState() instanceof Sign)
					{
						Sign s = (Sign)b.getState();
						s.setLine(0, "");
						s.setLine(1, ChatColor.GREEN + "Arena Waiting For Players");
						s.setLine(2, ChatColor.AQUA + "0/0");
						s.setLine(3, "");
						s.update();
						WarHandler.setSign(s);
						getConfig().set("Sign.Loc.x", b.getLocation().getBlockX());
						getConfig().set("Sign.Loc.y", b.getLocation().getBlockY());
						getConfig().set("Sign.Loc.z", b.getLocation().getBlockZ());
						getConfig().set("Sign.Loc.world", b.getLocation().getWorld().getName());
						saveConfig();
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Sign Created");
						return true;
					}
				}
				/*else if(args.length == 1 && args[0].equalsIgnoreCase("accept"))
				{
					final WarPlayer wp = WarHandler.getWarPlayer(player.getName());
					Resident resident;
					try
					{
						resident = TownyUniverse.getDataSource().getResident(player.getName());
					}
					catch(TownyException townyexception)
					{
						TownyMessaging.sendErrorMsg(player, townyexception.getMessage());
						return true;
					}
					
					if(wp.ischallanged())
					{
						if(economy.getBalance(wp.getName()) >= wp.getchallangeAmount())
						{
							final Town challangeTown = wp.getchallangeTown();
							final Town acceptTown;
							try
							{
								acceptTown = resident.getTown();
							}
							catch(TownyException townyexception)
							{
								TownyMessaging.sendErrorMsg(player, townyexception.getMessage());
								return true;
							}
							getServer().getPlayer(challangeTown.getMayor().getName()).sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Your challange Has Been Accepted!");
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "You Accepted The challange!");
							
							Resident challangeMayor = challangeTown.getMayor();
							Resident acceptMayor = acceptTown.getMayor();
							
							if(challangeMayor.hasNation())
							{
								Nation challangeNation = null;
								try
								{
									challangeNation = challangeTown.getNation();
								}
								catch(Exception e)
								{
								}
								
								for(Town t : challangeNation.getTowns())
								{
									//Bukkit.getServer().broadcastMessage(t.getName());
									for(Resident r : t.getResidents())
									{
										boolean check = true;
										for(Resident r2 : challangeTown.getResidents())
										{
											if(r2.equals(r))
											{
												check = false;
												break;
											}
										}
										if(check)
										{
											WarPlayer wp2 = WarHandler.getWarPlayer(r.getName());
											if(wp2 != null)
											{
												wp2.setAllyAsked(true);
												wp2.getPlayer().sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "The Town " + ChatColor.AQUA + acceptTown.getName() + ChatColor.GREEN + " Is Asking For Your Aid!");
												wp2.getPlayer().sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Type " + ChatColor.AQUA + "/war accept" + ChatColor.GREEN + " To Accept The Invitation");
											}
										}
									}
								}
							}
							
							if(acceptMayor.hasNation())
							{
								Nation acceptNation = null;
								try
								{
									acceptNation = acceptTown.getNation();
								}
								catch(Exception e)
								{
								}
								
								for(Town t : acceptNation.getTowns())
								{
									
									for(Resident r : t.getResidents())
									{
										boolean check = true;
										for(Resident r2 : acceptTown.getResidents())
										{
											if(r2.equals(r))
											{
												check = false;
												break;
											}
										}
										if(check)
										{
											WarPlayer wp2 = WarHandler.getWarPlayer(r.getName());
											if(wp2 != null)
											{
												wp2.setAllyAsked(true);
												wp2.getPlayer().sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "The Town " + ChatColor.AQUA + acceptTown.getName() + ChatColor.GREEN + " Is Asking For Your Aid!");
												wp2.getPlayer().sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Type " + ChatColor.AQUA + "/war accept" + ChatColor.GREEN + " To Accept The Invitation");
											}
											
										}
									}
								}
								
								
							}
							
							
							for(Resident r : challangeTown.getResidents())
							{
								Player p = getServer().getPlayer(r.getName());
								if(p != null)
								{
									p.teleport(lobby);
									p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + "The War Is About To Start You Have " + getConfig().getLong("War.PrepTime") + " Minutes To Get Ready!");
								}								
							}
							for(Resident r : acceptTown.getResidents())
							{
								Player p = getServer().getPlayer(r.getName());
								if(p != null)
								{
									p.teleport(lobby);
									p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + "The War Is About To Start You Have " + getConfig().getLong("War.PrepTime") + " Minutes To Get Ready!");
								}
							}
							getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
							{
								@Override
								public void run()
								{
									
									wp.setchallanged(false);
									
									Resident challangeMayor = challangeTown.getMayor();
									Resident acceptMayor = acceptTown.getMayor();
									
									if(challangeMayor.hasNation())
									{
										Nation challangeNation = null;
										try
										{
											challangeNation = challangeTown.getNation();
										}
										catch(Exception e)
										{
										}
										
										boolean check = true;
										for(Resident r : challangeNation.getResidents())
										{
											for(Resident r2 : challangeTown.getResidents())
											{
												if(r2.equals(r))
												{
													check = false;
													break;
												}
											}
											if(check)
											{
												WarPlayer wp2 = WarHandler.getWarPlayer(r.getName());
												if(wp2 != null)
													wp2.setAllyAsked(false);
											}
										}
										
									}
									
									if(acceptMayor.hasNation())
									{
										Nation acceptNation = null;
										try
										{
											acceptNation = acceptTown.getNation();
										}
										catch(Exception e)
										{
										}
										
										boolean check = true;
										for(Resident r : acceptNation.getResidents())
										{
											for(Resident r2 : acceptTown.getResidents())
											{
												if(r2.equals(r))
												{
													check = false;
													break;
												}
											}
											if(check)
											{
												WarPlayer wp2 = WarHandler.getWarPlayer(r.getName());
												if(wp2 != null)
													wp2.setAllyAsked(false);
											}
										}
										
									}
									startWar(challangeTown,acceptTown,wp.getchallangeAmount());
									
								}
							},20L*60L*getConfig().getLong("War.PrepTime"));
							
						}
						else
						{
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "You Dont Have Enough Money!");
							return true;
						}
						
					}
					else if(wp.getAllyAsked())
					{
						wp.setAllyAsked(false);
						wp.setAllyAccept(true);
						wp.getPlayer().sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "You Accepted!");
					}
					else
					{
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "You Have Not Been challanged!");
						return true;
					}
				}*/
				else if(args.length >= 2 && args[0].equalsIgnoreCase("set"))
				{
					/*if(args[1].equalsIgnoreCase("lobby"))
					{
						if(player.hasPermission("artofwar.set.lobby"))
						{
							lobby = player.getLocation();
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Lobby Set!");
							getConfig().set("War.Lobby.x", lobby.getBlockX());
							getConfig().set("War.Lobby.y", lobby.getBlockY());
							getConfig().set("War.Lobby.z", lobby.getBlockZ());
							getConfig().set("War.Lobby.World", lobby.getWorld().getName());
							saveConfig();
						}
					}
					/*else if(args.length >= 3 && args[1].equalsIgnoreCase("jail"))
					{
						if(args[2].equalsIgnoreCase("pos1"))
						{
							setPos1(player.getLocation());
						}
						else if(args[2].equalsIgnoreCase("pos1"))
						{
							setPos2(player.getLocation());
						}
					}*/
					if(args.length >= 3 && player.hasPermission("artofwar.set.arena"))
					{
						if(WarHandler.arenaExists(args[1]))
						{
							Arena a = WarHandler.getArena(args[1]);
							if(args[2].equalsIgnoreCase("town"))
							{
								if(!townyCheck)
								{
									player.sendMessage(ChatColor.RED + "Towny Is Not Enabled!");
									return false;
								}
								WorldCoord wc = WorldCoord.parseWorldCoord(player.getLocation());
								Town town;// = wc.getTownBlock().getTown();
								try
								{
									town = wc.getTownBlock().getTown();
								}
								catch(Exception townyexception)
								{
									TownyMessaging.sendErrorMsg(player, townyexception.getMessage());
									return true;
								}
								a.setTown(town);
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Town Set!");
								return true;
							}
							/*else if(args[2].equalsIgnoreCase("pos1"))
							{
								a.setPos1(player.getLocation());
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Pos1 Set!");
							}
							else if(args[2].equalsIgnoreCase("pos2"))
							{
								a.setPos2(player.getLocation());
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Pos2 Set!");
							}*/
							else if(args[2].equalsIgnoreCase("attacker") && args.length == 3)
							{
								a.setAttackerSpawn(player.getLocation());
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Attacker Spawn Set!");
							}
							else if(args[2].equalsIgnoreCase("defender") && args.length == 3)
							{
								a.setDefenderSpawn(player.getLocation());
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Defender Spawn Set!");
							}
							else if(args[2].equalsIgnoreCase("lobby"))
							{
								a.setLobby(player.getLocation());
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Lobby Set!");
							}
							else if(args[2].equalsIgnoreCase("subzone"))
							{
								we = WorldEdit.getInstance();
								wep = (WorldEditPlugin)getServer().getPluginManager().getPlugin("WorldEdit");
								Region region = null;
								try
								{
									region = wep.getSession(player).getSelection(wep.wrapPlayer(player).getWorld());
								}
								catch (IncompleteRegionException e)
								{
									player.sendMessage(ChatColor.RED + "You Must Select A Region First!");
									return false;
								}
								a.setSub1(new Location(player.getWorld(),region.getMinimumPoint().getBlockX(),region.getMinimumPoint().getBlockY(),region.getMinimumPoint().getBlockZ()));
								a.setSub2(new Location(player.getWorld(),region.getMaximumPoint().getBlockX(),region.getMaximumPoint().getBlockY(),region.getMaximumPoint().getBlockZ()));
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Sub Zone Set!");
								//a.setSub2(player.getLocation());
							}
							/*else if(args[2].equalsIgnoreCase("sub2"))
							{
								a.setSub2(player.getLocation());
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Sub2 Set!");
								//a.setSub2(player.getLocation());
							}*/
						}
						else
						{
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Cant Find " + ChatColor.AQUA + args[1]);
						}
					}
				}
				else if(args.length == 2 && args[0].equalsIgnoreCase("save") && player.hasPermission("artofwar.save.arena"))
				{
					if(WarHandler.arenaExists(args[1]))
					{
						Arena a = WarHandler.getArena(args[1]);
						a.save();
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + args[1] + ChatColor.GREEN + " Saved!");
					}
					else
					{
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Cant Find " + ChatColor.AQUA + args[1]);
					}
				}
				else if(args.length == 2 && args[0].equalsIgnoreCase("load") &&  player.hasPermission("artofwar.load.arena"))
				{
					if(WarHandler.arenaExists(args[1]))
					{
						Arena a = WarHandler.getArena(args[1]);
						a.loadSave();
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + args[1] + ChatColor.GREEN + " Loaded!");
					}
					else
					{
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Cant Find " + ChatColor.AQUA + args[1]);
					}
				}
				else if(args.length == 2 && args[0].equalsIgnoreCase("create") &&  player.hasPermission("artofwar.create.arena"))
				{
					if(!WarHandler.arenaExists(args[1]))
					{
						we = WorldEdit.getInstance();
						wep = (WorldEditPlugin)getServer().getPluginManager().getPlugin("WorldEdit");
						Region region = null;
						try
						{
							region = wep.getSession(player).getSelection(wep.wrapPlayer(player).getWorld());
						}
						catch (IncompleteRegionException e)
						{
							player.sendMessage(ChatColor.RED + "You Must Select A Region First!");
							return false;
						}
						Arena arena = new Arena(args[1]);
						arena.setPos1(new Location(player.getWorld(),region.getMinimumPoint().getBlockX(),region.getMinimumPoint().getBlockY(),region.getMinimumPoint().getBlockZ()));
						arena.setPos2(new Location(player.getWorld(),region.getMaximumPoint().getBlockX(),region.getMaximumPoint().getBlockY(),region.getMaximumPoint().getBlockZ()));
						arena.save();
						WarHandler.addArena(arena);
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "You Created The Arena: " + ChatColor.AQUA + args[1]);
						getConfig().set("Arena." + args[1] + ".Active", false);
						//List<String> temp = getConfig().getStringList("ArenaNames");
						//temp.add(args[1]);
						//getConfig().set("ArenaNames", temp);
						saveConfig();
					}
					else
					{
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "The Arena: " + ChatColor.AQUA + args[1] + ChatColor.RED + " Already Exists!");
					}
					
				}
				else if(args.length == 1 && args[0].equalsIgnoreCase("reload") &&  player.hasPermission("artofwar.reload"))
				{
					reloadConfig();
					loadLobby();
					loadPlayers();
					WarHandler.getActiveArenas().clear();
					WarHandler.getArenas().clear();
					loadArenas();
					player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "Art Of War Reloaded!");
				}
				/*else if(args.length == 1 && args[0].equalsIgnoreCase("surrender"))
				{
					WarPlayer wp = WarHandler.getWarPlayer(player.getName());
					if(wp.getWarID() != 0)
					{
						Resident resident = null;
						try
						{
							resident = TownyUniverse.getDataSource().getResident(player.getName());
						}
						catch(Exception e)
						{
						}
						if(resident.isMayor())
						{
							War war = WarHandler.getWar(wp.getWarID());
							if(war.getAttackingMayor().equals(resident))
							{
								war.defenderWin();
							}
							else if(war.getDefendingMayor().equals(resident))
							{
								war.attackerWin();
							}
						}
					}
				}*/
				else if(args.length == 2 && args[0].equalsIgnoreCase("join") && !getConfig().getBoolean("RandomArena"))
				{
					if(WarHandler.arenaExists(args[1]))
					{
						Arena a = WarHandler.getArena(args[1]);
						if(a.isActive())
						{
							if(!a.isInQueue(player))
							{
								if(!a.hasStarted())
								{
									if(plugin.getConfig().getBoolean("Arena." + a.getName() + ".CheckEmptyInventoryOnJoin"))
									{
										if(!isInventoryEmpty(player))
										{
											player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Please Empty Your Inventory Before Joining!");
											return true;
										}
											
									}
									for(Player p : a.getInQueuePlayers())
									{
										p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + player.getName() + " Joined The Game!");
										p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] [" + ChatColor.GOLD + String.valueOf((a.getInQueuePlayers().size() + 1)) + "/" + getConfig().getInt("Arena." + a.getName() + ".QueueSize") + ChatColor.AQUA + "] " + ChatColor.GREEN + "Will Start Automatically When Full");
									}
									player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + player.getName() + " Joined The Game!");
									player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] [" + ChatColor.GOLD + String.valueOf((a.getInQueuePlayers().size() + 1)) + "/" + getConfig().getInt("Arena." + a.getName() + ".QueueSize") + ChatColor.AQUA + "] " + ChatColor.GREEN + "Will Start Automatically When Full");
									player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + player.getName() + " Type: " + ChatColor.AQUA + "/war leave " + ChatColor.GREEN + "To Leave The Queue");
									
									a.addToQueue(player);
								}
								else
								{
									player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "The Arena Has Already Started!");
								}
							}
						}
						else
						{
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "That Arena Is Not Active!");
						}
						
					}
					else
					{
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Cant Find " + ChatColor.AQUA + args[1]);
					}
					
				}
				else if(args.length == 1 && args[0].equalsIgnoreCase("join") && getConfig().getBoolean("RandomArena"))
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
									return true;
								}
									
							}
							for(Player p : a.getInQueuePlayers())
							{
								p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + player.getName() + " Joined The Game!");
								p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] [" + ChatColor.GOLD + String.valueOf((a.getInQueuePlayers().size() + 1)) + "/" + getConfig().getInt("Arena." + a.getName() + ".QueueSize") + ChatColor.AQUA + "] " + ChatColor.GREEN + "Will Start Automaticly When Full");
							}
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + player.getName() + " Joined The Game!");
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] [" + ChatColor.GOLD + String.valueOf((a.getInQueuePlayers().size() + 1)) + "/" + getConfig().getInt("Arena." + a.getName() + ".QueueSize") + ChatColor.AQUA + "] " + ChatColor.GREEN + "Will Start Automaticly When Full");
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + player.getName() + " Type: " + ChatColor.AQUA + "/war leave " + ChatColor.GREEN + "To Leave The Queue");
							
							a.addToQueue(player);
						}
						else
						{
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "The Arena Has Already Started!");
						}
					}
					
				}
				else if(args.length == 1 && args[0].equalsIgnoreCase("leave"))
				{
					for(Arena a : WarHandler.getActiveArenas())
					{
						if(a.isInQueue(player))
						{
							if(!a.hasStarted())
							{
								a.removeInQueue(player);
								for(Player p : a.getInQueuePlayers())
								{
									p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + player.getName() + " Left The Game!");
									p.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] [" + ChatColor.GOLD + String.valueOf((a.getInQueuePlayers().size())) + "/" + getConfig().getInt("Arena." + a.getName() + ".QueueSize") + ChatColor.AQUA + "] " + ChatColor.GREEN + "Will Start Automaticly When Full");
									WarHandler.getSign().setLine(2, ChatColor.AQUA + "" + String.valueOf((a.getInQueuePlayers().size())) + "/21");
									WarHandler.getSign().update();
								}
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + " You Left The Game!");
							}
								
						}
					}
					
				}
				else if(args.length == 3 && args[0].equalsIgnoreCase("spectate") && player.hasPermission("artofwar.spectate") && !getConfig().getBoolean("RandomArena"))
				{
					if(WarHandler.arenaExists(args[1]))
					{
						Arena a = WarHandler.getArena(args[1]);
						if(!a.hasStarted())
						{
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "The Is Currently No One Playing The Arena!");
						}
						else if(args[2].equalsIgnoreCase("join"))
						{
							if(!a.getSpectaters().contains(player))
							{
								a.addSpectater(player);
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "You Joined The Spectaters!");
							}
							else
							{
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "You Are Already A Spectater!");
							}
						}
						else if(args[2].equalsIgnoreCase("leave"))
						{
							if(a.getSpectaters().contains(player))
							{
								a.removeSpectater(player);
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "You Left The Spectaters!");
							}
							else
							{
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "You Are Not A Spectater!");
							}
						}
					}
					else
					{
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Cant Find " + ChatColor.AQUA + args[1]);
					}
					
				}
				else if(args.length == 2 && args[0].equalsIgnoreCase("spectate") && player.hasPermission("artofwar.spectate") && getConfig().getBoolean("RandomArena"))
				{
					Arena a = WarHandler.getActiveArenas().get(0);
					if(!a.hasStarted())
					{
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "The Is Currently No One Playing The Arena!");
					}
					else if(args[1].equalsIgnoreCase("join"))
					{
						if(!a.getSpectaters().contains(player))
						{
							a.addSpectater(player);
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "You Joined The Spectaters!");
						}
						else
						{
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "You Are Already A Spectater!");
						}
					}
					else if(args[1].equalsIgnoreCase("leave"))
					{
						if(a.getSpectaters().contains(player))
						{
							a.removeSpectater(player);
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "You Left The Spectaters!");
						}
						else
						{
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.GREEN + "You Are Not A Spectater!");
						}
					}
				}
				else if(args.length >= 1 && args[0].equalsIgnoreCase("force"))
				{
					if(args.length >= 2 && args[1].equalsIgnoreCase("start"))
					{
						if(args.length == 3 && player.hasPermission("artofwar.force.start.arena"))
						{
							if(WarHandler.arenaExists(args[2]))
							{
								Arena a = WarHandler.getArena(args[2]);
								a.start();
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + args[2] + ChatColor.GREEN + " Force Started!");
							}
							else
							{
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Cant Find " + ChatColor.AQUA + args[2]);
							}
						}
					}
					else if(args.length >= 2 && args[1].equalsIgnoreCase("end"))
					{
						if(args.length == 3 && player.hasPermission("artofwar.force.end.arena"))
						{
							if(WarHandler.arenaExists(args[2]))
							{
								Arena a = WarHandler.getArena(args[2]);
								a.forceEnd();
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + args[2] + ChatColor.GREEN + " Force Ended!");
							}
							else
							{
								player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Cant Find " + ChatColor.AQUA + args[2]);
							}
						}
					}
				}
				else if(args.length == 2 && args[0].equalsIgnoreCase("activate") && player.hasPermission("artofwar.activate"))
				{
					if(WarHandler.arenaExists(args[1]))
					{
						Arena a = WarHandler.getArena(args[1]);
						if(a.isActive())
						{
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + args[1] + ChatColor.RED + " Is Already Active!");
							return true;
						}
						a.activate();
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + args[1] + ChatColor.GREEN + " Active!");
					}
					else
					{
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Cant Find " + ChatColor.AQUA + args[1]);
					}
				}
				else if(args.length == 2 && args[0].equalsIgnoreCase("deactivate") && player.hasPermission("artofwar.deactivate"))
				{
					if(WarHandler.arenaExists(args[1]))
					{
						Arena a = WarHandler.getArena(args[1]);
						if(!a.isActive())
						{
							player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + args[1] + ChatColor.RED + " Is Already Deactive!");
							return true;
						}
						a.deactivate();
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + args[1] + ChatColor.GREEN + " Deactive!");
					}
					else
					{
						player.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Cant Find " + ChatColor.AQUA + args[1]);
					}
				}
				else if(args.length > 2 && args[0].equalsIgnoreCase("kick") && player.hasPermission("artofwar.kick"))
				{
					WarPlayer wp = WarHandler.getWarPlayer(args[1]);
					if(wp != null)
					{
						if(wp.getArena() != null && wp.getArena().hasStarted())
						{
							Arena a = wp.getArena();
							a.kick(wp.getPlayer());
							String msg = new String();
							for(int i = 2;i<args.length;++i)
							{
								msg += args[i];
							}
							wp.getPlayer().sendMessage(ChatColor.RED + "You Got Kicked From The Arena For: " + ChatColor.AQUA + msg);
						}
					}
				}
				else if(args.length == 0)
				{
					player.sendMessage(ChatColor.GOLD + "===================COMMANDS========================");
					player.sendMessage(ChatColor.AQUA + "/war accept - accept a challange & to accept an ally invite");
					player.sendMessage(ChatColor.AQUA + "/war lobby to set lobby to your location");
					player.sendMessage(ChatColor.AQUA + "/war delcare <townname> to challange another town to a war");
					player.sendMessage(ChatColor.AQUA + "/war surrender - mayor can type to surrender (will lose)");
				}
				/*else if(args.length == 1 && args[0].equalsIgnoreCase("color") && player.isOp())
				{
					TagAPI.refreshPlayer(player);
				}*/
			}
			else if(commandLabel.equalsIgnoreCase("duel"))
			{
				if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("decline"))
					{
						WarPlayer wp = WarHandler.getWarPlayer(player.getName());
						if(wp.getChallangedBy() == null)
						{
							player.sendMessage(ChatColor.RED + "You Dont Have A Challenge Right Now!");
							return false;
						}
						player.sendMessage(ChatColor.GREEN + "Challenge Declined");
						Player target = getServer().getPlayer(wp.getChallangedBy());
						if(target != null)
						{
							target.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.RED + " Declined Your Challenge!");
						}
						return true;
					}
					else if(args[0].equalsIgnoreCase("accept"))
					{
						WarPlayer wp = WarHandler.getWarPlayer(player.getName());
						if(wp.getChallangedBy() == null)
						{
							player.sendMessage(ChatColor.RED + "You Dont Have A Challenge Right Now!");
							return false;
						}
						
						Player target = getServer().getPlayer(wp.getChallangedBy());
						if(target == null)
						{
							player.sendMessage(ChatColor.AQUA + wp.getChallangedBy() + ChatColor.RED + " Is Not Online Anymore!");
							return false;
						}
						target.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.GREEN  + " Accepted Your Challenge!");
						player.sendMessage(ChatColor.GREEN + "Challenge Accepted");
						WarHandler.getDuelArena().tryStartDuel(new DuelingPair(player,target));
						return true;
					}
				}
				else if(args.length == 2)
				{
					if(args[0].equalsIgnoreCase("challenge"))
					{
						Player target = getServer().getPlayer(args[1]);
						if(target == null)
						{
							player.sendMessage(ChatColor.RED + "Can't Find Player!");
							return false;
						}
						WarPlayer targetWP = WarHandler.getWarPlayer(target.getName());
						targetWP.setChallangedBy(player.getName());
						player.sendMessage(ChatColor.GREEN + "Challenge Sent");
						target.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.GREEN + " Has Challenge You To A Duel, Type " + ChatColor.AQUA + "/duel accept " + ChatColor.GREEN + 
								"To Accept The Challenge, Type " + ChatColor.AQUA + "/duel decline " + ChatColor.GREEN + "To Decline The Challenge,"
								+ ChatColor.BOLD + ChatColor.RED + " CLEAR YOUR INVENTORY BEFORE ACCEPTING!");
						return true;
					}
				}
			}
			
		}
		else
		{
			if(args.length >= 1 && args[0].equalsIgnoreCase("force"))
			{
				if(args.length >= 2 && args[1].equalsIgnoreCase("start"))
				{
					if(WarHandler.arenaExists(args[2]))
					{
						Arena a = WarHandler.getArena(args[2]);
						a.start();
						sender.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + args[2] + ChatColor.GREEN + " Force Started!");
					}
					else
					{
						sender.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Cant Find " + ChatColor.AQUA + args[2]);
					}
				}
				else if(args.length >= 2 && args[1].equalsIgnoreCase("end"))
				{
					if(WarHandler.arenaExists(args[2]))
					{
						Arena a = WarHandler.getArena(args[2]);
						a.forceEnd();
						sender.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.AQUA + args[2] + ChatColor.GREEN + " Force Ended!");
					}
					else
					{
						sender.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "WAR" + ChatColor.AQUA + "] " + ChatColor.RED + "Cant Find " + ChatColor.AQUA + args[2]);
					}
				}
			}
		}
		
		
		return true;
	}

    /*private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null)
        {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
    
    /*private void startWar(Town challangeTown,Town acceptTown,double challangeAmount)
    {
    	War war = new War(this,challangeTown,acceptTown,challangeAmount);
    	WarHandler.addWar(war);
    	List<Resident> challangeResidents = challangeTown.getResidents();
    	for(Resident r : challangeResidents)
    	{
    		WarPlayer wp = WarHandler.getWarPlayer(r.getName());
    		if(wp != null)
    			wp.setWarID(war.getID());
    	}
    	List<Resident> acceptResidents = acceptTown.getResidents();
    	for(Resident r : acceptResidents)
    	{
    		WarPlayer wp = WarHandler.getWarPlayer(r.getName());
    		if(wp != null)
    			wp.setWarID(war.getID());
    	}
    	war.startWar();
    }*/
    
    private void firstRun() throws Exception
	{
	    if(!configFile.exists())
	    {
	        configFile.getParentFile().mkdirs();
	        copy(getResource("config.yml"), configFile);
	    }
	}
	private void copy(InputStream in, File file)
	{
	    try
	    {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0)
	        {
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
	
	private void loadPlayers()
	{
		for(Player p : getServer().getOnlinePlayers())
		{
			WarHandler.addWarPlayer(new WarPlayer(p));
		}
	}
	
	private void loadLobby()
	{
		try
		{
			lobby = new Location(getServer().getWorld(getConfig().getString("War.Lobby.World")),getConfig().getInt("War.Lobby.x"),getConfig().getInt("War.Lobby.y"),getConfig().getInt("War.Lobby.z"));
		}
		catch(Exception e)
		{
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
	
	private void loadArenas()
	{
		//List<String> arenaNames = getConfig().getStringList("ArenaNames");
		if(getConfig().getConfigurationSection("Arena") == null)
			return;
		for(String s : getConfig().getConfigurationSection("Arena").getKeys(false))
		{
			Arena a = new Arena(s);
			a.load();
			WarHandler.addArena(a);
		}
	}
	
	private void loadPortalsAndSign()
	{
		if(getConfig().contains("Sign.Loc"))
		{
			Location loc = new Location(getServer().getWorld(getConfig().getString("Sign.Loc.world")), getConfig().getInt("Sign.Loc.x"), getConfig().getInt("Sign.Loc.y"), getConfig().getInt("Sign.Loc.z"));
			Block b = loc.getBlock();
			if(b.getState() instanceof Sign)
			{
				Sign s = (Sign)b.getState();
				s.setLine(0, "");
				s.setLine(1, ChatColor.GREEN + "Arena Waiting");
				s.setLine(2, ChatColor.AQUA + "0/" + getConfig().getInt("Arena." + WarHandler.getActiveArenas().get(0).getName() + ".QueueSize"));
				s.setLine(3, "");
				s.update();
				WarHandler.setSign(s);
			}
		}
		if(getConfig().getConfigurationSection("Portals") == null)
			return;
		for(String s : getConfig().getConfigurationSection("Portals").getKeys(false))
		{
			Location start = new Location(getServer().getWorld(getConfig().getString("Portals." + s + ".HighLoc.world")), getConfig().getInt("Portals." + s + ".HighLoc.x")
					, getConfig().getInt("Portals." + s + ".HighLoc.y"), getConfig().getInt("Portals." + s + ".HighLoc.z"));
			Location end = new Location(getServer().getWorld(getConfig().getString("Portals." + s + ".LowLoc.world")), getConfig().getInt("Portals." + s + ".LowLoc.x")
					, getConfig().getInt("Portals." + s + ".LowLoc.y"), getConfig().getInt("Portals." + s + ".LowLoc.z"));
			WarHandler.addPortal(new Portal(start,end,s));
		}
	}
	
	/*private void loadDuelArena()
	{
		Location start = new Location(getServer().getWorld(getConfig().getString("DuelArena.HighLoc.world")), getConfig().getInt("DuelArena.HighLoc.x")
				, getConfig().getInt("DuelArena.HighLoc.y"), getConfig().getInt("DuelArena.HighLoc.z"));
		Location end = new Location(getServer().getWorld(getConfig().getString("DuelArena.LowLoc.world")), getConfig().getInt("DuelArena.LowLoc.x")
				, getConfig().getInt("DuelArena.LowLoc.y"), getConfig().getInt("DuelArena.LowLoc.z"));
		DuelArena da = new DuelArena(start,end);
		if(getConfig().contains("DuelArena.Spawn1.world"))
		{
			da.setSpawn1(new Location(getServer().getWorld(getConfig().getString("DuelArena.Spawn1.world")), getConfig().getInt("DuelArena.Spawn1.x")
				, getConfig().getInt("DuelArena.Spawn1.y"), getConfig().getInt("DuelArena.Spawn1.z")));
		}
		if(getConfig().contains("DuelArena.Spawn2.world"))
		{
			da.setSpawn2(new Location(getServer().getWorld(getConfig().getString("DuelArena.Spawn2.world")), getConfig().getInt("DuelArena.Spawn2.x")
				, getConfig().getInt("DuelArena.Spawn2.y"), getConfig().getInt("DuelArena.Spawn2.z")));
		}
		if(getConfig().contains("DuelArena.StartSpawn1.world"))
		{
			da.setStartSpawn1(new Location(getServer().getWorld(getConfig().getString("DuelArena.StartSpawn1.world")), getConfig().getInt("DuelArena.StartSpawn1.x")
				, getConfig().getInt("DuelArena.StartSpawn1.y"), getConfig().getInt("DuelArena.StartSpawn1.z")));
		}
		if(getConfig().contains("DuelArena.StartSpawn2.world"))
		{
			da.setStartSpawn2(new Location(getServer().getWorld(getConfig().getString("DuelArena.StartSpawn2.world")), getConfig().getInt("DuelArena.StartSpawn2.x")
				, getConfig().getInt("DuelArena.StartSpawn2.y"), getConfig().getInt("DuelArena.StartSpawn2.z")));
		}
		WarHandler.setDuelArena(da);
	}*/
}
