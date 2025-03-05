package starlock.obf.obfuscator.transformers.impl.string;

import org.objectweb.asm.tree.*;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.StringTransformer;

import java.util.Arrays;
import java.util.Random;

public class HeavyStringTransformer extends StringTransformer {
    public void obfuscate(ClassNode classNode){
        classNode.methods.forEach(methodNode -> {
            Arrays.stream(methodNode.instructions.toArray())
                    .forEach(insn -> {
                        if (isString(insn)){
                            //System.err.println(methodNode.name);
                            int key = (new Random()).nextInt();
                            String encrypted = encrypt(getString(insn), key, StringTransformer.key);
                            //LOGGER.atError().log("Encrypted ->" + encrypted);
                            //LOGGER.atError().log("Key ->" + key);
                            //LOGGER.atError().log("Decrypted ->" + encrypt(encrypted, key, key2));

                            InsnList list = new InsnList();
                            list.add(new LdcInsnNode(encrypted));
                            list.add(new LdcInsnNode(1));
                            list.add(new LdcInsnNode(key));
                            list.add(new MethodInsnNode(INVOKESTATIC, "starlock/StarLock", "decrypt", "(Ljava/lang/String;II)Ljava/lang/String;"));

                            //LOGGER.atError().log(getString(insn) + " -> " + encrypted);

                            methodNode.instructions.insertBefore(insn, list);
                            methodNode.instructions.remove(insn);
                        }
                    });
        });
    }

    public String encrypt(String var0, int var3, int key) {
        char[] var1 = var0.toCharArray();
        char[] banan = var0.toCharArray();
        for(int var11 = 0; var11 < var1.length; ++var11) {
            var1[var11] = (char)(banan[var11] ^ var3);
        }
        char[] var17 = new char[var1.length];
        for(int var18 = 0; var18 < var17.length; ++var18) {
            switch (var18 % 3) {
                case 0 -> var17[var18] = (char) (var3 ^ key ^ var1[var18]);
                case 1 -> var17[var18] = (char) (key ^ var1[var18]);
                case 2 -> var17[var18] = (char) (var1[var18] ^ key);
            }
        }

        return new String(var17);
    }

}
