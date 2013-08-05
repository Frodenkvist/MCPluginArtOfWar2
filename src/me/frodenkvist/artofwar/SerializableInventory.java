// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SerializableInventory.java

package me.frodenkvist.artofwar;

import java.io.Serializable;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class SerializableInventory
    implements Serializable
{

    public SerializableInventory(Inventory i)
    {
        id = new int[i.getSize()];
        data = new byte[i.getSize()];
        amount = new int[i.getSize()];
        for(int x = 0; x < i.getSize(); x++)
            if(i.getContents()[x] != null)
            {
                id[x] = i.getContents()[x].getTypeId();
                data[x] = i.getContents()[x].getData().getData();
                amount[x] = i.getContents()[x].getAmount();
            }

    }

    public ItemStack[] convertToContent()
    {
        ItemStack stacks[] = new ItemStack[id.length];
        for(int x = 0; x < stacks.length; x++)
        {
            MaterialData md = new MaterialData(id[x], data[x]);
            stacks[x] = md.toItemStack();
            stacks[x].setAmount(amount[x]);
        }

        return stacks;
    }

    private static final long serialVersionUID = 1L;
    int id[];
    int amount[];
    byte data[];
}
