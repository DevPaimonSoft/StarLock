package starlock.obf.obfuscator.transformers.impl.flow;

import org.objectweb.asm.tree.*;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.ControlFlowTransformer;

import java.util.Arrays;
import java.util.Random;

import static starlock.obf.utils.FlowUtils.*;

public class NormalControlFlowTransformer extends ControlFlowTransformer {
    public void obfuscate(Obfuscator obfuscator) {
        new LightControlFlowTransformer().obfuscate(obfuscator);
        obfuscator.getClasses().stream()
                .filter(classNode -> !isAccess(classNode.access, ACC_INTERFACE))
                .forEach(classNode -> {
            final int[] i = {1};
            final long[] key = {new Random().nextLong()};
            final int[] key2 = {new Random().nextInt()};
            FieldNode field = new FieldNode(ACC_PRIVATE+ACC_STATIC+ACC_SYNTHETIC, getRandomString(39,2),"J",null, key[0]);
            FieldNode field2 = new FieldNode(ACC_PRIVATE+ACC_STATIC+ACC_SYNTHETIC, getRandomString(39,2),"I",null, key2[0]);

            classNode.fields.add(field);
            classNode.fields.add(field2);

            classNode.methods.stream()
                    .filter(methodNode -> !methodNode.name.equals("<init>"))
                    .filter(methodNode -> !methodNode.name.equals("<clinit>"))
                    .forEach(methodNode -> {
                        boolean check = methodNode.localVariables == null;
                        var methodAllVars = check ? 0 : methodNode.localVariables.size();

                        final long[] decKey = {new Random().nextLong()};
                        final int[] varInt = {methodAllVars+2};

                        try {
                            methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), getStartKey((int)key[0], varInt[0]));
                        } catch (Exception ignored){}

                        Arrays.stream(methodNode.instructions.toArray())
                                .forEach(insn -> {
                                    if(new Random().nextBoolean() && insn instanceof JumpInsnNode a)
                                        methodNode.instructions.insertBefore(insn, createFakeJump(a));
                                    else if (insn instanceof LabelNode && i[0] % 32 == 0
                                            && new Random().nextBoolean() && methodNode.instructions.indexOf(insn) > 5) {
                                        methodNode.instructions.insert(insn, getSwitchBlockInt(key[0], decKey[0], field, classNode.name));
                                        key[0] ^= decKey[0];
                                        decKey[0] = new Random().nextLong();
                                        ++varInt[0];
                                    }
                                    else if(isLong(insn)){
                                        long orig = getLong(insn);
                                        long key1 = key[0];
                                        long encrypt = (orig ^ key1);
                                        final InsnList insnList = new InsnList();
                                        insnList.add(new FieldInsnNode(GETSTATIC, classNode.name, field.name, field.desc));
                                        insnList.add(new InsnNode(LXOR));
                                        methodNode.instructions.insert(insn,insnList);
                                        methodNode.instructions.set(insn, new LdcInsnNode(encrypt));
                                    }
                                    else if(isInteger(insn)){
                                        int orig = getInteger(insn);
                                        int key1 = key2[0];
                                        int encrypt = (orig ^ key1);
                                        final InsnList insnList = new InsnList();
                                        insnList.add(new FieldInsnNode(GETSTATIC, classNode.name, field2.name, field2.desc));
                                        insnList.add(new InsnNode(IXOR));
                                        methodNode.instructions.insert(insn,insnList);
                                        methodNode.instructions.set(insn, new LdcInsnNode(encrypt));
                                    }
                                    ++i[0];
                                });
                    });
        });
    }
}