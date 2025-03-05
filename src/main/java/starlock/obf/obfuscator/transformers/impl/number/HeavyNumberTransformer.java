package starlock.obf.obfuscator.transformers.impl.number;

import org.objectweb.asm.tree.*;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.NumberTransformer;
import starlock.obf.obfuscator.transformers.StringTransformer;
import starlock.obf.obfuscator.transformers.impl.string.HeavyStringTransformer;
import starlock.obf.utils.ASMHelper;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class HeavyNumberTransformer extends NumberTransformer {
    private static AtomicInteger fieldValue = new AtomicInteger();
    private boolean useField;
    public void obfuscate(ClassNode classNode) {
        if(getConfig().getBoolean("NumberObfuscation.Random"))
            useField = new Random().nextBoolean();
        else
            useField = true;
        AtomicInteger counter = new AtomicInteger(0);

        fieldValue = new AtomicInteger((new Random()).nextInt());
        FieldNode keyField = new FieldNode(ACC_STATIC | ACC_PRIVATE, getRandomName(128,1), "I", null, fieldValue.get());

        classNode.methods.forEach(methodNode -> {

            Arrays.stream(methodNode.instructions.toArray())
                    .filter(ASMHelper::isInteger)
                    .forEach(insn -> {
                        counter.addAndGet(1);
                        if(new Random().nextBoolean()){
                            int value = getInteger(insn);
                            if(useField) value ^= fieldValue.get();

                            int randKey = (new Random()).nextInt();
                            String encrypted = encrypt(value, key, randKey);

                            InsnList list = new InsnList();
                            list.add(new LdcInsnNode(encrypted));
                            list.add(new LdcInsnNode(0));
                            list.add(new LdcInsnNode(randKey));
                            list.add(new MethodInsnNode(INVOKESTATIC, "starlock/StarLock", "decrypt", "(Ljava/lang/String;II)Ljava/lang/String;"));
                            list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I"));
                            if(useField){
                                list.add(new FieldInsnNode(GETSTATIC, classNode.name, keyField.name,keyField.desc));
                                list.add(new InsnNode(IXOR));
                            }
                            methodNode.instructions.insertBefore(insn, list);
                            methodNode.instructions.remove(insn);
                        } else {
                            int index = (new Random().nextInt() % 3);
                            index = index < 0 ? -index : index;

                            int[] keys = {new Random().nextInt(), new Random().nextInt(), new Random().nextInt()};
                            long seed = new Random().nextLong();
                            int value = obfuscate(getInteger(insn), index, keys,seed);
                            if(useField)
                                value ^= fieldValue.get();

                            InsnList list = getRandomMutation(value, index, keys, seed);
                            if(useField){
                                list.add(new FieldInsnNode(GETSTATIC, classNode.name, keyField.name,keyField.desc));
                                list.add(new InsnNode(IXOR));

                                //TODO: maybe later i add dynamic key
                                //list.add(new TypeInsnNode(NEW, "java/util/Random"));
                                //list.add(new InsnNode(DUP));
                                //list.add(new LdcInsnNode(seed2));
                                //list.add(new MethodInsnNode(INVOKESPECIAL, "java/util/Random", "<init>", "(J)V"));
                                //list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/util/Random", "nextInt", "()I"));
                                //list.add(new FieldInsnNode(PUTSTATIC, classNode.name, keyField.name,keyField.desc));
                                //fieldValue.set(new Random(seed2).nextInt());
                            }
                            methodNode.instructions.insertBefore(insn, list);
                            methodNode.instructions.remove(insn);
                        }
                    });
            Arrays.stream(methodNode.instructions.toArray())
                    .filter(ASMHelper::isLong)
                    .forEach(insn -> {

                        long value = getLong(insn);
                        int randKey = (new Random()).nextInt();
                        String encrypted = encrypt(value, key, randKey);

                        InsnList list = new InsnList();
                        list.add(new LdcInsnNode(encrypted));
                        list.add(new LdcInsnNode(0));
                        list.add(new LdcInsnNode(randKey));

                        //TODO: This is a template for removing dependency on an external class.
                        String className = classNode.name;
                        if(SLClass)
                            className = "starlock/StarLock";
                        list.add(new MethodInsnNode(INVOKESTATIC, className, SLClass ? "decrypt" : method4Decrypt, "(Ljava/lang/String;II)Ljava/lang/String;"));
                        list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Long", "parseLong", "(Ljava/lang/String;)J"));
                        methodNode.instructions.insert(insn, list);
                        methodNode.instructions.remove(insn);
                    });
        });
        if(useField && counter.get() != 0) classNode.fields.add(keyField);
        if (!SLClass && counter.get() != 0){
            MethodNode decrypt = new MethodNode(ACC_PRIVATE | ACC_STATIC, method4Decrypt,"(Ljava/lang/String;II)Ljava/lang/String;", null,null);
            ClassNode sl = getSLClassNode();
            MethodNode temp = sl.methods.stream().filter(methodNode1 -> methodNode1.name.equals("decrypt")
                    && methodNode1.desc.equals("(Ljava/lang/String;II)Ljava/lang/String;")).findFirst().orElse(null);
            if(temp != null){
                decrypt.instructions = temp.instructions;
                Arrays.stream(temp.instructions.toArray())
                        .forEach(insn -> {
                            if (ASMHelper.isInteger(insn) && ASMHelper.getInteger(insn) == 1122331334) {
                                temp.instructions.set(insn, new LdcInsnNode(NumberTransformer.key));
                            } else if (ASMHelper.isInteger(insn) && ASMHelper.getInteger(insn) == 345345777) {
                                temp.instructions.set(insn, new LdcInsnNode(StringTransformer.key));
                            } else if (ASMHelper.isString(insn) && ASMHelper.getString(insn).equals("SPLITSTRING")) {
                                temp.instructions.set(insn, new LdcInsnNode(getUnicodeString(6)));
                            }
                        });
                classNode.methods.add(decrypt);
            } else {
                throw new RuntimeException("Could not find decrypt method!");
            }
        }
    }
    private String encrypt(long toObf, int key, int randKey){
        char[] str = Long.toString(toObf).toCharArray();
        StringBuilder encrypted = new StringBuilder();
        for (char c : str) {
            encrypted.append((char) ((int) c ^ key ^ randKey));
        }
        return encrypted.toString();
    }

    public static int obfuscate(int value, int index, int[] keys, long seed) {
        return switch (index) {
            case 0 -> (value ^ keys[0] ^ keys[1]);
            case 1 -> (new Random(seed).nextInt() ^ value);
            case 2 -> (value ^ keys[2] ^ new Random(seed).nextInt());
            default -> throw new IllegalArgumentException("Unknown formula index: " + index);
        };
    }

    private InsnList getRandomMutation(int value, int index, int[] keys, long seed) {
        InsnList insnList = new InsnList();
        // * imul
        // / idiv
        // + iadd
        // - isub
        // << ishl
        // >> ishr
        // | ior


        switch (index) {
            case 0:
                insnList.add(new LdcInsnNode(keys[0]));
                insnList.add(new LdcInsnNode(keys[1]));
                insnList.add(new InsnNode(IXOR));
                insnList.add(new LdcInsnNode(value));
                insnList.add(new InsnNode(IXOR));
                break;
            case 1:
                insnList.add(new TypeInsnNode(NEW, "java/util/Random"));
                insnList.add(new InsnNode(DUP));
                insnList.add(new LdcInsnNode(seed));
                insnList.add(new MethodInsnNode(INVOKESPECIAL, "java/util/Random", "<init>", "(J)V"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/util/Random", "nextInt", "()I"));
                insnList.add(new LdcInsnNode(value));
                insnList.add(new InsnNode(IXOR));
                break;
            case 2:
                insnList.add(new LdcInsnNode(keys[2]));
                insnList.add(new LdcInsnNode(value));
                insnList.add(new InsnNode(IXOR));
                insnList.add(new TypeInsnNode(NEW, "java/util/Random"));
                insnList.add(new InsnNode(DUP));
                insnList.add(new LdcInsnNode(seed));
                insnList.add(new MethodInsnNode(INVOKESPECIAL, "java/util/Random", "<init>", "(J)V"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/util/Random", "nextInt", "()I"));
                insnList.add(new InsnNode(IXOR));
                break;
            default:
                throw new IllegalArgumentException("Unknown formula index: " + index);
        }

        return insnList;
    }

}
