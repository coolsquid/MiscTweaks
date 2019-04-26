package coolsquid.misctweaks.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("coolsquid.misctweaks.asm")
public class MiscTweaksPlugin implements IFMLLoadingPlugin {

	static boolean fireTickRateHook = true, fireSourceHook = true, chestSizeHook = true, enderChestSizeHook = true, minecartChestSizeHook = true;

	@Override
	public String[] getASMTransformerClass() {
		Properties properties = new Properties();
		File propertiesFile = new File("config/MiscTweaks_ASM.properties");
		if (propertiesFile.exists()) {
			try {
				properties.load(new FileInputStream(propertiesFile));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		fireTickRateHook = Boolean.parseBoolean(properties.getProperty("fireTickRateHook", "true"));
		fireSourceHook = Boolean.parseBoolean(properties.getProperty("fireSourceHook", "true"));
		chestSizeHook = Boolean.parseBoolean(properties.getProperty("chestSizeHook", "true"));
		enderChestSizeHook = Boolean.parseBoolean(properties.getProperty("enderChestSizeHook", "true"));
		minecartChestSizeHook = Boolean.parseBoolean(properties.getProperty("minecartChestSizeHook", "true"));
		return new String[] { Transformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
