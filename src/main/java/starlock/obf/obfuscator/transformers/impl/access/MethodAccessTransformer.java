package starlock.obf.obfuscator.transformers.impl.access;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ParameterNode;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.AccessTransformer;

public class MethodAccessTransformer extends AccessTransformer {
    public void obfuscate(Obfuscator obfuscator){
        obfuscator.getClasses().stream()
                .filter(classNode -> !isAccess(classNode.access, ACC_INTERFACE))
                .forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                classNode.methods.forEach(method -> {
                    if (!method.name.equals("<init>") && !method.name.equals("<clinit>")) {
                        method.access &= ~(Opcodes.ACC_VARARGS | Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED);
                        method.access |= (Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE);
                    } else {
                        method.access &= ~(Opcodes.ACC_VARARGS | Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED);
                        method.access |= (Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC);
                    }

                    //if ((Opcodes.ACC_STATIC & method.access) != 0)
                    //    method.access |= (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE);

                    if (method.parameters != null)
                        for (ParameterNode pn : method.parameters)
                            pn.access |= (Opcodes.ACC_SYNTHETIC);
                });
            });
        });
    }
}
