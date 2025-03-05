package starlock.obf.obfuscator.transformers.impl.miscellaneous;

import starlock.obf.manager.StarLockManager;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.MiscellaneousTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import starlock.obf.utils.BytecodeUtils;
public class TrashClassesTransformer extends MiscellaneousTransformer {

    private static final ArrayList<String> DESCRIPTORS = new ArrayList<>();

    static {
        DESCRIPTORS.add("Z");
        DESCRIPTORS.add("C");
        DESCRIPTORS.add("B");
        DESCRIPTORS.add("S");
        DESCRIPTORS.add("I");
        DESCRIPTORS.add("F");
        DESCRIPTORS.add("J");
        DESCRIPTORS.add("D");
        DESCRIPTORS.add("V");
    }
    public void init(ClassNode ignored) {
        Obfuscator obfuscator = new Obfuscator();
        boolean check = LIBS == null;
        List classNames = check ?  new ArrayList<>() : LIBS;
        int ii = new Random().nextInt();
        int l = (ii < 0) ? ~ii : ii;

        if(check){
            for(int i = 0; i < l % 20; i++){
                classNames.add(getRandomName(new Random().nextInt(256),1));
            }
        }

        //obfuscator.getClasses()
        //        .forEach(classNode -> {
        //            classNames.add(classNode.name);
        //        });

        for (int i = 0; i < classNames.size() % 20; i++)
            DESCRIPTORS.add("L" + classNames.get(new Random().nextInt(classNames.size())) + ";");

        for (int i = 0; i < getConfig().getInt("Miscellaneous.TrashClasses"); i++) {
            ClassNode classNode = generateClass();
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cw.newUTF8("StarLock " + StarLockManager.VERSION);
            classNode.accept(cw);

            obfuscator.setFile(classNode.name + ".class", cw.toByteArray());
        }
    }
    private ClassNode generateClass() {
        ClassNode classNode = createClass(getRandomString(39,2));
        int methodsToGenerate = new Random().nextInt(3) + 2;

        for (int i = 0; i < methodsToGenerate; i++)
            classNode.methods.add(methodGen());

        return classNode;
    }

    private ClassNode createClass(String className) {
        ClassNode classNode = new ClassNode();
        classNode.visit(49, ACC_SUPER + ACC_PUBLIC, className, null, "java/lang/Object", null);

        MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        classNode.visitEnd();

        return classNode;
    }

    private MethodNode methodGen() {
        String randDesc = descGen();
        MethodNode method = new MethodNode(ACC_STATIC + ACC_PRIVATE, randomString(7), randDesc, null, null);
        int instructions = new Random().nextInt(30) + 30;

        InsnList insns = new InsnList();

        for (int i = 0; i < instructions; ++i)
            insns.add(junkInstructions());

        Type returnType = Type.getReturnType(randDesc);
        switch (returnType.getSort()) {
            case Type.VOID:
                insns.add(new InsnNode(RETURN));
                break;
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                if (new Random().nextInt(10) % 2 == 1)
                    insns.add(new InsnNode(ICONST_0));
                else
                    insns.add(new InsnNode(ICONST_1));

                insns.add(new InsnNode(IRETURN));
                break;
            case Type.FLOAT:
                insns.add(BytecodeUtils.getNumberInsn(new Random().nextFloat()));
                insns.add(new InsnNode(FRETURN));
                break;
            case Type.LONG:
                insns.add(BytecodeUtils.getNumberInsn(new Random().nextLong()));
                insns.add(new InsnNode(LRETURN));
                break;
            case Type.DOUBLE:
                insns.add(BytecodeUtils.getNumberInsn(new Random().nextDouble()));
                insns.add(new InsnNode(DRETURN));
                break;
            default:
                insns.add(new VarInsnNode(ALOAD, new Random().nextInt(30)));
                insns.add(new InsnNode(ARETURN));
                break;
        }

        method.instructions = insns;
        return method;
    }

    private String descGen() {
        StringBuilder sb = new StringBuilder("(");

        for (int i = 0; i < new Random().nextInt(7); i++)
            sb.append(DESCRIPTORS.get(new Random().nextInt(DESCRIPTORS.size())));

        sb.append(")");
        sb.append(DESCRIPTORS.get(new Random().nextInt(DESCRIPTORS.size())));

        return sb.toString();
    }

    private AbstractInsnNode junkInstructions() {
        int index = new Random().nextInt(20);
        return switch (index) {
            case 0 -> new MethodInsnNode(INVOKESTATIC, randomString(7), randomString(7), "(Ljava/lang/String;)V", false);
            case 1 -> new FieldInsnNode(GETFIELD, randomString(7), randomString(7), "I");
            case 2 -> new InsnNode(new Random().nextInt(16));
            case 3 -> new VarInsnNode(ALOAD, new Random().nextInt(30));
            case 4 -> new IntInsnNode(BIPUSH, new Random().nextInt(255));
            case 5 -> new IntInsnNode(SIPUSH, new Random().nextInt(25565));
            case 6, 7, 8 -> new InsnNode(new Random().nextInt(5));
            case 9 -> new LdcInsnNode(randomString(7));
            case 10 -> new IincInsnNode(new Random().nextInt(16), new Random().nextInt(16));
            case 11 -> new MethodInsnNode(INVOKESPECIAL, randomString(7), randomString(7), "()V", false);
            case 12 ->
                    new MethodInsnNode(INVOKEVIRTUAL, randomString(7), randomString(7), "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            case 13 -> new VarInsnNode(ILOAD, new Random().nextInt(30));
            case 14 -> new InsnNode(ATHROW);
            case 15 -> new MethodInsnNode(INVOKEINTERFACE, randomString(7), randomString(7), "(I)I", false);
            case 16 -> {
                Handle handle = new Handle(6, randomString(7), randomString(7), randomString(7), false);
                yield new InvokeDynamicInsnNode(randomString(7), randomString(7), handle, new Random().nextInt(5), new Random().nextInt(5), new Random().nextInt(5), new Random().nextInt(5), new Random().nextInt(5));
            }
            case 17 -> new IntInsnNode(ANEWARRAY, new Random().nextInt(30));
            case 18 -> new VarInsnNode(ASTORE, new Random().nextInt(30));
            default -> new VarInsnNode(ISTORE, new Random().nextInt(30));
        };
    }
}
