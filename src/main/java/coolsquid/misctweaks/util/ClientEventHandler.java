package coolsquid.misctweaks.util;

import java.lang.reflect.Method;

import coolsquid.misctweaks.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientEventHandler {

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
		if (event.getGui() instanceof GuiCreateWorld) {
			GuiCreateWorld gui = (GuiCreateWorld) event.getGui();
			if (event.getButton().id == 2 && !ConfigManager.allowedGamemodes.isEmpty()) {
				while (!ConfigManager.allowedGamemodes.contains(gui.gameMode.toUpperCase())) {
					try {
						Method m = GuiCreateWorld.class.getDeclaredMethod("actionPerformed", GuiButton.class);
						m.setAccessible(true);
						m.invoke(gui, event.getButton());
					} catch (ReflectiveOperationException e) {
						throw new RuntimeException(e);
					}
				}
			}
			OptionTweaks.disableButtons(gui);
		} else if (event.getGui() instanceof GuiOptions && event.getButton().id == 108 && !ConfigManager.allowedDifficulties.isEmpty()) {
			GuiOptions gui = (GuiOptions) event.getGui();
			while (!ConfigManager.allowedDifficulties.contains(Minecraft.getMinecraft().world.getWorldInfo().getDifficulty().name().toUpperCase())) {
				Minecraft.getMinecraft().world.getWorldInfo().setDifficulty(EnumDifficulty.getDifficultyEnum(Minecraft.getMinecraft().world.getDifficulty().getDifficultyId() + 1));
                gui.difficultyButton.displayString = gui.getDifficultyText(Minecraft.getMinecraft().world.getDifficulty());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
		if (ConfigManager.disabledOverlays.contains(event.getType())) {
			event.setCanceled(true);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiOpen(InitGuiEvent.Post event) {
		if (event.getGui() instanceof GuiCreateWorld) {
			GuiCreateWorld gui = (GuiCreateWorld) event.getGui();
			if (!ConfigManager.defaultGamemode.isEmpty()) {
				if (ConfigManager.defaultGamemode.equalsIgnoreCase("hardcore")) {
					gui.gameMode = "hardcore";
					gui.hardCoreMode = true;
					gui.btnAllowCommands.enabled = false;
					gui.btnBonusItems.enabled = false;
				} else {
					gui.gameMode = ConfigManager.defaultGamemode.toLowerCase();
					if (ConfigManager.defaultGamemode.equals("creative") && ConfigManager.cheats == -1) {
						gui.allowCheats = true;
					}
				}
			}
			if (ConfigManager.cheats != -1) {
				gui.allowCheats = ConfigManager.cheats == 1;
				gui.allowCheatsWasSetByUser = true;
			}
			if (ConfigManager.bonusChest != -1) {
				gui.bonusChestEnabled = ConfigManager.bonusChest == 1;
			}
			if (ConfigManager.generateStructures != -1) {
				gui.generateStructuresEnabled = ConfigManager.generateStructures == 1;
			}
			if (!ConfigManager.defaultWorldType.isEmpty()) {
				gui.selectedIndex = WorldType.parseWorldType(ConfigManager.defaultWorldType).getId();
			}
			if (!ConfigManager.defaultChunkProviderSettings.isEmpty()) {
				gui.chunkProviderSettingsJson = ConfigManager.defaultChunkProviderSettings;
			}
			if (!ConfigManager.defaultSeed.isEmpty()) {
				gui.worldSeed = ConfigManager.defaultSeed;
				gui.worldSeedField.setText(ConfigManager.defaultSeed);
			}
			OptionTweaks.disableButtons(gui);
		}
	}
}