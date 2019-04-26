package coolsquid.misctweaks.util;

import coolsquid.misctweaks.config.ConfigManager;
import net.minecraft.client.gui.GuiCreateWorld;
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
			if (event.getButton().id == 2) {
				OptionTweaks.updateGuiWorld((GuiCreateWorld) event.getGui());
			} else if (event.getButton().id == 5 && !ConfigManager.defaultChunkProviderSettings.isEmpty()) {
				GuiCreateWorld gui = (GuiCreateWorld) event.getGui();
				gui.chunkProviderSettingsJson = ConfigManager.defaultChunkProviderSettings;
				OptionTweaks.updateGuiWorld((GuiCreateWorld) event.getGui());
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
					gui.hardCoreMode = true;
					gui.gameMode = "survival";
					gui.savedGameMode = gui.gameMode;
				} else {
					gui.gameMode = ConfigManager.defaultGamemode.toLowerCase();
					gui.savedGameMode = gui.gameMode;
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
			if (ConfigManager.defaultWorldType != -1) {
				gui.selectedIndex = ConfigManager.defaultWorldType;
			}
			if (!ConfigManager.defaultChunkProviderSettings.isEmpty()) {
				gui.chunkProviderSettingsJson = ConfigManager.defaultChunkProviderSettings;
			}
			if (!ConfigManager.defaultSeed.isEmpty()) {
				gui.worldSeed = ConfigManager.defaultSeed;
				gui.worldSeedField.setText(ConfigManager.defaultSeed);
			}
			OptionTweaks.updateGuiWorld(gui);
		}
	}
}