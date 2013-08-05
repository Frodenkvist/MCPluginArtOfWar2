
package me.frodenkvist.artofwar;

import java.io.Serializable;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.inventory.*;


public class TotallySerializableBlock
    implements Serializable
{

    public TotallySerializableBlock(Block b)
    {
        saveBlocks(b);
    }

    private void saveBlocks(Block b)
    {
        data = b.getData();
        id = b.getTypeId();
        rawdata = b.getState().getRawData();
        if(b.getState() instanceof Chest)
        {
            Chest c = (Chest)b.getState();
            inv = new SerializableInventory(c.getInventory());
            data = c.getData().getData();
            id = c.getTypeId();
        }
        if(b.getState() instanceof BrewingStand)
        {
            BrewingStand c = (BrewingStand)b.getState();
            inv = new SerializableInventory(c.getInventory());
            data = c.getData().getData();
            id = c.getTypeId();
        }
        if(b.getState() instanceof Dispenser)
        {
            Dispenser c = (Dispenser)b.getState();
            inv = new SerializableInventory(c.getInventory());
            data = c.getData().getData();
            id = c.getTypeId();
        }
        if(b.getState() instanceof Sign)
        {
            Sign c = (Sign)b.getState();
            data = c.getData().getData();
            id = c.getTypeId();
            signcontent = c.getLines();
        }
    }

    public void convertToBlock(Block out)
    {
        out.setTypeId(id);
        out.setData(data);
        out.getState().setRawData(rawdata);
        Block b = out;
        if(b.getState() instanceof DoubleChest)
        {
            DoubleChest c = (DoubleChest)b.getState();
            ItemStack stacks[] = new ItemStack[27];
            System.arraycopy(inv.convertToContent(), 27, stacks, 0, 27);
            c.getInventory().setContents(stacks);
        }
        if(b.getState() instanceof Chest)
        {
            Chest c = (Chest)b.getState();
            ItemStack stacks[] = new ItemStack[27];
            if(b.getRelative(1, 0, 0).getType() == Material.CHEST)
                c.getInventory().setContents(inv.convertToContent());
            else
            if(b.getRelative(0, 0, 1).getType() == Material.CHEST)
                c.getInventory().setContents(inv.convertToContent());
            else
            if(b.getRelative(-1, 0, 0).getType() == Material.CHEST)
                c.getInventory().setContents(inv.convertToContent());
            else
            if(b.getRelative(0, 0, -1).getType() == Material.CHEST)
            {
                c.getInventory().setContents(inv.convertToContent());
            } else
            {
                System.arraycopy(inv.convertToContent(), 0, stacks, 0, 27);
                c.getInventory().setContents(stacks);
            }
        }
        if(b.getState() instanceof BrewingStand)
        {
            BrewingStand c = (BrewingStand)b.getState();
            c.getInventory().setContents(inv.convertToContent());
        }
        if(b.getState() instanceof Dispenser)
        {
            Dispenser c = (Dispenser)b.getState();
            c.getInventory().setContents(inv.convertToContent());
        }
        if(b.getState() instanceof Sign)
        {
            Sign c = (Sign)b.getState();
            if(signcontent == null)
                return;
            for(int i = 0; i < signcontent.length; i++)
                c.setLine(i, signcontent[i]);

            c.update();
        }
    }

    private static final long serialVersionUID = 1L;
    SerializableInventory inv;
    byte data;
    byte rawdata;
    int id;
    String signcontent[];
}

