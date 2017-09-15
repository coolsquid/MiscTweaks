package coolsquid.misctweaks.asm;

import java.util.Map;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class Transformer implements IClassTransformer, IFMLLoadingPlugin {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals("net.minecraft.block.BlockFire")) {
			ClassNode c = createClassNode(basicClass);
			MethodNode m = this.getMethod(c, "tickRate", "(Lnet/minecraft/world/World;)I", "a", "(Lamu;)I");
			InsnList toInject = new InsnList();
			toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "getFireTick",
					"()I", false));
			toInject.add(new InsnNode(Opcodes.IRETURN));
			m.instructions.insertBefore(m.instructions.getFirst(), toInject);
			return toBytes(c);
		}
		return basicClass;
	}

	private MethodNode getMethod(ClassNode c, String name, String desc, String obfName, String obfDesc) {
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