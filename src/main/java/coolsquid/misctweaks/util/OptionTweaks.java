package coolsquid.misctweaks.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiOptionsRowList;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import coolsquid.misctweaks.config.ConfigManager;

public class OptionTweaks {

	public static void updateGuiWorld(GuiCreateWorld gui) {
		if (!ConfigManager.forcedGamemode.isEmpty()) {
			if (ConfigManager.forcedGamemode.equalsIgnoreCase("hardcore")) {
				gui.btnGameMode.displayString = I18n.format("selectWorld.gameMode") + ": "
						+ I18n.format("selectWorld.gameMode.hardcore");
				gui.hardCoreMode = true;
				gui.gameMode = "survival";
				gui.savedGameMode = gui.gameMode;
			} else {
				gui.btnGameMode.displayString = I18n.format("selectWorld.gameMode") + ": "
						+ I18n.format("selectWorld.gameMode." + ConfigManager.forcedGamemode.toLowerCase());
				gui.gameMode = ConfigManager.forcedGamemode.toLowerCase();
				gui.savedGameMode = gui.gameMode;
			}
			gui.btnGameMode.enabled = false;
		}
		if (ConfigManager.disableCheats) {
			gui.allowCheats = false;
			gui.allowCheatsWasSetByUser = true;
			gui.btnAllowCommands.enabled = false;
			gui.btnAllowCommands.displayString = I18n.format("selectWorld.allowCommands", new Object[0]) + ' '
					+ I18n.format("options.off", new Object[0]);
		}
		if (ConfigManager.disableBonusChest) {
			gui.bonusChestEnabled = false;
			gui.btnBonusItems.enabled = false;
			gui.btnBonusItems.displayString = I18n.format("selectWorld.bonusItems", new Object[0]) + ' '
					+ I18n.format("options.off", new Object[0]);
		}
		if (ConfigManager.forcedWorldType != -1) {
			gui.selectedIndex = ConfigManager.forcedWorldType;
			gui.btnMapType.enabled = false;
			gui.btnMapType.displayString = I18n.format("selectWorld.mapType") + " "
					+ I18n.format(WorldType.WORLD_TYPES[ConfigManager.forcedWorldType].getTranslateName());
			gui.btnCustomizeType.enabled = false;
			gui.btnCustomizeType.displayString = I18n.format("selectWorld.customizeType");
		}
	}

	private static void setOptions() {
		if (ConfigManager.maxGamma < 1) {
			GameSettings.Options.GAMMA.setValueMax(ConfigManager.maxGamma);
			if (ConfigManager.maxGamma < Minecraft.getMinecraft().gameSettings.gammaSetting) {
				Minecraft.getMinecraft().gameSettings.setOptionFloatValue(Options.GAMMA, ConfigManager.maxGamma);
			}
		} else {
			GameSettings.Options.GAMMA.setValueMax(1);
		}
		if (ConfigManager.maxRenderDistance < 32) {
			GameSettings.Options.RENDER_DISTANCE.setValueMax(ConfigManager.maxRenderDistance);
			if (ConfigManager.maxRenderDistance < Minecraft.getMinecraft().gameSettings.renderDistanceChunks) {
				Minecraft.getMinecraft().gameSettings.setOptionValue(Options.RENDER_DISTANCE,
						ConfigManager.maxRenderDistance);
			}
		} else {
			GameSettings.Options.RENDER_DISTANCE.setValueMax(32);
		}
		Minecraft.getMinecraft().gameSettings.saveOptions();
	}

	public static class SettingsListener {

		// must be done when the main menu opens, as saving options before that breaks new key bindings
		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onGuiOpen(GuiOpenEvent event) {
			if (event.getGui() instanceof GuiMainMenu) {
				setOptions();
				MinecraftForge.EVENT_BUS.unregister(this);
			}
		}
	}

	public static class Listener {

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
			if (event.getGui() instanceof GuiCreateWorld) {
				OptionTweaks.updateGuiWorld((GuiCreateWorld) event.getGui());
			} else if (event.getGui() instanceof GuiOptions && !ConfigManager.forcedDifficulty.isEmpty()) {
				GuiButton b = ((GuiOptions) event.getGui()).difficultyButton;
				if (b != null) {
					b.enabled = false;
					b.displayString = I18n.format("options.difficulty", new Object[0]) + ": " + I18n.format(
							"options.difficulty." + ConfigManager.forcedDifficulty.toLowerCase(), new Object[0]);
				}
			} else if (event.getGui() instanceof GuiMainMenu) {
				if (ConfigManager.removeRealmsButton) {
					((GuiMainMenu) event.getGui()).realmsButton.visible = false;
					((GuiMainMenu) event.getGui()).realmsNotification = null;
					ReflectionHelper.setPrivateValue(GuiButton.class, ReflectionHelper.getPrivateValue(
							GuiMainMenu.class, (GuiMainMenu) event.getGui(), "modButton"), 200, "width");
				}
				/*if (ConfigManager.removeCopyrightText) {
					((GuiMainMenu) event.getGui()).widthCopyright = 0;
					((GuiMainMenu) event.getGui()).widthCopyrightRest = event.getGui().width + 1;
				}*/
			} else if (event.getGui() instanceof GuiVideoSettings && ConfigManager.maxGamma < 1) {
				GuiOptionsRowList list = ReflectionHelper.getPrivateValue(GuiVideoSettings.class,
						(GuiVideoSettings) event.getGui(), 3);
				if (list != null) {
					GameSettings.Options[] options = ReflectionHelper.getPrivateValue(GuiVideoSettings.class, null, 4);
					for (int i = 0; i < options.length; i++) {
						if (options[i] == Options.GAMMA) {
							boolean isEven = (float) i % 2 == 0;
							GuiOptionsRowList.Row row = list.getListEntry(isEven ? i / 2 : (i - 1) / 2);
							GuiOptionSlider slider = ReflectionHelper.getPrivateValue(GuiOptionsRowList.Row.class, row,
									isEven ? 1 : 2);
							ReflectionHelper.setPrivateValue(GuiOptionsRowList.Row.class, row,
									new GammaSlider(slider.id, slider.xPosition, slider.yPosition), isEven ? 1 : 2);
						}
					}
				}
			}
		}
	}
}