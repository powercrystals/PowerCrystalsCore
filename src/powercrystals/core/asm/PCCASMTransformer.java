package powercrystals.core.asm;

import cpw.mods.fml.relauncher.IClassTransformer;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import powercrystals.core.asm.relauncher.Implementable;

public class PCCASMTransformer implements IClassTransformer
{
	private String desc;
	private ArrayList<String> workingPath = new ArrayList<String>();

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
		
		workingPath.add(transformedName);

		if (this.implement(cn))
		{
			System.out.println("Adding runtime interfaces to " + transformedName);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			bytes = cw.toByteArray();
			cr = new ClassReader(bytes);
		}
		
		workingPath.remove(workingPath.size() - 1);

		if ("net.minecraft.world.WorldServer".equals(transformedName))
		{
			cn = new ClassNode(Opcodes.ASM4);
			cr.accept(cn, ClassReader.EXPAND_FRAMES);

			/* new WorldServer constructor
			 * WorldServer(MinecraftServer minecraftServer,
							ISaveHandler saveHandler, String worldName,
							WorldProvider provider, WorldSettings worldSettings,
							Profiler theProfiler, ILogAgent worldLogAgent)
			 **/
			MethodNode m = new MethodNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC, "<init>", "(Lnet/minecraft/server/MinecraftServer;Lakf;Ljava/lang/String;Lacn;Laai;Lla;Lku;)V", null, null);
			InsnList code = m.instructions;
			code.add(new VarInsnNode(Opcodes.ALOAD, 0));
			code.add(new InsnNode(Opcodes.DUP));
			code.add(new InsnNode(Opcodes.DUP));
			code.add(new InsnNode(Opcodes.DUP));
			code.add(new InsnNode(Opcodes.DUP));
			code.add(new VarInsnNode(Opcodes.ALOAD, 2));
			code.add(new VarInsnNode(Opcodes.ALOAD, 3));
			code.add(new VarInsnNode(Opcodes.ALOAD, 4));
			code.add(new VarInsnNode(Opcodes.ALOAD, 5));
			code.add(new VarInsnNode(Opcodes.ALOAD, 6));
			code.add(new VarInsnNode(Opcodes.ALOAD, 7));
			code.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "aab", "<init>", "(Lakf;Ljava/lang/String;Lacn;Laai;Lla;Lku;)V"));
			code.add(new VarInsnNode(Opcodes.ALOAD, 1));
			code.add(new FieldInsnNode(Opcodes.PUTFIELD, name, "a", "Lnet/minecraft/server/MinecraftServer;"));
			code.add(new InsnNode(Opcodes.ACONST_NULL));
			code.add(new FieldInsnNode(Opcodes.PUTFIELD, name, "J", "Lit;"));
			code.add(new InsnNode(Opcodes.ACONST_NULL));
			code.add(new FieldInsnNode(Opcodes.PUTFIELD, name, "J", "Liw;"));
			code.add(new InsnNode(Opcodes.ACONST_NULL));
			code.add(new FieldInsnNode(Opcodes.PUTFIELD, name, "J", "Laao;"));
			code.add(new InsnNode(Opcodes.RETURN));
			
			cn.methods.add(m);

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
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
										if (!workingPath.contains(clazz))
										{
											Class.forName(clazz, false, this.getClass().getClassLoader());
										}
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
