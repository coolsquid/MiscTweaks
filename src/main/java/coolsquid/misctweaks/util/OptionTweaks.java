package coolsquid.misctweaks.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;

import coolsquid.misctweaks.MiscTweaks;
import coolsquid.misctweaks.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiOptionsRowList;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OptionTweaks {

	public static void updateGuiWorld(GuiCreateWorld gui) {
		if (!ConfigManager.defaultGamemode.isEmpty() && ConfigManager.forceGamemode) {
			gui.btnGameMode.enabled = false;
		}
		if (ConfigManager.cheats != -1 && ConfigManager.forceDefaultCheatsOption) {
			gui.btnAllowCommands.enabled = false;
		}
		if (ConfigManager.bonusChest != -1 && ConfigManager.forceDefaultBonusChestOption) {
			gui.btnBonusItems.enabled = false;
		}
		if (ConfigManager.generateStructures != -1 && ConfigManager.forceDefaultGenerateStructuresOption) {
			gui.btnMapFeatures.enabled = false;
		}
		if (ConfigManager.defaultWorldType != -1 && ConfigManager.forceWorldType) {
			gui.btnMapType.enabled = false;
			gui.btnCustomizeType.enabled = false;
		}
		if (!ConfigManager.defaultChunkProviderSettings.isEmpty() && ConfigManager.forceChunkProviderSettings) {
			gui.btnCustomizeType.enabled = false;
		}
		if (!ConfigManager.defaultSeed.isEmpty() && ConfigManager.forceSeed) {
			gui.worldSeedField.setEnabled(false);
		}
		try {
			Method m = GuiCreateWorld.class.getDeclaredMethod("updateDisplayState");
			m.setAccessible(true);
			m.invoke(gui);
		} catch (ReflectiveOperationException e) {
			LogManager.getLogger(MiscTweaks.NAME).catching(e);
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
		public void onGuiInit(GuiScreenEvent.ActionPerformedEvent.Post event) {
			if (event.getGui() instanceof GuiCreateWorld && event.getButton().id == 3) {
				updateGuiWorld((GuiCreateWorld) event.getGui());
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
			if (event.getGui() instanceof GuiCreateWorld) {
				OptionTweaks.updateGuiWorld((GuiCreateWorld) event.getGui());
			} else if (event.getGui() instanceof GuiOptions && ConfigManager.forceDifficulty) {
				GuiButton b = ((GuiOptions) event.getGui()).difficultyButton;
				if (b != null) {
					b.enabled = false;
					Method m = GuiCreateWorld.class.getDeclaredMethods()[4];
					m.setAccessible(true);
					try {
						m.invoke(event.getGui());
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						LogManager.getLogger(MiscTweaks.NAME).catching(e);
					}
				}
			} else if (event.getGui() instanceof GuiMainMenu) {
				if (ConfigManager.removeRealmsButton) {
					((GuiMainMenu) event.getGui()).realmsButton.visible = false;
					((GuiMainMenu) event.getGui()).realmsNotification = null;
					GuiButton modButton = ReflectionHelper.getPrivateValue(GuiMainMenu.class,
							(GuiMainMenu) event.getGui(), "modButton");
					modButton.width = 200;
				}
				if (ConfigManager.removeCopyrightText) {
					((GuiMainMenu) event.getGui()).widthCopyright = 0;
					((GuiMainMenu) event.getGui()).widthCopyrightRest = event.getGui().width + 1;
				}
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
									new GammaSlider(slider.id, slider.x, slider.y), isEven ? 1 : 2);
						}
					}
				}
			}
		}
	}
}