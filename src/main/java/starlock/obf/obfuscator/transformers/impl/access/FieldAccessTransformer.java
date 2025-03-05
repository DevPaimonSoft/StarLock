package starlock.obf.obfuscator.transformers.impl.access;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.transformers.AccessTransformer;

public class FieldAccessTransformer extends AccessTransformer {
    public void obfuscate(ClassNode classNode){
        classNode.methods.forEach(methodNode -> {
            classNode.fields.forEach(field -> {
                field.access &= ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED);
                field.access |= (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_PUBLIC);
            });
        });
    }
}