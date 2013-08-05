package me.frodenkvist.artofwar;

import java.io.*;
import java.util.zip.ZipInputStream;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.ItemSpawnEvent;

// Referenced classes of package de.pylamo.pylamorestorationsystem:
//            PylamoRestorationSystem, RestorationRegion, SaveAction, TotallySerializableBlock, 
//            RegionRestoredEvent

public class RestorationAction
    implements Listener, Runnable
{

    public RestorationAction(RestorationRegion rr)
    {
        this.rr = rr;
        File bgfolder = new File((new StringBuilder(String.valueOf(ArtOfWar.plugin.getDataFolder().getPath()))).append(File.separator).append("regions").toString());
        File bg = new File((new StringBuilder(String.valueOf(bgfolder.getPath()))).append(File.separator).append(rr.name).append(".state").toString());
        try
        {
            FileInputStream fis = new FileInputStream(bg);
            ZipInputStream zis = new ZipInputStream(fis);
            zis.getNextEntry();
            ois = new ObjectInputStream(zis);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return;
        }
        //Bukkit.getPluginManager().registerEvents(this, PylamoRestorationSystem.plugin);
        Bukkit.getScheduler().scheduleSyncDelayedTask(ArtOfWar.plugin, this);
    }

    public void run()
    {
        for(int i = 0; i < offsetsPerTick; i++)
        {
            Block b = rr.start.getWorld().getBlockAt(rr.start.getBlockX() + x, rr.start.getBlockY() + y, rr.start.getBlockZ() + z);
            try
            {
                TotallySerializableBlock tsb = (TotallySerializableBlock)ois.readObject();
                if(tsb != null)
                    tsb.convertToBlock(b);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                rr.restoring = false;
                HandlerList.unregisterAll(this);
                //Bukkit.getPluginManager().callEvent(new RegionRestoredEvent(rr));
                return;
            }
            z++;
            if((double)z >= rr.v.getZ())
            {
                z = 0;
                y++;
            }
            if((double)y >= rr.v.getY())
            {
                y = 0;
                x++;
            }
            if((double)x >= rr.v.getX())
            {
                rr.restoring = false;
                HandlerList.unregisterAll(this);
                //Bukkit.getPluginManager().callEvent(new RegionRestoredEvent(rr));
                return;
            }
            i++;
        }

        if((double)x <= rr.v.getX())
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(ArtOfWar.plugin, this);
        }
        else
        {
            rr.restoring = false;
            HandlerList.unregisterAll(this);
            //Bukkit.getPluginManager().callEvent(new RegionRestoredEvent(rr));
        }
    }

    public void restore()
    {
        rr.restoring = true;
        try
        {
            maxoffset = (int)(rr.v.getX() * rr.v.getY() * rr.v.getZ());
            Bukkit.getScheduler().scheduleSyncDelayedTask(ArtOfWar.plugin, this);
        }
        catch(Exception exception) { }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event)
    {
        if(rr.restoring)
        {
            boolean s = (double)event.getBlock().getX() >= rr.start.getX() && (double)event.getBlock().getY() >= rr.start.getY() && (double)event.getBlock().getZ() >= rr.start.getZ();
            boolean e = (double)event.getBlock().getX() <= rr.end.getX() && (double)event.getBlock().getY() <= rr.end.getY() && (double)event.getBlock().getZ() <= rr.end.getZ();
            if(s && e)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockMelt(BlockFadeEvent event)
    {
        if(rr.restoring)
        {
            boolean s = (double)event.getBlock().getX() >= rr.start.getX() && (double)event.getBlock().getY() >= rr.start.getY() && (double)event.getBlock().getZ() >= rr.start.getZ();
            boolean e = (double)event.getBlock().getX() <= rr.end.getX() && (double)event.getBlock().getY() <= rr.end.getY() && (double)event.getBlock().getZ() <= rr.end.getZ();
            if(s && e)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event)
    {
        if(rr.restoring)
        {
            boolean s = (double)event.getLocation().getBlockX() >= rr.start.getX() && (double)event.getLocation().getBlockY() >= rr.start.getY() && (double)event.getLocation().getBlockZ() >= rr.start.getZ();
            boolean e = (double)event.getLocation().getBlockX() <= rr.end.getX() && (double)event.getLocation().getBlockY() <= rr.end.getY() && (double)event.getLocation().getBlockZ() <= rr.end.getZ();
            if(s && e)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        if(rr.restoring)
        {
            boolean s = (double)event.getBlock().getX() >= rr.start.getX() && (double)event.getBlock().getY() >= rr.start.getY() && (double)event.getBlock().getZ() >= rr.start.getZ();
            boolean e = (double)event.getBlock().getX() <= rr.end.getX() && (double)event.getBlock().getY() <= rr.end.getY() && (double)event.getBlock().getZ() <= rr.end.getZ();
            if(s && e)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event)
    {
        if(rr.restoring)
        {
            boolean s = (double)event.getBlock().getX() >= rr.start.getX() && (double)event.getBlock().getY() >= rr.start.getY() && (double)event.getBlock().getZ() >= rr.start.getZ();
            boolean e = (double)event.getBlock().getX() <= rr.end.getX() && (double)event.getBlock().getY() <= rr.end.getY() && (double)event.getBlock().getZ() <= rr.end.getZ();
            if(s && e)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(rr.restoring)
        {
            boolean s = (double)event.getBlock().getX() >= rr.start.getX() && (double)event.getBlock().getY() >= rr.start.getY() && (double)event.getBlock().getZ() >= rr.start.getZ();
            boolean e = (double)event.getBlock().getX() <= rr.end.getX() && (double)event.getBlock().getY() <= rr.end.getY() && (double)event.getBlock().getZ() <= rr.end.getZ();
            if(s && e)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(rr.restoring)
        {
            boolean s = (double)event.getBlock().getX() >= rr.start.getX() && (double)event.getBlock().getY() >= rr.start.getY() && (double)event.getBlock().getZ() >= rr.start.getZ();
            boolean e = (double)event.getBlock().getX() <= rr.end.getX() && (double)event.getBlock().getY() <= rr.end.getY() && (double)event.getBlock().getZ() <= rr.end.getZ();
            if(s && e)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurned(BlockBurnEvent event)
    {
        if(rr.restoring)
        {
            boolean s = (double)event.getBlock().getX() >= rr.start.getX() && (double)event.getBlock().getY() >= rr.start.getY() && (double)event.getBlock().getZ() >= rr.start.getZ();
            boolean e = (double)event.getBlock().getX() <= rr.end.getX() && (double)event.getBlock().getY() <= rr.end.getY() && (double)event.getBlock().getZ() <= rr.end.getZ();
            if(s && e)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        if(rr.restoring)
        {
            boolean s = (double)event.getBlock().getX() >= rr.start.getX() && (double)event.getBlock().getY() >= rr.start.getY() && (double)event.getBlock().getZ() >= rr.start.getZ();
            boolean e = (double)event.getBlock().getX() <= rr.end.getX() && (double)event.getBlock().getY() <= rr.end.getY() && (double)event.getBlock().getZ() <= rr.end.getZ();
            if(s && e)
                event.setCancelled(true);
        }
    }

    public static int offsetsPerTick = 10000;
    public static int item = 336;
    RestorationRegion rr;
    int offset;
    int maxoffset;
    ObjectInputStream ois = null;
    int x;
    int y;
    int z;

}
