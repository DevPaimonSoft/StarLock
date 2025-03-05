package starlock.obf.obfuscator.transformers.impl.access;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.AccessTransformer;

public class ClassAccessTransformer extends AccessTransformer {
    public void obfuscate(ClassNode classNode){
        classNode.methods.forEach(methodNode -> {
            classNode.access &= ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED);
            classNode.access |= (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_PUBLIC);
        });
    }
}