package powercrystals.core.asm;

import cpw.mods.fml.relauncher.FMLRelauncher;
import cpw.mods.fml.relauncher.IClassTransformer;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import powercrystals.core.CoreLoader;
import powercrystals.core.asm.relauncher.Implementable;

public class PCCASMTransformer implements IClassTransformer
{
	private String desc;
	private ArrayList<String> workingPath = new ArrayList<String>();
	private boolean isClient = FMLRelauncher.side().equals("CLIENT");

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
			bytes = writeWorldServer(name, transformedName, bytes, cr);
		}
		else if (!isClient && "net.minecraft.world.World".equals(transformedName))
		{
			bytes = writeWorld(name, transformedName, bytes, cr);
		}

		return bytes;
	}

	private byte[] writeWorld(String name, String transformedName, byte[] bytes, ClassReader cr)
	{
		String[] names = null;
		if (CoreLoader.runtimeDeobfEnabled)
		{
			names = new String[]{"field_73019_z","field_72986_A","field_73011_w","field_72984_F","field_98181_L"};
		}
		else
		{
			names = new String[]{"saveHandler","worldInfo","provider","theProfiler","worldLogAgent"};
		}
		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(Opcodes.ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		/* new World constructor
		 * World(ISaveHandler saveHandler, String worldName,
						WorldProvider provider, WorldSettings worldSettings,
						Profiler theProfiler, ILogAgent worldLogAgent)
		 **/
		String sig = "(Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;Lnet/minecraft/logging/ILogAgent;)V";
		cw.newMethod(name, "<init>", sig, true);
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", sig, null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitInsn(Opcodes.DUP);
		mv.visitInsn(Opcodes.DUP);
		mv.visitInsn(Opcodes.DUP);
		mv.visitInsn(Opcodes.DUP);
		mv.visitInsn(Opcodes.DUP);
		mv.visitInsn(Opcodes.DUP);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, name, names[0], "Lnet/minecraft/world/storage/ISaveHandler;");
		mv.visitTypeInsn(Opcodes.NEW, "net/minecraft/world/storage/WorldInfo");
		mv.visitInsn(Opcodes.DUP);
		mv.visitVarInsn(Opcodes.ALOAD, 4);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/world/storage/WorldInfo", "<init>", "(Lnet/minecraft/world/WorldSettings;Ljava/lang/String;)V");
		mv.visitFieldInsn(Opcodes.PUTFIELD, name, names[1], "Lnet/minecraft/world/storage/WorldInfo;");
		mv.visitVarInsn(Opcodes.ALOAD, 3);
		mv.visitFieldInsn(Opcodes.PUTFIELD, name, names[2], "Lnet/minecraft/world/WorldProvider;");
		mv.visitVarInsn(Opcodes.ALOAD, 5);
		mv.visitFieldInsn(Opcodes.PUTFIELD, name, names[3], "Lnet/minecraft/profiler/Profiler;");
		mv.visitVarInsn(Opcodes.ALOAD, 6);
		mv.visitFieldInsn(Opcodes.PUTFIELD, name, names[3], "Lnet/minecraft/logging/ILogAgent;");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(11, 10);
		mv.visitEnd();
		cw.visitEnd();
		return cw.toByteArray();
	}

	private byte[] writeWorldServer(String name, String transformedName, byte[] bytes, ClassReader cr)
	{
		String[] names = null;
		if (CoreLoader.runtimeDeobfEnabled)
		{
			names = new String[]{"field_73061_a","field_73062_L","field_73063_M","field_85177_Q"};
		}
		else
		{
			names = new String[]{"mcServer","theEntityTracker","thePlayerManager","field_85177_Q"};
		}
		name = name.replace('.', '/');
		ClassNode cn = new ClassNode(Opcodes.ASM4);
		cr.accept(cn, ClassReader.EXPAND_FRAMES);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		/* new WorldServer constructor
		 * WorldServer(MinecraftServer minecraftServer,
						ISaveHandler saveHandler, String worldName,
						WorldProvider provider, WorldSettings worldSettings,
						Profiler theProfiler, ILogAgent worldLogAgent)
		 **/
		String sig = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;Lnet/minecraft/logging/ILogAgent;)V";
		cw.newMethod(name, "<init>", sig, true);
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", sig, null, null);
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
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/world/World", "<init>", "(Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;Lnet/minecraft/logging/ILogAgent;)V");
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, name, names[0], "Lnet/minecraft/server/MinecraftServer;");
		mv.visitInsn(Opcodes.ACONST_NULL);
		mv.visitFieldInsn(Opcodes.PUTFIELD, name, names[1], "Lnet/minecraft/entity/EntityTracker;");
		mv.visitInsn(Opcodes.ACONST_NULL);
		mv.visitFieldInsn(Opcodes.PUTFIELD, name, names[2], "Lnet/minecraft/server/management/PlayerManager;");
		mv.visitInsn(Opcodes.ACONST_NULL);
		mv.visitFieldInsn(Opcodes.PUTFIELD, name, names[3], "Lnet/minecraft/world/Teleporter;");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(11, 10);
		mv.visitEnd();
		cw.visitEnd();
		return cw.toByteArray();
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
