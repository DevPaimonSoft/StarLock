package starlock.obf.obfuscator.transformers.impl.flow;

import org.objectweb.asm.tree.*;
import starlock.obf.obfuscator.transformers.ControlFlowTransformer;

import java.util.Arrays;
import java.util.Random;

import static starlock.obf.utils.FlowUtils.*;

public class NormalControlFlowTransformer extends ControlFlowTransformer {
    public void obfuscate(ClassNode classNode) {
        new LightControlFlowTransformer().obfuscate(classNode);

        final int[] i = {1};
        final long[] key = {new Random().nextLong()};
        final int[] key2 = {new Random().nextInt()};
        FieldNode field = new FieldNode(ACC_PRIVATE|ACC_STATIC|ACC_SYNTHETIC, getRandomString(39,2),"J",null, key[0]);
        FieldNode field2 = new FieldNode(ACC_PRIVATE|ACC_STATIC|ACC_SYNTHETIC, getRandomString(39,2),"I",null, key2[0]);

        classNode.fields.add(field);
        classNode.fields.add(field2);

        classNode.methods.stream()
                .filter(methodNode -> !methodNode.name.equals("<init>") && !methodNode.name.equals("<clinit>"))
                .forEach(methodNode -> {
                    boolean check = methodNode.localVariables == null;
                    var methodAllVars = check ? 0 : methodNode.localVariables.size();

                    final int[] decKey = {new Random().nextInt()};
                    final int[] varInt = {methodAllVars+2};

                    try {
                        methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), getStartKey((int)key[0], varInt[0]));
                    } catch (Exception ignored){}

                    Arrays.stream(methodNode.instructions.toArray())
                            .forEach(insn -> {
                                //if(insn.getOpcode() >= IFEQ && insn.getOpcode() <= IF_ACMPNE && new Random().nextBoolean()){
                                //val jumpInsnNode = ((JumpInsnNode)insn);
                                //LOGGER.info("Class: " + classNode.name + " method: " + methodNode.name);
                                //methodNode.instructions.insertBefore(insn, createFakeJump(jumpInsnNode));
                                //}
                                //else
                                //if (insn instanceof LabelNode
                                //        && new Random().nextBoolean() && methodNode.instructions.indexOf(insn) > 5) {
                                //    methodNode.instructions.insert(insn, getSwitchBlockInt(key2[0], decKey[0], field2, classNode.name, varInt[0]));
                                //    key2[0] ^= decKey[0];
                                //    decKey[0] = new Random().nextInt();
                                //} else
                                if(isLong(insn)){
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
                                    final InsnList insnList = new InsnList();
                                    int encrypt;
                                    if(new Random().nextBoolean()){
                                        int orig = getInteger(insn);
                                        int key1 = key2[0];
                                        encrypt = (orig ^ key1);
                                        insnList.add(new FieldInsnNode(GETSTATIC, classNode.name, field2.name, field2.desc));
                                        insnList.add(new InsnNode(IXOR));
                                    } else {
                                        int orig = getInteger(insn);
                                        int key1 = key2[0];
                                        encrypt = (orig ^ key1);
                                        insnList.add(new FieldInsnNode(GETSTATIC, classNode.name, field2.name, field2.desc));
                                        insnList.add(new InsnNode(IXOR));
                                    }
                                    methodNode.instructions.insert(insn,insnList);
                                    methodNode.instructions.set(insn, new LdcInsnNode(encrypt));
                                }
                                ++i[0];
                            });
                });
    }
}