package coolsquid.misctweaks.asm;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import coolsquid.misctweaks.config.ConfigManager;

public class Hooks {

	public static int getFireTick() {
		return ConfigManager.fireTickRate;
	}

	public static boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
		return Hooks.isFireSource(world, pos, side);
	}
}