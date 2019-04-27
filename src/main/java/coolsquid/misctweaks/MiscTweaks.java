package coolsquid.misctweaks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import coolsquid.misctweaks.config.ConfigManager;
import coolsquid.misctweaks.util.BrandingTweaks;
import coolsquid.misctweaks.util.ClientEventHandler;
import coolsquid.misctweaks.util.ModEventHandler;
import coolsquid.misctweaks.util.OptionTweaks;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = MiscTweaks.MODID, name = MiscTweaks.NAME, version = MiscTweaks.VERSION, dependencies = MiscTweaks.DEPENDENCIES, updateJSON = MiscTweaks.UPDATE_JSON, acceptableRemoteVersions = "*", guiFactory = "coolsquid.misctweaks.config.ConfigGuiFactory", acceptedMinecraftVersions = "[1.12,1.13)")
public class MiscTweaks {

	public static final String MODID = "misctweaks";
	public static final String NAME = "MiscTweaks";
	public static final String VERSION = "1.4.0";
	public static final String DEPENDENCIES = "required-after:forge@[14.21.1.2387,);after:applecore";
	public static final String UPDATE_JSON = "https://gist.githubusercontent.com/coolsquid/15f17615b4a2d493ec38399c4ec6489f/raw/e493499854250efee3f2adab276a1e15d8cd9f7d/misctweaks.json";

	@EventHandler
	public void init(FMLInitializationEvent event) {
		BrandingTweaks.oldBrandings = FMLCommonHandler.instance().getBrandings(true);
		BrandingTweaks.oldBrandingsNoMc = FMLCommonHandler.instance().getBrandings(false);
		applyTweaks();

		MinecraftForge.EVENT_BUS.register(new OptionTweaks.Listener());
		Object handler = new ModEventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		MinecraftForge.TERRAIN_GEN_BUS.register(handler);
	}

	@SideOnly(Side.CLIENT)
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigManager.loadConfig();
	}

	@SideOnly(Side.SERVER)
	@EventHandler
	public void serverPreInit(FMLPreInitializationEvent event) {
		ConfigManager.loadConfig();
		File settingsFile = new File("server.properties");
		if (!settingsFile.exists()) {
			Properties properties = new Properties();
			if (!ConfigManager.defaultWorldType.isEmpty()) {
				properties.setProperty("level-type", WorldType.parseWorldType(ConfigManager.defaultWorldType).getName().toUpperCase());
			}
			if (!ConfigManager.defaultChunkProviderSettings.isEmpty()) {
				properties.setProperty("generator-settings", ConfigManager.defaultChunkProviderSettings);
			}
			if (ConfigManager.generateStructures != -1) {
				properties.setProperty("generate-structures", String.valueOf(ConfigManager.generateStructures == 1));
			}
			if (!ConfigManager.defaultDifficulty.isEmpty()) {
				properties.setProperty("difficulty", String.valueOf(EnumDifficulty.valueOf(ConfigManager.defaultDifficulty.toUpperCase()).ordinal()));
			}
			if (!ConfigManager.defaultGamemode.isEmpty()) {
				if (ConfigManager.defaultGamemode.equals("hardcore")) {
					properties.setProperty("gamemode", "0");
					properties.setProperty("hardcore", "true");
				}
				else {
					properties.setProperty("gamemode", String.valueOf(GameType.valueOf(ConfigManager.defaultGamemode.toUpperCase()).getID()));
				}
			}
			if (!ConfigManager.defaultSeed.isEmpty()) {
				properties.setProperty("level-seed", ConfigManager.defaultSeed);
			}
			try {
				properties.store(new FileWriter(settingsFile), "");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Applies tweaks that have to be reapplied whenever their settings have
	 * been changed.
	 */
	public static void applyTweaks() {
		BrandingTweaks.updateBranding();
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			if (ConfigManager.disabledOverlays.isEmpty()) {
				MinecraftForge.EVENT_BUS.unregister(ClientEventHandler.INSTANCE);
			} else {
				MinecraftForge.EVENT_BUS.register(ClientEventHandler.INSTANCE);
			}
			// unregisters itself for performance, must be reregistered if configs change
			MinecraftForge.EVENT_BUS.register(new OptionTweaks.SettingsListener());
		}
		if (!ConfigManager.allowedWorldTypes.isEmpty()) {
			for (WorldType type : WorldType.WORLD_TYPES) {
				if (type != null) {
					try {
						Method m = WorldType.class.getDeclaredMethod("setCanBeCreated", boolean.class);
						m.setAccessible(true);
						m.invoke(type, ConfigManager.allowedWorldTypes.contains(type.getName().toUpperCase()));
					} catch (ReflectiveOperationException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
}
