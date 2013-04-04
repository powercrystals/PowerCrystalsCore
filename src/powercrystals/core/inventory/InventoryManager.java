package powercrystals.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

public class InventoryManager
{
	@SuppressWarnings("deprecation")
	public static IInventoryManager create(IInventory inventory, ForgeDirection targetSide)
	{
		if(inventory instanceof ISidedInventory)
		{
			return new InventoryManagerSided((ISidedInventory)inventory, targetSide);
		}
		else if(inventory instanceof net.minecraftforge.common.ISidedInventory)
		{
			return new InventoryManagerLegacy((net.minecraftforge.common.ISidedInventory)inventory, targetSide);
		}
		else if(inventory instanceof IInventory)
		{
			return new InventoryManagerStandard(inventory, targetSide);
		}
		else
		{
			return null;
		}
	}
	
	public static boolean stacksEqual(ItemStack s1, ItemStack s2)
	{
		return stacksEqual(s1, s2, true);
	}
	
	public static boolean stacksEqual(ItemStack s1, ItemStack s2, boolean nbtSensitive)
	{
		if(s1 == null || s2 == null) return false;
		if(!s1.isItemEqual(s2)) return false;
		
		if(nbtSensitive)
		{
			if(s1.getTagCompound() == null && s2.getTagCompound() == null) return true;
			if(s1.getTagCompound() == null || s2.getTagCompound() == null) return false;
			return s1.getTagCompound().equals(s2.getTagCompound());
		}
		
		return true;
	}
}
