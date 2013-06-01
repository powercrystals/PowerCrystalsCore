package powercrystals.core.asm;

import cpw.mods.fml.relauncher.IClassTransformer;

import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import powercrystals.core.asm.relauncher.Implementable;

public class PCCASMTransformer implements IClassTransformer
{
	private String desc;

	public PCCASMTransformer()
	{
		desc = Type.getDescriptor(Implementable.class);
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);

		if (this.implement(cn))
		{
			System.out.println("Adding runtime interfaces to " + transformedName);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
			cr = new ClassReader(bytes);
		}

		if ("net.minecraft.world.WorldServer".equals(transformedName))
		{
			cn = new ClassNode(Opcodes.ASM4);
			cr.accept(cn, ClassReader.EXPAND_FRAMES);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			/* new WorldServer constructor
			 * WorldServer(MinecraftServer minecraftServer,
							ISaveHandler saveHandler, String worldName,
							WorldProvider provider, WorldSettings worldSettings,
							Profiler theProfiler, ILogAgent worldLogAgent)
			 **/
			cw.newMethod(name, "<init>", "(Lnet/minecraft/server/MinecraftServer;Lakf;Ljava/lang/String;Lacn;Laai;Lla;Lku;)V", true);
			MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Lnet/minecraft/server/MinecraftServer;Lakf;Ljava/lang/String;Lacn;Laai;Lla;Lku;)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitInsn(Opcodes.DUP);
			mv.visitInsn(Opcodes.DUP);
			mv.visitInsn(Opcodes.DUP);
			mv.visitInsn(Opcodes.DUP);
			mv.visitVarInsn(Opcodes.ALOAD, 2);
			mv.visitVarInsn(Opcodes.ALOAD, 3);
			mv.visitVarInsn(Opcodes.ALOAD, 4);
			mv.visitVarInsn(Opcodes.ALOAD, 5);
			mv.visitVarInsn(Opcodes.ALOAD, 6);
			mv.visitVarInsn(Opcodes.ALOAD, 7);
			// [World] super(saveHandler, par2String, provider, par4WorldSettings, theProfiler, worldLogAgent);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "aab", "<init>", "(Lakf;Ljava/lang/String;Lacn;Laai;Lla;Lku;)V");
			mv.visitVarInsn(Opcodes.ALOAD, 1);
			mv.visitFieldInsn(Opcodes.PUTFIELD, name, "a", "Lnet/minecraft/server/MinecraftServer;");
			mv.visitInsn(Opcodes.ACONST_NULL);
			mv.visitFieldInsn(Opcodes.PUTFIELD, name, "J", "Lit;");
			mv.visitInsn(Opcodes.ACONST_NULL);
			mv.visitFieldInsn(Opcodes.PUTFIELD, name, "K", "Liw;");
			mv.visitInsn(Opcodes.ACONST_NULL);
			mv.visitFieldInsn(Opcodes.PUTFIELD, name, "P", "Laao;");
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(11, 10);
			mv.visitEnd();
			cw.visitEnd();
			bytes = cw.toByteArray();
		}

		return bytes;
	}

	private boolean implement(ClassNode cn)
	{
		if (cn.visibleAnnotations == null)
		{
			return false;
		}
		boolean interfaces = false;
		for (AnnotationNode node : cn.visibleAnnotations)
		{
			if (node.desc.equals(desc))
			{
				if (node.values != null)
				{
					List<Object> values = node.values;
					for (int i = 0, e = values.size(); i < e; )
					{
						Object k = values.get(i++);
						Object v = values.get(i++);
						if (k instanceof String && k.equals("value") && v instanceof String)
						{
							String[] value = ((String)v).split(";");
							for (int j = 0, l = value.length; j < l; ++j)
							{
								String clazz = value[j].trim();
								String cz = clazz.replace('.', '/');
								if (!cn.interfaces.contains(cz))
								{
									try {
										Class.forName(clazz, false, this.getClass().getClassLoader());
										cn.interfaces.add(cz);
										interfaces = true;
									} catch (Throwable _) {}
								}
							}
						}
					}
				}
			}
		}
		return interfaces;
	}

}
