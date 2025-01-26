package starlock.obf.obfuscator.transformers.impl.pools;

import org.objectweb.asm.tree.*;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.PoolTransformer;
import starlock.obf.utils.ASMHelper;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NumberPoolTransformer extends PoolTransformer {
    private static String poolKey;
    public void obfuscate(Obfuscator obfuscator){
        obfuscator.getClasses().forEach(classNode -> {
            poolKey = NumberPoolTransformer.getRandomString(new Random().nextInt(128));
            FieldNode field = new FieldNode(ACC_PRIVATE+ACC_STATIC+ACC_SYNTHETIC, NumberPoolTransformer.getRandomString(new Random().nextInt(256)),"Ljava/lang/String;",null, null);
            FieldNode fieldPool = new FieldNode(ACC_PRIVATE+ACC_STATIC+ACC_SYNTHETIC, NumberPoolTransformer.getRandomString(new Random().nextInt(256)),"[Ljava/lang/String;",null, null);
            final String[] toEncrypt = {""};
            AtomicInteger countStr = new AtomicInteger();
            AtomicBoolean cpool = new AtomicBoolean(true);
            classNode.methods.stream()
                    .filter(methodNode -> !methodNode.name.equals("<init>"))
                    .filter(methodNode -> !methodNode.name.equals("<clinit>"))
                    .filter(methodNode -> isAccess(methodNode.access, ACC_STATIC))
                    .forEach(methodNode -> {
                        cpool.set(true);
                        Arrays.stream(methodNode.instructions.toArray())
                                .filter(ASMHelper::isNumber)
                                .forEach(insn -> {
                                    toEncrypt[0] += getNumber(insn)+"<uwu>";

                                    InsnList list = new InsnList();
                                    list.add(new FieldInsnNode(GETSTATIC, classNode.name, fieldPool.name, fieldPool.desc));
                                    list.add(new LdcInsnNode(countStr.getAndIncrement()));
                                    list.add(new InsnNode(AALOAD));
                                    if(isInteger(insn)) list.add(new MethodInsnNode(INVOKESTATIC,"java/lang/Integer","parseInt","(Ljava/lang/String;)I"));
                                    if(isLong(insn)) list.add(new MethodInsnNode(INVOKESTATIC,"java/lang/Long","parseLong","(Ljava/lang/String;)J"));
                                    if(isFloat(insn)) list.add(new MethodInsnNode(INVOKESTATIC,"java/lang/Float","parseFloat","(Ljava/lang/String;)F"));
                                    if(isDouble(insn)) list.add(new MethodInsnNode(INVOKESTATIC,"java/lang/Double","parseDouble","(Ljava/lang/String;)D"));
                                    methodNode.instructions.insert(insn, list);
                                    methodNode.instructions.remove(insn);
                                });
                    });
            if(cpool.get()){
                int key1 = new Random().nextInt(), key2 = new Random().nextInt();
                String enc = encryptPool(toEncrypt[0], poolKey, key1, key2);
                InsnList list = new InsnList();

                field.value = enc;
                classNode.fields.add(field);
                classNode.fields.add(fieldPool);

                //list.add(new LdcInsnNode(enc));
                list.add(new FieldInsnNode(GETSTATIC, classNode.name, field.name, field.desc));
                list.add(new LdcInsnNode(poolKey));
                list.add(new LdcInsnNode(key1));
                list.add(new LdcInsnNode(key2));
                list.add(new MethodInsnNode(INVOKESTATIC, "starlock/StarLock", "decryptPool", "(Ljava/lang/String;Ljava/lang/String;II)Ljava/lang/String;"));
                list.add(new LdcInsnNode("<uwu>"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "split", "(Ljava/lang/String;)[Ljava/lang/String;"));
                list.add(new FieldInsnNode(PUTSTATIC, classNode.name, fieldPool.name, fieldPool.desc));

                MethodNode clinit = getClinit(classNode);
                clinit.instructions.insert(clinit.instructions.get(0),list);
            }

        });
    }
    private static MethodNode getClinit(ClassNode classNode){
        MethodNode methodNode = classNode.methods.stream().filter(methodNode1 -> methodNode1.name.equals("<clinit>")).findFirst().orElse(null);
        if(methodNode == null){
            methodNode = new MethodNode(ACC_STATIC, "<clinit>", "()V", null, null);;
            InsnList list = new InsnList();
            list.add(new InsnNode(RETURN));
            methodNode.instructions = list;
        }
        return methodNode;
    }
    public String decryptPool(String var0, String varKeys, int var3, int key) {
        char[] var1 = var0.toCharArray();
        char[] banan = varKeys.toCharArray();
        for(int var11 = 0; var11 < var1.length; ++var11) {
            var1[var11] ^= banan[var11 % banan.length];
        }
        char[] var17 = new char[var1.length];
        for(int var18 = 0; var18 < var17.length; ++var18) {
            switch (var18 % 6) {
                case 0 -> var17[var18] = (char) (var3 ^ key ^ var1[var18]);
                case 1 -> var17[var18] = (char) (key ^ var1[var18]);
                case 2 -> var17[var18] = (char) (var1[var18] ^ key);
                case 3 -> var17[var18] = (char) (var18 ^ key ^ var1[var18]);
                case 4 -> var17[var18] = (char) (var3 ^ var18 ^ var1[var18]);
                case 5 -> var17[var18] = (char) (var1[var18] ^ var18);
            }
        }
        return new String(var17);
    }
    public String encryptPool(String var0, String varKeys, int var3, int key) {
        char[] var1 = var0.toCharArray();
        char[] banan = varKeys.toCharArray();

        for(int var18 = 0; var18 < var1.length; ++var18) {
            switch (var18 % 6) {
                case 0 -> var1[var18] = (char) (var1[var18] ^ var18);
                case 1 -> var1[var18] = (char) (var3 ^ key ^ var1[var18]);
                case 2 -> var1[var18] = (char) (var18 ^ key ^ var1[var18]);
                case 3 -> var1[var18] = (char) (key ^ var1[var18]);
                case 4 -> var1[var18] = (char) (var1[var18] ^ key);
                case 5 -> var1[var18] = (char) (var3 ^ var18 ^ var1[var18]);
            }
        }
        for(int var11 = 0; var11 < var1.length; ++var11) {
            var1[var11] ^= banan[var11 % banan.length];
        }

        return new String(var1);
    }
}
