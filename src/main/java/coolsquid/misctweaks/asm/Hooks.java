package coolsquid.misctweaks.asm;

import coolsquid.misctweaks.config.ConfigManager;

public class Hooks {

	public static int getFireTick() {
		return ConfigManager.fireTickRate;
	}
}