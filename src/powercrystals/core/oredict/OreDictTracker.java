package powercrystals.core.oredict;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;

public final class OreDictTracker
{
	private static Map<ItemIdentifier, List<String>> _oreDictEntries = new HashMap<ItemIdentifier, List<String>>();
	
	public static void registerOreDictEntry(ItemStack stack, String name)
	{
		ItemIdentifier ii = ItemIdentifier.fromItemStack(stack);
		if(_oreDictEntries.get(ii) == null)
		{
			_oreDictEntries.put(ii, new LinkedList<String>());
		}
		_oreDictEntries.get(ii).add(name);
	}
	
	public static List<String> getNamesFromItem(ItemStack stack)
	{
		for(Entry<ItemIdentifier, List<String>> e : _oreDictEntries.entrySet())
		{
			if(e.getKey().itemId == stack.itemID && e.getKey().itemMeta == stack.getItemDamage())
			{
				return e.getValue();
			}
		}
		return null;
	}
}
