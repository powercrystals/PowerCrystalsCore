package powercrystals.core.gui.controls;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;

public abstract class ButtonOption extends Button
{
	private Map<Integer, String> _values = new HashMap<Integer, String>();
	private int _currentValue = 0;
	
	public ButtonOption(GuiContainer containerScreen, int x, int y, int width, int height)
	{
		super(containerScreen, x, y, width, height, "");
	}
	
	public void setValue(int value, String label)
	{
		_values.put(value, label);
	}

	@Override
	public void onClick()
	{
		_currentValue++;
		if(_currentValue > _values.size())
		{
			_currentValue = 0;
		}
		setText(_values.get(_currentValue));
		onValueChanged(_currentValue, _values.get(_currentValue));
	}
	
	public abstract void onValueChanged(int value, String label);
}
