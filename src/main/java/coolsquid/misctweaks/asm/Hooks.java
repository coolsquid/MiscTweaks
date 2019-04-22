package coolsquid.misctweaks.asm;

import coolsquid.misctweaks.config.ConfigManager;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Hooks {

	public static int getFireTick() {
		return ConfigManager.fireTickRate;
	}

	public static boolean isFireSource(Block block, World world, BlockPos pos, EnumFacing side) {
		if (ConfigManager.disabledFireSources.contains(block.getRegistryName().toString())) {
			return false;
		} else if (ConfigManager.newFireSources.contains(block.getRegistryName().toString())) {
			return side == EnumFacing.UP;
		}
		return block.isFireSource(world, pos, side);
	}

	public static int getChestSize() {
		return ConfigManager.chestSize;
	}

	public static int getEnderChestSize() {
		return ConfigManager.enderChestSize;
	}
}