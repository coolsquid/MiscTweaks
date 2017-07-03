package coolsquid.misctweaks;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import coolsquid.misctweaks.config.ConfigManager;
import coolsquid.misctweaks.util.BrandingTweaks;
import coolsquid.misctweaks.util.ModEventHandler;

@Mod(modid = MiscTweaks.MODID, name = MiscTweaks.NAME, version = MiscTweaks.VERSION, dependencies = MiscTweaks.DEPENDENCIES, updateJSON = MiscTweaks.UPDATE_JSON, acceptableRemoteVersions = "*", guiFactory = "coolsquid.misctweaks.config.ConfigGuiFactory")
public class MiscTweaks {

	public static final String MODID = "misctweaks";
	public static final String NAME = "MiscTweaks";
	public static final String VERSION = "1.0.9";
	public static final String DEPENDENCIES = "required-after:forge@[14.21.1.2387,);after:AppleCore";
	public static final String UPDATE_JSON = "https://coolsquid.me/api/version/misctweaks.json";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		File squidDir = new File("./config/coolsquid");
		if (squidDir.exists() && squidDir.list().length == 0) {
			squidDir.delete();
		}
		ConfigManager.loadConfig();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		BrandingTweaks.oldBrandings = FMLCommonHandler.instance().getBrandings(true);
		BrandingTweaks.oldBrandingsNoMc = FMLCommonHandler.instance().getBrandings(false);
		applyTweaks();

		Object handler = new ModEventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		MinecraftForge.TERRAIN_GEN_BUS.register(handler);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) throws IOException {

	}

	public static void applyTweaks() {
		BrandingTweaks.updateBranding();
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT && ConfigManager.maxGamma < 1) {
			GameSettings.Options.GAMMA.setValueMax(ConfigManager.maxGamma);
			if (ConfigManager.maxGamma < Minecraft.getMinecraft().gameSettings.gammaSetting) {
				Minecraft.getMinecraft().gameSettings.setOptionFloatValue(Options.GAMMA, ConfigManager.maxGamma);
			}
		}
	}
}
