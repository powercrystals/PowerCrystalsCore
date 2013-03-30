package powercrystals.core;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion(value = "1.5.1")
public class CoreLoader implements IFMLLoadingPlugin
{
	@Override
	public String[] getLibraryRequestClass()
	{
		return null;
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] { "powercrystals.core.at.PCCAccessTransformer" };
	}

	@Override
	public String getModContainerClass()
	{
		return "powercrystals.core.CoreCore";
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
	}
}
