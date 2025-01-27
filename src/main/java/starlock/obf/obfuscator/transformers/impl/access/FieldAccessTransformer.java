package starlock.obf.obfuscator.transformers.impl.access;

import org.objectweb.asm.Opcodes;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.AccessTransformer;

public class FieldAccessTransformer extends AccessTransformer {
    public void obfuscate(Obfuscator obfuscator){
        obfuscator.getClasses().stream()
                .filter(classNode -> !isAccess(classNode.access, ACC_INTERFACE))
                .forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                classNode.fields.forEach(field -> {
                    field.access &= ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED);
                    field.access |= (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_PUBLIC);
                });
            });
        });
    }
}