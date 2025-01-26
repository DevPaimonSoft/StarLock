package starlock.obf.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Random;

public class FlowUtils extends ASMHelper implements Opcodes {
    public static InsnList createFakeJump(JumpInsnNode originalJump) {
        InsnList fakeJump = new InsnList();

        int randomValue1 = new Random().nextInt();
        int randomValue2 = new Random().nextInt();

        fakeJump.add(new LdcInsnNode(randomValue1));
        fakeJump.add(new LdcInsnNode(randomValue2));
        fakeJump.add(new InsnNode(ISUB));
        fakeJump.add(new InsnNode(ICONST_0));
        fakeJump.add(new InsnNode(IADD));
        fakeJump.add(new JumpInsnNode(IFGE, originalJump.label));
        fakeJump.add(new TypeInsnNode(NEW, "java/lang/RuntimeException"));
        fakeJump.add(new InsnNode(ATHROW));


        return fakeJump;
    }
    public static InsnList getStartKey(final int key, final int varInt) {
        final InsnList insnList = new InsnList();

        insnList.add(new LdcInsnNode(key));
        insnList.add(new VarInsnNode(ISTORE, varInt));

        return insnList;
    }
    public static InsnList getStartString(final String key, final int varInt) {
        final InsnList insnList = new InsnList();

        insnList.add(new LdcInsnNode(key));
        insnList.add(new VarInsnNode(ASTORE, varInt));

        return insnList;
    }
    public static int reverseJump(int opcode){
        return switch (opcode){
            case IFNE -> IFEQ;
            case IFEQ -> IFNE;
            case IFGE -> IFLT;
            case IFGT -> IFLE;
            case IFLE -> IFGT;
            case IFLT -> IFGE;
            case IFNONNULL -> IFNULL;
            case IFNULL -> IFNONNULL;
            case IF_ACMPEQ -> IF_ACMPNE;
            case IF_ACMPNE -> IF_ACMPEQ;
            case IF_ICMPEQ -> IF_ICMPNE;
            case IF_ICMPNE -> IF_ICMPEQ;
            case IF_ICMPGE -> IF_ICMPLT;
            case IF_ICMPGT -> IF_ICMPLE;
            case IF_ICMPLE -> IF_ICMPGT;
            case IF_ICMPLT -> IF_ICMPGE;
            default -> throw new IllegalStateException(String.format("Unable to reverse jump opcode: %d", opcode));
        };
    }
    public static InsnList getSwitchBlockInt(final long key, final long decKey, final FieldNode field, String owner) {
        final InsnList insnList = new InsnList();
        final LabelNode L0 = new LabelNode(),L1 = new LabelNode(),L2 = new LabelNode(),L3 = new LabelNode(),L4 = new LabelNode();
        LabelNode[] labels = {L0,L1,L2};
        int[] keys = {new Random().nextInt(), new Random().nextInt(), (int)key};
        final LabelNode defLabel = new LabelNode();



        insnList.add(L4);
        insnList.add(new FieldInsnNode(GETSTATIC,owner, field.name, field.desc));
        insnList.add(new LookupSwitchInsnNode(L3, keys, labels));

        insnList.add(L0);
        insnList.add(new FieldInsnNode(GETSTATIC,owner, field.name, field.desc));;
        insnList.add(new LdcInsnNode(new Random().nextLong()));
        insnList.add(new InsnNode(LXOR));
        insnList.add(new FieldInsnNode(PUTSTATIC,owner, field.name, field.desc));
        insnList.add(new JumpInsnNode(GOTO, defLabel));

        insnList.add(L1);
        insnList.add(new FieldInsnNode(GETSTATIC,owner, field.name, field.desc));
        insnList.add(new LdcInsnNode(new Random().nextLong()));
        insnList.add(new InsnNode(LXOR));
        insnList.add(new FieldInsnNode(PUTSTATIC,owner, field.name, field.desc));
        insnList.add(new JumpInsnNode(GOTO, defLabel));

        insnList.add(L2);
        insnList.add(new FieldInsnNode(GETSTATIC,owner, field.name, field.desc));
        insnList.add(new LdcInsnNode(decKey));
        insnList.add(new InsnNode(LXOR));
        insnList.add(new FieldInsnNode(PUTSTATIC,owner, field.name, field.desc));
        insnList.add(new JumpInsnNode(GOTO, defLabel));

        insnList.add(L3);
        insnList.add(new LdcInsnNode(new Random().nextLong()));
        insnList.add(new LdcInsnNode(new Random().nextLong()));
        insnList.add(new InsnNode(LXOR));
        insnList.add(new FieldInsnNode(PUTSTATIC,owner, field.name, field.desc));
        insnList.add(new JumpInsnNode(GOTO, defLabel));

        insnList.add(defLabel);
        return insnList;
    }
    public static InsnList getSwitchBlockString(final int key, final int varS, final String originalLDC) {
        final InsnList insnList = new InsnList();
        final LabelNode L0 = new LabelNode(),L1 = new LabelNode(),L2 = new LabelNode(),L3 = new LabelNode();
        LabelNode[] labels = {L0,L1,L2};
        int[] keys = {new Random().nextInt(), new Random().nextInt(), key};
        final LabelNode defLabel = new LabelNode();



        //insnList.add(new LdcInsnNode(getRandomInvalidString(originalLDC.length(), 1)));
        //insnList.add(new VarInsnNode(ALOAD, varS));
        insnList.add(new LookupSwitchInsnNode(L3, keys, labels));

        insnList.add(L0);
        insnList.add(new LdcInsnNode(getRandomString(originalLDC.length(), 1)));
        insnList.add(new VarInsnNode(ASTORE, varS));
        insnList.add(new JumpInsnNode(GOTO, defLabel));

        insnList.add(L1);
        insnList.add(new LdcInsnNode(getRandomString(originalLDC.length(), 1)));
        insnList.add(new VarInsnNode(ASTORE, varS));
        insnList.add(new JumpInsnNode(GOTO, defLabel));

        insnList.add(L2);
        insnList.add(new LdcInsnNode(originalLDC));
        insnList.add(new VarInsnNode(ASTORE, varS));
        insnList.add(new JumpInsnNode(GOTO, defLabel));

        insnList.add(L3);
        insnList.add(new LdcInsnNode(getRandomString(originalLDC.length(), 1)));
        insnList.add(new VarInsnNode(ASTORE, varS));
        insnList.add(new JumpInsnNode(GOTO, defLabel));

        insnList.add(defLabel);
        return insnList;
    }
}