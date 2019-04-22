package coolsquid.misctweaks.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class Transformer implements IClassTransformer, IFMLLoadingPlugin {

	private static boolean fireTickRateHook = true, fireSourceHook = true, chestSizeHook = true, enderChestSizeHook = true;

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals("net.minecraft.block.BlockFire")) {
			ClassNode c = createClassNode(basicClass);
			if (fireSourceHook) {
				MethodNode m = getMethod(c, "updateTick", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", "", "");
				for (int i = 0; i < m.instructions.size(); i++) {
					if (m.instructions.get(i) instanceof MethodInsnNode) {
						MethodInsnNode a = (MethodInsnNode) m.instructions.get(i);
						if (a.name.equals("isFireSource") && a.desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z")) {
							m.instructions.insertBefore(a, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "isFireSource",
									"(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z", false));
							m.instructions.remove(a);
							break;
						}
					}
				}
			}
			if (fireTickRateHook) {
				MethodNode m = getMethod(c, "tickRate", "(Lnet/minecraft/world/World;)I", "a", "(Lamu;)I");
				InsnList toInject = new InsnList();
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "getFireTick",
						"()I", false));
				toInject.add(new InsnNode(Opcodes.IRETURN));
				m.instructions.insertBefore(m.instructions.getFirst(), toInject);
			}
			return toBytes(c);
		}
		else if (transformedName.equals("net.minecraft.tileentity.TileEntityChest")) {
			ClassNode c = createClassNode(basicClass);
			if (chestSizeHook) {
				for (MethodNode m : c.methods) {
					if (m.name.equals("<init>")) {
						for (int i = 0; i < m.instructions.size(); i++) {
							AbstractInsnNode n = m.instructions.get(i);
							if (n instanceof IntInsnNode) {
								if (((IntInsnNode) n).operand == 27) {
									InsnList toInject = new InsnList();
									toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "getChestSize",
											"()I", false));
									//toInject.add(new InsnNode(Opcodes.ILOAD));
									m.instructions.insertBefore(n, toInject);
									m.instructions.remove(n);
								}
							}
						}
					}
				}
				MethodNode m = getMethod(c, "getSizeInventory", "()I", "a", "(Lamu;)I");
				InsnList toInject = new InsnList();
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "getChestSize",
						"()I", false));
				toInject.add(new InsnNode(Opcodes.IRETURN));
				m.instructions.insertBefore(m.instructions.getFirst(), toInject);
			}
			return toBytes(c);
		}
		else if (transformedName.equals("net.minecraft.inventory.InventoryEnderChest")) {
			ClassNode c = createClassNode(basicClass);
			if (enderChestSizeHook) {
				for (MethodNode m : c.methods) {
					if (m.name.equals("<init>")) {
						for (int i = 0; i < m.instructions.size(); i++) {
							AbstractInsnNode n = m.instructions.get(i);
							if (n instanceof IntInsnNode) {
								if (((IntInsnNode) n).operand == 27) {
									InsnList toInject = new InsnList();
									toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "getEnderChestSize",
											"()I", false));
									//toInject.add(new InsnNode(Opcodes.ILOAD));
									m.instructions.insertBefore(n, toInject);
									m.instructions.remove(n);
								}
							}
						}
					}
				}
			}
			return toBytes(c);
		}
		return basicClass;
	}

	private static MethodNode getMethod(ClassNode c, String name, String desc, String obfName, String obfDesc) {
		for (MethodNode m : c.methods) {
			if ((m.name.equals(name) || m.name.equals(obfName)) && (m.desc.equals(desc) || m.desc.equals(obfDesc))) {
				return m;
			}
		}
		return null;
	}

	private static ClassNode createClassNode(byte[] bytes) {
		ClassNode c = new ClassNode();
		ClassReader r = new ClassReader(bytes);
		r.accept(c, ClassReader.EXPAND_FRAMES);
		return c;
	}

	private static byte[] toBytes(ClassNode c) {
		ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		c.accept(w);
		return w.toByteArray();
	}

	@Override
	public String[] getASMTransformerClass() {
		Properties properties = new Properties();
		File propertiesFile = new File("config/MiscTweaks_ASM.properties");
		if (propertiesFile.exists()) {
			try {
				properties.load(new FileInputStream(propertiesFile));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		fireTickRateHook = Boolean.parseBoolean(properties.getProperty("fireTickRateHook", "true"));
		fireSourceHook = Boolean.parseBoolean(properties.getProperty("fireSourceHook", "true"));
		chestSizeHook = Boolean.parseBoolean(properties.getProperty("chestSizeHook", "true"));
		enderChestSizeHook = Boolean.parseBoolean(properties.getProperty("enderChestSizeHook", "true"));
		return new String[] { this.getClass().getName() };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}