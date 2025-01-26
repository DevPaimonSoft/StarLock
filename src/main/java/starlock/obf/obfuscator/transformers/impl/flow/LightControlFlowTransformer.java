package starlock.obf.obfuscator.transformers.impl.flow;

import lombok.val;
import org.objectweb.asm.tree.*;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.ControlFlowTransformer;

import java.util.Arrays;
import java.util.Random;

import static starlock.obf.utils.FlowUtils.*;

public class LightControlFlowTransformer extends ControlFlowTransformer {

    public void obfuscate(Obfuscator obfuscator) {
        obfuscator.getClasses().forEach(classNode -> {
            classNode.methods.stream()
                    .filter(methodNode -> !methodNode.name.equals("<init>"))
                    .filter(methodNode -> !methodNode.name.equals("<clinit>"))
                    .filter(methodNode -> isAccess(methodNode.access, ACC_STATIC))
                    .forEach(methodNode -> {
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn -> {
                            if(isInteger(insn)){
                                val list = new InsnList();
                                val intDec = new Random().nextInt();
                                val intDec2 = new Random().nextInt();
                                val origInt = getInteger(insn);
                                val forPaste = (origInt ^ intDec ^ intDec2);

                                list.add(new LdcInsnNode(Integer.toString(intDec)));
                                list.add(new MethodInsnNode(INVOKESTATIC,"java/lang/Integer","parseInt","(Ljava/lang/String;)I"));
                                list.add(new InsnNode(IXOR));
                                list.add(new LdcInsnNode(intDec2));
                                list.add(new InsnNode(IXOR));

                                methodNode.instructions.insert(insn, list);
                                methodNode.instructions.set(insn, new LdcInsnNode(forPaste));
                            } else if(insn.getOpcode() >= IFEQ && insn.getOpcode() <= IF_ACMPNE){
                                val jumpInsnNode = ((JumpInsnNode)insn);
                                val offset = new LabelNode();
                                val insnList = new InsnList();

                                insnList.add(new JumpInsnNode(GOTO, jumpInsnNode.label));
                                insnList.add(offset);

                                jumpInsnNode.setOpcode(reverseJump(insn.getOpcode()));
                                jumpInsnNode.label = offset;

                                methodNode.instructions.insert(jumpInsnNode, insnList);
                            }
                        });
            });
        });
    }
}
