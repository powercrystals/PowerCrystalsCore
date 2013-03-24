package powercrystals.core.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import powercrystals.core.position.BlockPosition;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.transport.IPipeEntry;

public class UtilInventory
{
	public static List<ForgeDirection> findPipes(World world, int x, int y, int z)
	{
		List<ForgeDirection> pipes = new LinkedList<ForgeDirection>();
		BlockPosition ourpos = new BlockPosition(x, y, z);
		for(ForgeDirection o : ForgeDirection.values())
		{
			BlockPosition bp = new BlockPosition(ourpos);
			bp.orientation = o;
			bp.moveForwards(1);
			TileEntity te = world.getBlockTileEntity(bp.x, bp.y, bp.z);
			if(te instanceof IPipeEntry)
			{
				pipes.add(o);
			}
		}
		
		return pipes;
	}
	
	public static Map<ForgeDirection, IInventory> findChests(World world, int x, int y, int z)
	{
		HashMap<ForgeDirection, IInventory> chests = new HashMap<ForgeDirection, IInventory>();
		
		for(ForgeDirection d : ForgeDirection.values())
		{
			if(d == ForgeDirection.UNKNOWN)
			{
				continue;
			}
			BlockPosition bp = new BlockPosition(x, y, z);
			bp.orientation = d;
			bp.moveForwards(1);
			TileEntity te = world.getBlockTileEntity(bp.x, bp.y, bp.z);
			if(te != null && te instanceof IInventory)
			{
				chests.put(d, checkForDoubleChest(world, te, bp));
			}
		}
		return chests;
	}
	
	private static IInventory checkForDoubleChest(World world, TileEntity te, BlockPosition chestloc)
	{
		if(world.getBlockId(chestloc.x, chestloc.y, chestloc.z) == Block.chest.blockID)
		{
			for(BlockPosition bp : chestloc.getAdjacent(false))
			{
				if(world.getBlockId(bp.x, bp.y, bp.z) == Block.chest.blockID)
				{
					return new InventoryLargeChest("Large Chest", ((IInventory)te), ((IInventory)world.getBlockTileEntity(bp.x, bp.y, bp.z)));
				}
			}
		}
		return ((IInventory)te);
	}
}
