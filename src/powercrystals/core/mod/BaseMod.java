package powercrystals.core.mod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import powercrystals.core.updater.IUpdateableMod;

import cpw.mods.fml.common.registry.LanguageRegistry;

public abstract class BaseMod implements IUpdateableMod
{
	protected File _configFolder;
	
	protected void setConfigFolderBase(File folder)
	{
		_configFolder = new File(folder.getAbsolutePath() + "/" + getConfigBaseFolder() + "/" + getModId().toLowerCase() + "/");
	}
	
	protected File getClientConfig()
	{
		return new File(_configFolder.getAbsolutePath() + "/client.cfg");
	}
	
	protected File getCommonConfig()
	{
		return new File(_configFolder.getAbsolutePath() + "/common.cfg");
	}
	
	protected String getConfigBaseFolder()
	{
		return "powercrystals";
	}
	
	protected void extractLang(String[] languages)
	{
		String langResourceBase = "/" + getConfigBaseFolder() + "/" + getModId().toLowerCase() + "/lang/";
		for(String lang : languages)
		{
			InputStream is = this.getClass().getResourceAsStream(langResourceBase + lang + ".lang");
			try
			{
				OutputStream os = new FileOutputStream(_configFolder.getAbsolutePath() + "/" + lang + ".lang");
				byte[] buffer = new byte[1024];
				int read = 0;
				while((read = is.read(buffer)) != -1)
				{
					os.write(buffer, 0, read);
				}
				is.close();
				os.flush();
				os.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	protected void loadLang()
	{
		for(File langFile : _configFolder.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return name.endsWith(".lang");
			}
		}))
		{
			try
			{
				Properties langPack = new Properties();
				langPack.load(new FileInputStream(langFile));
				String lang = langFile.getName().replace(".lang", "");
				LanguageRegistry.instance().addStringLocalization(langPack, lang);
			}
			catch(FileNotFoundException x)
			{
				x.printStackTrace();
			}
			catch(IOException x)
			{
				x.printStackTrace();
			}
		}
	}
}
