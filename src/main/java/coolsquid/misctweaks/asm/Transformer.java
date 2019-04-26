package coolsquid.misctweaks.asm;

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

public class Transformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals("net.minecraft.block.BlockFire")) {
			ClassNode c = createClassNode(basicClass);
			if (MiscTweaksPlugin.fireSourceHook) {
				MethodNode m = getMethod(c, "updateTick", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", "b", "(Lamu;Let;Lawt;Ljava/util/Random;)V");
				for (int i = 0; i < m.instructions.size(); i++) {
					if (m.instructions.get(i) instanceof MethodInsnNode) {
						MethodInsnNode a = (MethodInsnNode) m.instructions.get(i);
						if (a.name.equals("isFireSource") && (a.desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z") || a.desc.equals("(Lamu;Let;Lfa;)Z"))) {
							m.instructions.insertBefore(a, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "isFireSource",
									"(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z", false));
							m.instructions.remove(a);
							break;
						}
					}
				}
			}
			if (MiscTweaksPlugin.fireTickRateHook) {
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
			if (MiscTweaksPlugin.chestSizeHook) {
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
				MethodNode m = getMethod(c, "getSizeInventory", "()I", "w_", "()I");
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
			if (MiscTweaksPlugin.enderChestSizeHook) {
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
		else if (transformedName.equals("net.minecraft.entity.item.EntityMinecartChest")) {
			ClassNode c = createClassNode(basicClass);
			if (MiscTweaksPlugin.minecartChestSizeHook) {
				MethodNode m = getMethod(c, "getSizeInventory", "()I", "w_", "()I");
				InsnList toInject = new InsnList();
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "getMinecartChestSize",
						"()I", false));
				toInject.add(new InsnNode(Opcodes.IRETURN));
				m.instructions.insertBefore(m.instructions.getFirst(), toInject);
			}
			return toBytes(c);
		}
		else if (transformedName.equals("net.minecraft.entity.item.EntityMinecartContainer")) {
			ClassNode c = createClassNode(basicClass);
			if (MiscTweaksPlugin.minecartChestSizeHook) {
				for (MethodNode m : c.methods) {
					if (m.name.equals("<init>")) {
						for (int i = 0; i < m.instructions.size(); i++) {
							AbstractInsnNode n = m.instructions.get(i);
							if (n instanceof IntInsnNode) {
								// For some reason, the list of items in a minecart chest has room for 36 items, even though the chest can only contain 27 items in Vanilla  
								if (((IntInsnNode) n).operand == 36) {
									InsnList toInject = new InsnList();
									toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "getMinecartChestSize",
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
}