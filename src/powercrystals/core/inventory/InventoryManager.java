package powercrystals.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
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
}
