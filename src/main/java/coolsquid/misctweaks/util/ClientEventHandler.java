package coolsquid.misctweaks.util;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import coolsquid.misctweaks.config.ConfigManager;

public class ClientEventHandler {

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
		if (event.getGui() instanceof GuiCreateWorld && event.getButton().id == 2) {
			OptionTweaks.updateGuiWorld((GuiCreateWorld) event.getGui());
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
			if (ConfigManager.defaultWorldType != -1) {
				gui.selectedIndex = ConfigManager.defaultWorldType;
				gui.btnMapType.displayString = I18n.format("selectWorld.mapType") + " "
						+ I18n.format(WorldType.WORLD_TYPES[ConfigManager.defaultWorldType].getTranslationKey());
			}
			if (!ConfigManager.defaultChunkProviderSettings.isEmpty()) {
				gui.chunkProviderSettingsJson = ConfigManager.defaultChunkProviderSettings;
			}
		}
	}
}