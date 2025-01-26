package starlock.obf.obfuscator.transformers.impl.number;

import org.objectweb.asm.tree.*;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.NumberTransformer;

import java.util.Arrays;
import java.util.Random;

public class HeavyNumberTransformer extends NumberTransformer {
    public static int key = (new Random()).nextInt();
    public void obfuscate(Obfuscator obfuscator) {
        obfuscator.getClasses().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn -> {
                            if(isInteger(insn)){
                                String encrypted = encrypt(getInteger(insn), key);

                                InsnList list = new InsnList();
                                list.add(new LdcInsnNode(encrypted));
                                list.add(new LdcInsnNode(0));
                                list.add(new LdcInsnNode((new Random()).nextInt()));
                                list.add(new MethodInsnNode(INVOKESTATIC, "starlock/StarLock", "decrypt", "(Ljava/lang/String;II)Ljava/lang/String;"));
                                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I"));

                                methodNode.instructions.insertBefore(insn, list);
                                methodNode.instructions.remove(insn);

                            } else if(isLong(insn)){
                                String encrypted = encrypt(getLong(insn), key);

                                InsnList list = new InsnList();
                                list.add(new LdcInsnNode(encrypted));
                                list.add(new LdcInsnNode(0));
                                list.add(new LdcInsnNode((new Random()).nextInt()));
                                list.add(new MethodInsnNode(INVOKESTATIC, "starlock/StarLock", "decrypt", "(Ljava/lang/String;II)Ljava/lang/String;"));
                                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Long", "parseLong", "(Ljava/lang/String;)J"));

                                methodNode.instructions.insertBefore(insn, list);
                                methodNode.instructions.remove(insn);

                            }
                        });
            });
        });
    }
    private String encrypt(long toObf, int key){
        char[] str = Long.toString(toObf).toCharArray();
        StringBuilder encrypted = new StringBuilder();
        for (char c : str) {
            encrypted.append((char) ((int) c ^ key));
        }
        return encrypted.toString();
    }
}
