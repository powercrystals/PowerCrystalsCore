package powercrystals.core;

import powercrystals.core.updater.IUpdateableMod;
import powercrystals.core.updater.UpdateManager;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = CoreCore.modId, name = CoreCore.modName, version = CoreCore.version)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class CoreCore implements IUpdateableMod
{
	public static final String version = "1.4.6R1.0.0";
	public static final String modId = "PowerCrystalsCore";
	public static final String modName = "PowerCrystals Core";
	
	@Init
	public void load(FMLInitializationEvent evt)
	{
		TickRegistry.registerScheduledTickHandler(new UpdateManager(this), Side.CLIENT);
	}
	
	@ServerStarted
	public void serverStarted(FMLServerStartedEvent event)
	{
	}

	@Override
	public String getModId()
	{
		return modId;
	}

	@Override
	public String getModName()
	{
		return modName;
	}

	@Override
	public String getModFolder()
	{
		return modId;
	}

	@Override
	public String getModVersion()
	{
		return version;
	}
}
