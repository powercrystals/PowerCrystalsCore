package powercrystals.core;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import powercrystals.core.updater.IUpdateableMod;
import powercrystals.core.updater.UpdateManager;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = CoreCore.modId, name = CoreCore.modName, version = CoreCore.version)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class CoreCore implements IUpdateableMod
{
	public static final String version = "1.4.6R1.0.2B1";
	public static final String modId = "PowerCrystalsCore";
	public static final String modName = "PowerCrystals Core";
	
	public static Property doUpdateCheck;

	@PreInit
	public void preInit(FMLPreInitializationEvent evt)
	{
		loadConfig(evt.getSuggestedConfigurationFile());
	}
	
	@Init
	public void load(FMLInitializationEvent evt)
	{
		TickRegistry.registerScheduledTickHandler(new UpdateManager(this), Side.CLIENT);
	}
	
	private void loadConfig(File f)
	{
		Configuration c = new Configuration(f);
		c.load();
		
		doUpdateCheck = c.get(Configuration.CATEGORY_GENERAL, "EnableUpdateCheck", true);
		doUpdateCheck.comment = "Set to false to disable update checks for all Power Crystals' mods.";
		
		c.save();
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
