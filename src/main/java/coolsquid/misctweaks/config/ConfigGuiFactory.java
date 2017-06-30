package coolsquid.misctweaks.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.Loader;

import coolsquid.misctweaks.MiscTweaks;

public class ConfigGuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public boolean hasConfigGui() {
		return ConfigManager.enableConfigGui;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new Gui(parentScreen);
	}

	public static class Gui extends GuiConfig {

		public Gui(GuiScreen parent) {
			super(parent, getConfigElements(), MiscTweaks.MODID, MiscTweaks.MODID, false, false,
					MiscTweaks.NAME + " configuration", ConfigManager.CONFIG.getConfigFile().getAbsolutePath());
		}

		private static List<IConfigElement> getConfigElements() {
			List<IConfigElement> list = new ArrayList<>();
			list.add(createElement("game_options", "Game Options", "misctweaks.config.game_options"));
			list.add(createElement("world", "World", "misctweaks.config.world"));
			if (Loader.isModLoaded("AppleCore")) {
				list.add(createElement("hunger", "Hunger", "misctweaks.config.hunger"));
			}
			list.add(createElement("miscellaneous", "Miscellaneous", "misctweaks.config.miscellaneous"));
			return list;
		}

		private static IConfigElement createElement(String category, String name, String lang_key) {
			return new DummyConfigElement.DummyCategoryElement(name, lang_key,
					new ConfigElement(ConfigManager.CONFIG.getCategory(category)).getChildElements());
		}
	}
}