package coolsquid.misctweaks.util;

import coolsquid.misctweaks.config.ConfigManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientEventHandler {

	public static final ClientEventHandler INSTANCE = new ClientEventHandler();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
		if (ConfigManager.disabledOverlays.contains(event.getType())) {
			event.setCanceled(true);
		}
	}
}