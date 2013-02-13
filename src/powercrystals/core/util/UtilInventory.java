package powercrystals.core.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import powercrystals.core.position.BlockPosition;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import buildcraft.api.transport.IPipeEntry;

public class UtilInventory
{
	public static int addToInventory(IInventory targetInventory, ForgeDirection toSide, ItemStack stackToAdd)
	{
		int amountLeftToAdd = stackToAdd.stackSize;
		int stackSizeLimit;

		stackSizeLimit = Math.min(targetInventory.getInventoryStackLimit(), stackToAdd.getMaxStackSize());
		
		int slotIndex;
		
		while(amountLeftToAdd > 0)
		{
			slotIndex = getAvailableSlot(targetInventory, toSide, stackToAdd);
			if(slotIndex < 0)
			{
				break;
			}
			ItemStack targetStack = targetInventory.getStackInSlot(slotIndex);
			if(targetStack == null)
			{
				if(stackToAdd.stackSize <= stackSizeLimit)
				{
					ItemStack s = stackToAdd.copy();
					s.stackSize = amountLeftToAdd;
					targetInventory.setInventorySlotContents(slotIndex, s);
					amountLeftToAdd = 0;
					break;
				}
				else
				{
					ItemStack s = stackToAdd.copy();
					s.stackSize = stackSizeLimit;
					targetInventory.setInventorySlotContents(slotIndex, stackToAdd.copy());
					amountLeftToAdd -= s.stackSize;
				}
			}
			else
			{
				int amountToAdd = Math.min(amountLeftToAdd, stackSizeLimit - targetStack.stackSize);
				targetStack.stackSize += amountToAdd;
				amountLeftToAdd -= amountToAdd;
			}
		}
		
		return amountLeftToAdd;
	}
	
	private static int getAvailableSlot(IInventory inventory, ForgeDirection toSide, ItemStack stack)
	{
		int stackSizeLimit;

		stackSizeLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
		
		int invStart = 0;
		int invEnd = inventory.getSizeInventory();
		
		if(toSide != ForgeDirection.UNKNOWN && inventory instanceof ISidedInventory)
		{
			invStart = ((ISidedInventory)inventory).getStartInventorySide(toSide.getOpposite());
			invEnd = invStart + ((ISidedInventory)inventory).getSizeInventorySide(toSide.getOpposite());
		}
		
		for(int i = invStart; i < invEnd; i++)
		{
			ItemStack targetStack = inventory.getStackInSlot(i);
			if(targetStack == null)
			{
				return i;
			}
			if(targetStack.itemID == stack.itemID && targetStack.getItemDamage() == stack.getItemDamage() && targetStack.stackSize < stackSizeLimit)
			{
				return i;
			}
		}
		
		return -1;
	}

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


	
	public static int findFirstStack(IInventory inv, int itemId, int itemDamage)
	{
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack s = inv.getStackInSlot(i);
			if(s != null && s.itemID == itemId && s.getItemDamage() == itemDamage)
			{
				return i;
			}
		}
		return -1;
	}
}
