package coolsquid.misctweaks.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

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
			list.add(createElement("game_options"));
			list.add(createElement("world"));
			list.add(createElement("hunger"));
			list.add(createElement("client"));
			list.add(createElement("miscellaneous"));
			return list;
		}

		private static IConfigElement createElement(String category) {
			return new ConfigElement(ConfigManager.CONFIG.getCategory(category));
		}
	}
}