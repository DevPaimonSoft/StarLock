package starlock.obf.utils;

import lombok.Getter;
import org.objectweb.asm.tree.*;

public class InstructionBuilder {
    @Getter
    private final InsnList instructions = new InsnList();

    private InstructionBuilder() {}

    public static InstructionBuilder allocate() {
        return new InstructionBuilder();
    }

    public InstructionBuilder put(AbstractInsnNode node) {
        instructions.add(node);
        return this;
    }

    public InstructionBuilder put(InsnList node) {
        instructions.add(node);
        return this;
    }

    public InstructionBuilder put(int opcode) {
        instructions.add(new InsnNode(opcode));
        return this;
    }

}