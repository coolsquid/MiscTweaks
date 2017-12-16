package coolsquid.misctweaks;

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
import coolsquid.misctweaks.util.ClientEventHandler;
import coolsquid.misctweaks.util.ModEventHandler;
import coolsquid.misctweaks.util.OptionTweaks;

@Mod(modid = MiscTweaks.MODID, name = MiscTweaks.NAME, version = MiscTweaks.VERSION, dependencies = MiscTweaks.DEPENDENCIES, updateJSON = MiscTweaks.UPDATE_JSON, acceptableRemoteVersions = "*", guiFactory = "coolsquid.misctweaks.config.ConfigGuiFactory")
public class MiscTweaks {

	public static final String MODID = "misctweaks";
	public static final String NAME = "MiscTweaks";
	public static final String VERSION = "1.1.5";
	public static final String DEPENDENCIES = "required-after:forge@[14.21.1.2387,);after:AppleCore";
	public static final String UPDATE_JSON = "https://coolsquid.me/api/version/misctweaks.json";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigManager.loadConfig();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		BrandingTweaks.oldBrandings = FMLCommonHandler.instance().getBrandings(true);
		BrandingTweaks.oldBrandingsNoMc = FMLCommonHandler.instance().getBrandings(false);
		applyTweaks();

		MinecraftForge.EVENT_BUS.register(new OptionTweaks.Listener());
		Object handler = new ModEventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		MinecraftForge.TERRAIN_GEN_BUS.register(handler);

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	/**
	 * Applies tweaks that have to be reapplied whenever their settings have
	 * been changed.
	 */
	public static void applyTweaks() {
		BrandingTweaks.updateBranding();
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			// unregisters itself for performance, must be reregistered if configs change
			MinecraftForge.EVENT_BUS.register(new OptionTweaks.SettingsListener());
		}
	}
}
