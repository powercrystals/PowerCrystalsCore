package powercrystals.core.inventory;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

@SuppressWarnings("deprecation")
public class InventoryManagerLegacy extends InventoryManagerStandard
{
	private ISidedInventory _sidedInv;
	
	public InventoryManagerLegacy(ISidedInventory inventory, ForgeDirection targetSide)
	{
		super(inventory, targetSide);
		_sidedInv = inventory;
	}
	
	@Override
	public int[] getSlots()
	{
		int start = _sidedInv.getStartInventorySide(_targetSide);
		int size = _sidedInv.getSizeInventorySide(_targetSide);
		int[] sides = new int[size];
		for(int i = start; i < start + size; i++)
		{
			sides[i - start] = i;
		}
		return sides;
	}
}
