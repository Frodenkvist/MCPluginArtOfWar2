// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SaveAction.java

package me.frodenkvist.artofwar;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.ItemSpawnEvent;

// Referenced classes of package de.pylamo.pylamorestorationsystem:
//            PylamoRestorationSystem, RestorationRegion, TotallySerializableBlock, RegionRestoredEvent

public class SaveAction
    implements Listener, Runnable
{

    public SaveAction(RestorationRegion rr)
    {
        saving = false;
        this.rr = rr;
        try
        {
            File regionFolder = new File((new StringBuilder(String.valueOf(ArtOfWar.plugin.getDataFolder().getPath()))).append(File.separator).append("regions").toString());
            File bg = new File((new StringBuilder(String.valueOf(regionFolder.getPath()))).append(File.separator).append(rr.name).append(".state").toString());
            FileOutputStream fos = new FileOutputStream(bg);
            ZipOutputStream zos = new ZipOutputStream(fos);
            zos.putNextEntry(new ZipEntry("data"));
            oos = new ObjectOutputStream(zos);
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        if(oos == null)
        {
            return;
        } else
        {
            Bukkit.getPluginManager().registerEvents(this, ArtOfWar.plugin);
            rr.saving = true;
            Bukkit.getScheduler().scheduleSyncDelayedTask(ArtOfWar.plugin, this);
            return;
        }
    }

    public void run()
    {
        for(int i = 0; i < offsetsPerTick; i++)
        {
            Block b = rr.start.getWorld().getBlockAt(rr.start.getBlockX() + x, rr.start.getBlockY() + y, rr.start.getBlockZ() + z);
            try
            {
                oos.writeObject(new TotallySerializableBlock(b));
            }
            catch(IOException e)
            {
                rr.saving = false;
                HandlerList.unregisterAll(this);
                try
                {
                    oos.close();
                }
                catch(IOException sad)
                {
                    e.printStackTrace();
                }
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
                rr.saving = false;
                HandlerList.unregisterAll(this);
                try
                {
                    oos.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                //Bukkit.getPluginManager().callEvent(new RegionRestoredEvent(rr));
                return;
            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(ArtOfWar.plugin, this);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event)
    {
        if(rr.saving)
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
        if(rr.saving)
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
        if(rr.saving)
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
        if(rr.saving)
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
        if(rr.saving)
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
        if(rr.saving)
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
        if(rr.saving)
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
        if(rr.saving)
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
        if(rr.saving)
        {
            boolean s = (double)event.getBlock().getX() >= rr.start.getX() && (double)event.getBlock().getY() >= rr.start.getY() && (double)event.getBlock().getZ() >= rr.start.getZ();
            boolean e = (double)event.getBlock().getX() <= rr.end.getX() && (double)event.getBlock().getY() <= rr.end.getY() && (double)event.getBlock().getZ() <= rr.end.getZ();
            if(s && e)
                event.setCancelled(true);
        }
    }

    public static int offsetsPerTick = 20000;
    public RestorationRegion rr;
    public ObjectOutputStream oos;
    boolean saving;
    int x;
    int y;
    int z;

}

