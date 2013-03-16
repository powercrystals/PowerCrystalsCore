
package powercrystals.core.updater;

import java.util.EnumSet;

import powercrystals.core.CoreCore;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class UpdateManager implements IScheduledTickHandler
{
	private boolean _notificationDisplayed;
	private IUpdateableMod _mod;
	private UpdateCheckThread _updateThread;
	
	public UpdateManager(IUpdateableMod mod)
	{
		this(mod, null);
	}
	
	public UpdateManager(IUpdateableMod mod, String releaseUrl)
	{
		_mod = mod;
		_updateThread = new UpdateCheckThread(mod, releaseUrl);
		if(CoreCore.doUpdateCheck.getBoolean(true))
		{
			_updateThread.start();
		}
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		if(!_notificationDisplayed && _updateThread.checkComplete())
		{
			_notificationDisplayed = true;
			if(_updateThread.newVersionAvailable())
			{
				EntityPlayer player = (EntityPlayer) tickData[0];
				player.sendChatToPlayer("[" + _mod.getModName() + "] A new version is available: " + _updateThread.newVersion().modVersion().toString());
				player.sendChatToPlayer( _updateThread.newVersion().description());
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{

	}

	@Override
	public EnumSet<TickType> ticks()
	{
		if (_notificationDisplayed)
		{
			return EnumSet.noneOf(TickType.class);
		}
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel()
	{
		return _mod.getModId() + ".version";
	}

	@Override
	public int nextTickSpacing()
	{
		if(!_notificationDisplayed)
		{
			return 400;
		}
		return 72000;
	}
}