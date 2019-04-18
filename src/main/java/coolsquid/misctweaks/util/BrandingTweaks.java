package coolsquid.misctweaks.util;

import java.util.List;

import com.google.common.collect.Lists;

import coolsquid.misctweaks.config.ConfigManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class BrandingTweaks {

	public static List<String> oldBrandings, oldBrandingsNoMc;

	public static void updateBranding() {
		List<String> brandings = Lists.newArrayList();
		List<String> brandingsNoMC = Lists.newArrayList();
		if (ConfigManager.retainOldBranding) {
			brandings.addAll(oldBrandings);
			brandingsNoMC.addAll(oldBrandingsNoMc);
		}
		if (ConfigManager.branding.length != 0) {
			for (String branding : ConfigManager.branding) {
				brandings.add(branding);
			}
		}
		ReflectionHelper.setPrivateValue(FMLCommonHandler.class, FMLCommonHandler.instance(), brandings, "brandings");
		ReflectionHelper.setPrivateValue(FMLCommonHandler.class, FMLCommonHandler.instance(), brandingsNoMC,
				"brandingsNoMC");
	}
}