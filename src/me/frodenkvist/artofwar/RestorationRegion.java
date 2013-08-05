package me.frodenkvist.artofwar;

import java.io.File;
import java.io.IOException;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class RestorationRegion
    implements Listener
{

    public RestorationRegion(String name, Location start, Location end)
    {
        timedRestoration = false;
        timedSave = false;
        timer = 10;
        saveTimer = 20;
        task = -1;
        savetask = -1;
        this.name = name;
        v = new Vector(Math.abs(end.getBlockX() - start.getBlockX()) + 1, Math.abs(end.getBlockY() - start.getBlockY()) + 1, Math.abs(end.getBlockZ() - start.getBlockZ()) + 1);
        int startx = start.getBlockX();
        int endx = end.getBlockX();
        if(startx > endx)
        {
            int buffer = startx;
            startx = endx;
            endx = buffer;
        }
        int starty = start.getBlockY();
        int endy = end.getBlockY();
        if(starty > endy)
        {
            int buffer = starty;
            starty = endy;
            endy = buffer;
        }
        int startz = start.getBlockZ();
        int endz = end.getBlockZ();
        if(startz > endz)
        {
            int buffer = startz;
            startz = endz;
            endz = buffer;
        }
        this.start = new Location(start.getWorld(), startx, starty, startz);
        this.end = new Location(start.getWorld(), endx, endy, endz);
        //Bukkit.getServer().getPluginManager().registerEvents(this, PylamoRestorationSystem.plugin);
        //saveState();
        //save();
    }

    
    public void saveState()
    {
        if(!ArtOfWar.plugin.getDataFolder().exists())
        	ArtOfWar.plugin.getDataFolder().mkdir();
        File regionFolder = new File((new StringBuilder(String.valueOf(ArtOfWar.plugin.getDataFolder().getPath()))).append(File.separator).append("regions").toString());
        if(!regionFolder.exists())
            regionFolder.mkdir();
        new SaveAction(this);
    }
    
    public void save()
    {
        File bgfolder = new File((new StringBuilder(String.valueOf(ArtOfWar.plugin.getDataFolder().getPath()))).append(File.separator).append("regions").toString());
        File bg = new File((new StringBuilder(String.valueOf(bgfolder.getPath()))).append(File.separator).append(name).append(".region").toString());
        try
        {
            YamlConfiguration yc = new YamlConfiguration();
            yc.set("RestorationRegion.name", name);
            yc.set("RestorationRegion.world", start.getWorld().getName());
            yc.set("RestorationRegion.startx", Double.valueOf(start.getX()));
            yc.set("RestorationRegion.starty", Double.valueOf(start.getY()));
            yc.set("RestorationRegion.startz", Double.valueOf(start.getZ()));
            yc.set("RestorationRegion.endx", Double.valueOf(end.getX()));
            yc.set("RestorationRegion.endy", Double.valueOf(end.getY()));
            yc.set("RestorationRegion.endz", Double.valueOf(end.getZ()));
            yc.set("RestorationRegion.timer enabled", Boolean.valueOf(false));
            yc.set("RestorationRegion.timer time", Integer.valueOf(10));
            try
            {
                yc.save(bg);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        catch(Exception exception) { }
    }

    
    /*public void convertOldFiles()
    {
        File bgfolder = new File((new StringBuilder(String.valueOf(PylamoRestorationSystem.plugin.getDataFolder().getPath()))).append(File.separator).append("regions").toString());
        File bg = new File((new StringBuilder(String.valueOf(bgfolder.getPath()))).append(File.separator).append(name).append(".state").toString());
        ObjectInputStream ois;
        Object o;
        FileOutputStream fos;
        ObjectOutputStream oos;
        ZipOutputStream zos;
        Exception e;
        TotallySerializableBlock tsbs[][][];
        int z;
        int y;
        int x;
        try
        {
            FileInputStream fis = new FileInputStream(bg);
            ZipInputStream zis = new ZipInputStream(fis);
            zis.getNextEntry();
            ois = new ObjectInputStream(zis);
            o = ois.readObject();
            if(o instanceof TotallySerializableBlock)
            {
                ois.close();
                return;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return;
        }
        if(!(o instanceof TotallySerializableBlock[][][]))
            break MISSING_BLOCK_LABEL_343;
        oos = null;
        try
        {
            fos = new FileOutputStream(bg);
            zos = new ZipOutputStream(fos);
            zos.putNextEntry(new ZipEntry("data"));
            oos = new ObjectOutputStream(zos);
        }
        // Misplaced declaration of an exception variable
        catch(Exception e)
        {
            e.printStackTrace();
            oos.close();
            return;
        }
        Bukkit.getLogger().log(Level.INFO, (new StringBuilder("Converting region ")).append(name).append(" to new file format").toString());
        tsbs = (TotallySerializableBlock[][][])o;
        for(z = 0; z < tsbs.length; z++)
            for(y = 0; y < tsbs[y].length; y++)
                for(x = 0; x < tsbs[x][y].length;)
                {
                    oos.writeObject(tsbs[x][y][z]);
                    z++;
                }



        oos.close();
        ois.close();
        break MISSING_BLOCK_LABEL_381;
        Bukkit.getLogger().log(Level.WARNING, (new StringBuilder("You have to recreate the restoration region ")).append(name).toString());
        break MISSING_BLOCK_LABEL_381;
        Exception exception;
        exception;
    }*/

    

    public void restoreRegion()
    {
            new RestorationAction(this);
    }


    Vector v;
    Location start;
    Location end;
    public String name;
    public boolean restoring;
    public boolean saving;
    public boolean timedRestoration;
    public boolean timedSave;
    public int timer;
    public int saveTimer;
    int task;
    int savetask;
    //public static HashSet registeredRegions = new HashSet();

}
