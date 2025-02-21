package starlock.obf.obfuscator.transformers.impl.watermark;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.WaterMarkTransformer;

/*
    @author lustman, 2025.
    заодно ломает дизассемблер в recaf'е.
*/
public class InvokeDynamicWatermarkTransformer extends WaterMarkTransformer {
    public void obfuscate(Obfuscator obfuscator) {
        obfuscator.getClasses().forEach(classNode -> {
            classNode.methods.stream()
                    .filter(methodNode -> methodNode.name.equals("<init>"))
                    .forEach(methodNode -> {
                        InsnList insnList = new InsnList();
                        var l = new LabelNode();
                        insnList.add(new InsnNode(ACONST_NULL));
                        insnList.add(new JumpInsnNode(IFEQ, l));
                        insnList.add(new InvokeDynamicInsnNode("get da starl0ck", "()V", new Handle(Opcodes.H_INVOKESTATIC, "", "yap", "()V;", false)));
                        insnList.add(new InvokeDynamicInsnNode("lox", "()B", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/0", "", "()L;", false)));
                        insnList.add(new InsnNode(Opcodes.POP));
                        insnList.add(l);
                        AbstractInsnNode first = methodNode.instructions.get(0);
                        methodNode.instructions.insertBefore(first, insnList);
                    });

        });
    }
}
