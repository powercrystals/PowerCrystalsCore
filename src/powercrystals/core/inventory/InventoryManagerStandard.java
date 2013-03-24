package powercrystals.core.inventory;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

public class InventoryManagerStandard implements IInventoryManager
{
	private IInventory _inv;
	protected ForgeDirection _targetSide;
	
	public InventoryManagerStandard(IInventory inventory, ForgeDirection targetSide)
	{
		_inv = inventory;
		_targetSide = targetSide;
	}
	
	protected boolean canAddItem(ItemStack stack, int slot)
	{
		return _inv.isStackValidForSlot(slot, stack);
	}
	
	protected boolean canRemoveItem(ItemStack stack, int slot)
	{
		return true;
	}
	
	@Override
	public ItemStack addItem(ItemStack stack)
	{
		if(stack == null)
		{
			return null;
		}
		
		int quantitytoadd = stack.stackSize;
		ItemStack remaining = stack.copy();
		for(int i : getSlots())
		{
			ItemStack s = getSlotContents(i);
			if(s == null)
			{
				ItemStack add = stack.copy();
				add.stackSize = Math.min(quantitytoadd, _inv.getInventoryStackLimit());
				
				if(canAddItem(add, i))
				{
					_inv.setInventorySlotContents(i, add);
					quantitytoadd -= add.stackSize;
				}
			}
			else if(InventoryManager.stacksEqual(s, stack))
			{
				ItemStack add = stack.copy();
				add.stackSize = Math.min(quantitytoadd, _inv.getInventoryStackLimit() - s.stackSize);
				
				if(add.stackSize > 0 && canAddItem(add, i))
				{
					s.stackSize += add.stackSize;
					_inv.setInventorySlotContents(i, add);
					quantitytoadd -= add.stackSize;
				}
			}
		}
		
		remaining.stackSize = quantitytoadd;
		if(remaining.stackSize == 0)
		{
			return null;
		}
		else
		{
			return remaining;
		}
	}

	@Override
	public ItemStack removeItem(int maxRemove)
	{
		for(int i : getSlots())
		{
			ItemStack s = getSlotContents(i);
			if(s != null && canRemoveItem(s, i))
			{
				int toRemove = Math.min(s.stackSize, maxRemove);
				s.stackSize -= toRemove;
				ItemStack removed = s.copy();
				removed.stackSize = toRemove;
				_inv.setInventorySlotContents(i, s);
				return removed;
			}
		}
		return null;
	}

	@Override
	public ItemStack removeItem(int maxRemove, ItemStack type)
	{
		for(int i : getSlots())
		{
			ItemStack s = getSlotContents(i);
			if(InventoryManager.stacksEqual(s, type) && canRemoveItem(s, i))
			{
				int toRemove = Math.min(s.stackSize, maxRemove);
				s.stackSize -= toRemove;
				ItemStack removed = s.copy();
				removed.stackSize = toRemove;
				_inv.setInventorySlotContents(i, s);
				return removed;
			}
		}
		return null;
	}

	@Override
	public ItemStack getSlotContents(int slot)
	{
		return _inv.getStackInSlot(slot);
	}

	@Override
	public int hasItem(ItemStack type)
	{
		int quantity = 0;
		for(ItemStack s : getContents().values())
		{
			if(InventoryManager.stacksEqual(s, type))
			{
				quantity += s.stackSize;
			}
		}
		return quantity;
	}

	@Override
	public int findItem(ItemStack type)
	{
		int quantity = 0;
		for(int i : getSlots())
		{
			ItemStack s = _inv.getStackInSlot(i);
			if(InventoryManager.stacksEqual(s, type))
			{
				return i;
			}
		}
		return quantity;
	}

	@Override
	public int[] getSlots()
	{
		int[] slots = new int[_inv.getSizeInventory()];
		for(int i = 0; i < slots.length; i++)
		{
			slots[i] = i;
		}
		return slots;
	}

	@Override
	public Map<Integer, ItemStack> getContents()
	{
		Map<Integer, ItemStack> contents = new HashMap<Integer, ItemStack>();
		for(int i : getSlots())
		{
			contents.put(i, _inv.getStackInSlot(i));
		}
		return contents;
	}
}
