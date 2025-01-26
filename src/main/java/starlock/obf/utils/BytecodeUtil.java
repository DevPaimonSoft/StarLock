package starlock.obf.utils;

import starlock.obf.utils.wrapper.ClassWrapper;
import starlock.obf.utils.wrapper.MethodWrapper;
import lombok.experimental.UtilityClass;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;


@UtilityClass
public final class BytecodeUtil implements Opcodes {
    private static final Random RANDOM = new Random();
    private static final Pattern ANONYM_CLASS_PATTERN = Pattern.compile(".+[$][0-9]+");

    private static final Map<String, String> BOXING = Map.of(
            "V", "java/lang/Void",
            "Z", "java/lang/Boolean",
            "B", "java/lang/Byte",
            "C", "java/lang/Character",
            "S", "java/lang/Short",
            "I", "java/lang/Integer",
            "F", "java/lang/Float",
            "J", "java/lang/Long",
            "D", "java/lang/Double"
    );

    public String box(String desc) {
        var type = Type.getType(desc);
        if (!BOXING.containsKey(type.getDescriptor()))
            return desc;
        return Type.getType("L" + BOXING.get(type.getDescriptor()) + ";").getDescriptor();
    }

    public InstructionBuilder getLoadOpcodes(Type[] args) {
        return getLoadOpcodes(0, args);
    }

    public InstructionBuilder getLoadOpcodes(int start, Type[] args) {
        var builder = InstructionBuilder.allocate();
        var idx = start;

        for (Type type : args) {
            var opcode = getLoadOpcode(type);
            builder.put(new VarInsnNode(opcode, idx++));
            if (opcode == DLOAD || opcode == LLOAD) idx++;
        }

        return builder;
    }

    public String getGenericMethodDescriptor(String desc) {
        var returnType = Type.getReturnType(desc);
        var args = Type.getArgumentTypes(desc);

        for (int i = 0; i < args.length; i++) {
            var arg = args[i];

            if (arg.getSort() == Type.OBJECT)
                args[i] = Type.getType("Ljava/lang/Object;");
        }

        return Type.getMethodDescriptor(returnType, args);
    }

    public int getLoadOpcode(Type type) {
        int opcode;

        switch (type.getSort()) {
            case Type.ARRAY, Type.OBJECT -> opcode = ALOAD;
            case Type.BOOLEAN, Type.BYTE, Type.SHORT, Type.INT, Type.CHAR -> opcode = ILOAD;
            case Type.DOUBLE -> opcode = DLOAD;
            case Type.FLOAT -> opcode = FLOAD;
            case Type.LONG -> opcode = LLOAD;
            default -> throw new RuntimeException(String.valueOf(type));
        }

        return opcode;
    }

    public int getArrayStoreOpcode(Type type) {
        int opcode;

        switch (type.getSort()) {
            case Type.OBJECT -> opcode = AASTORE;
            case Type.BYTE -> opcode = BASTORE;
            case Type.SHORT -> opcode = SASTORE;
            case Type.CHAR -> opcode = CASTORE;
            case Type.BOOLEAN, Type.INT -> opcode = IASTORE;
            case Type.DOUBLE -> opcode = DASTORE;
            case Type.FLOAT -> opcode = FASTORE;
            case Type.LONG -> opcode = LASTORE;
            default -> throw new RuntimeException(String.valueOf(type));
        }

        return opcode;
    }

    public int getReturnOpcode(Type type) {
        switch (type.getSort()) {
            case Type.ARRAY, Type.OBJECT -> {
                return ARETURN;
            }
            case Type.BOOLEAN, Type.BYTE, Type.SHORT, Type.INT, Type.CHAR -> {
                return IRETURN;
            }
            case Type.DOUBLE -> {
                return DRETURN;
            }
            case Type.FLOAT -> {
                return FRETURN;
            }
            case Type.LONG -> {
                return LRETURN;
            }
            case Type.VOID -> {
                return RETURN;
            }
            default -> throw new RuntimeException(String.valueOf(type));
        }
    }

    public AbstractInsnNode getUnWrapMethod(Type type) {
        switch (type.getDescriptor()) {
            case "Z" -> {
                return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            }
            case "B" -> {
                return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
            }
            case "I" -> {
                return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            }
            case "S" -> {
                return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
            }
            case "D" -> {
                return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            }
            case "F" -> {
                return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
            }
            case "J" -> {
                return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            }
            case "C" -> {
                return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
            }
        }

        return new InsnNode(NOP);
    }

    public String getInternalName(Type type) {
        return switch (type.toString()) {
            case "V" -> "void";
            case "Z" -> "boolean";
            case "C" -> "char";
            case "B" -> "byte";
            case "S" -> "short";
            case "I" -> "int";
            case "F" -> "float";
            case "J" -> "long";
            case "D" -> "double";
            default -> throw new IllegalArgumentException("Type not known.");
        };
    }

    public Type box(Type type) {
        if (!BOXING.containsKey(type.getDescriptor()))
            return type;
        return Type.getType("L" + BOXING.get(type.getDescriptor()) + ";");
    }

    public MethodInsnNode getBoxing(Type type) {
        return new MethodInsnNode(INVOKESTATIC, BOXING.get(type.toString()), "valueOf", "(" + type + ")" + box(type));
    }

    public static String unbox(String desc) throws IllegalArgumentException {
        var type = Type.getType(desc);
        return BOXING.entrySet().stream()
                .filter(entry -> entry.getValue().equals(type.getInternalName()))
                .map(Map.Entry::getKey)
                .map(Type::getType)
                .findFirst()
                .orElse(type).getDescriptor();
    }

    public boolean isNumber(AbstractInsnNode node) {
        return isInteger(node) || isLong(node) || isFloat(node) || isDouble(node);
    }

    public Number getNumber(AbstractInsnNode node) {
        if (isInteger(node)) return getInteger(node);
        if (isLong(node)) return getLong(node);
        if (isFloat(node)) return getFloat(node);
        if (isDouble(node)) return getDouble(node);

        throw new IllegalArgumentException();
    }

    public static boolean isInteger(AbstractInsnNode insn) {
        if (insn == null) return false;
        int opcode = insn.getOpcode();
        return ((opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5)
                || opcode == Opcodes.BIPUSH
                || opcode == Opcodes.SIPUSH
                || (insn instanceof LdcInsnNode ldcInsnNode
                && ldcInsnNode.cst instanceof Integer));
    }

    public static boolean isLong(AbstractInsnNode insn) {
        if (insn == null) return false;
        int opcode = insn.getOpcode();
        return (opcode == Opcodes.LCONST_0
                || opcode == Opcodes.LCONST_1
                || (insn instanceof LdcInsnNode ldcInsnNode
                && ldcInsnNode.cst instanceof Long));
    }

    public static boolean isFloat(AbstractInsnNode insn) {
        if (insn == null) return false;
        int opcode = insn.getOpcode();
        return (opcode >= Opcodes.FCONST_0 && opcode <= Opcodes.FCONST_2)
                || (insn instanceof LdcInsnNode ldcInsnNode && ldcInsnNode.cst instanceof Float);
    }

    public static boolean isDouble(AbstractInsnNode insn) {
        if (insn == null) return false;
        int opcode = insn.getOpcode();
        return (opcode >= Opcodes.DCONST_0 && opcode <= Opcodes.DCONST_1)
                || (insn instanceof LdcInsnNode ldcInsnNode && ldcInsnNode.cst instanceof Double);
    }

    public static boolean isString(AbstractInsnNode insn) {
        if (insn == null) return false;
        return (insn instanceof LdcInsnNode ldc && ldc.cst instanceof String);
    }

    public static String getString(AbstractInsnNode insn) {
        if (insn instanceof LdcInsnNode ldc && ldc.cst instanceof String str) return str;

        throw new IllegalArgumentException();
    }

    public int getInteger(AbstractInsnNode node) {
        var opcode = node.getOpcode();

        if (opcode >= ICONST_M1 && opcode <= ICONST_5)
            return opcode - ICONST_0;
        else if (node instanceof IntInsnNode intInsnNode && intInsnNode.getOpcode() != NEWARRAY)
            return intInsnNode.operand;
        else if (node instanceof LdcInsnNode ldcInsnNode
                && ldcInsnNode.cst instanceof Integer integerValue)
            return integerValue;

        throw new IllegalArgumentException();
    }

    public long getLong(AbstractInsnNode node) {
        var opcode = node.getOpcode();

        if (opcode >= LCONST_0 && opcode <= LCONST_1)
            return opcode - LCONST_0;
        else if (node instanceof LdcInsnNode ldcInsnNode
                && ldcInsnNode.cst instanceof Long longValue)
            return longValue;

        throw new IllegalArgumentException();
    }

    public float getFloat(AbstractInsnNode node) {
        var opcode = node.getOpcode();

        if (opcode >= FCONST_0 && opcode <= FCONST_2)
            return opcode - FCONST_0;
        else if (node instanceof LdcInsnNode ldcInsnNode
                && ldcInsnNode.cst instanceof Float floatValue)
            return floatValue;

        throw new IllegalArgumentException();
    }

    public double getDouble(AbstractInsnNode node) {
        var opcode = node.getOpcode();

        if (opcode >= DCONST_0 && opcode <= DCONST_1)
            return opcode - DCONST_0;
        else if (node instanceof LdcInsnNode ldcInsnNode
                && ldcInsnNode.cst instanceof Double doubleValue)
            return doubleValue;

        throw new IllegalArgumentException();
    }

    public AbstractInsnNode getOptimizedNumber(Number number) {
        if (number instanceof Integer integerValue)
            return getOptimizedInteger(integerValue);
        else if (number instanceof Long longValue)
            return getOptimizedLong(longValue);
        else if (number instanceof Float floatValue)
            return getOptimizedFloat(floatValue);
        else if (number instanceof Double doubleValue)
            return getOptimizedDouble(doubleValue);

        throw new IllegalArgumentException();
    }

    public AbstractInsnNode getOptimizedInteger(int integerValue) {
        if (integerValue >= -1 && integerValue <= 5)
            return new InsnNode(integerValue + ICONST_0);
        else if (integerValue >= Byte.MIN_VALUE && integerValue <= Byte.MAX_VALUE)
            return new IntInsnNode(BIPUSH, integerValue);
        else if (integerValue >= Short.MIN_VALUE && integerValue <= Short.MAX_VALUE)
            return new IntInsnNode(SIPUSH, integerValue);
        else
            return new LdcInsnNode(integerValue);
    }

    public AbstractInsnNode getOptimizedLong(long longValue) {
        if (longValue == 0 || longValue == 1)
            return new InsnNode((int) longValue + LCONST_0);
        else
            return new LdcInsnNode(longValue);
    }

    public AbstractInsnNode getOptimizedFloat(float floatValue) {
        if (floatValue >= 0 && floatValue <= 2)
            return new InsnNode((int) floatValue + FCONST_0);
        else
            return new LdcInsnNode(floatValue);
    }

    public AbstractInsnNode getOptimizedDouble(double doubleValue) {
        if (doubleValue == 0 || doubleValue == 1)
            return new InsnNode((int) doubleValue + DCONST_0);
        else
            return new LdcInsnNode(doubleValue);
    }

    public static boolean isInvoke(AbstractInsnNode node, boolean invokeDynamic) {
        var opcode = node.getOpcode();
        return (opcode >= INVOKEVIRTUAL) && (invokeDynamic ? opcode <= INVOKEDYNAMIC : opcode < INVOKEDYNAMIC);
    }


    public boolean isOpcodeValid(int opcode) {
        return opcode == -1 || opcode >= NOP && opcode <= ALOAD || opcode >= IALOAD && opcode <= ASTORE || opcode >= IASTORE && opcode <= IFNONNULL;
    }

    public boolean isOpcodeValid(AbstractInsnNode instruction) {
        return isOpcodeValid(instruction.getOpcode());
    }

    public String getPackage(String in) {
        int lin = in.lastIndexOf('/');

        if (lin == 0) throw new IllegalArgumentException("Illegal class name");

        return lin == -1 ? "" : in.substring(0, lin);
    }

    public String getPackage(ClassWrapper wrapper) {
        var in = wrapper.getName();
        int lin = in.lastIndexOf('/');

        if (lin == 0) throw new IllegalArgumentException("Illegal class name");

        return lin == -1 ? "" : in.substring(0, lin);
    }

    public boolean isInnerClass(ClassWrapper node, ClassWrapper of) {
        return node.getName().equals(of.getName());
    }


    public static boolean isDefaultInvoke(AbstractInsnNode node) {
        var opcode = node.getOpcode();
        return (opcode == INVOKESTATIC || opcode == INVOKEVIRTUAL);
    }

    public static boolean isStaticInvoke(AbstractInsnNode node) {
        return (node.getOpcode() == INVOKESTATIC);
    }

    public static boolean isInvoke(AbstractInsnNode node) {
        return isInvoke(node, false);
    }

    public static boolean isField(AbstractInsnNode node) {
        var opcode = node.getOpcode();
        return (opcode >= GETSTATIC) && (opcode <= PUTFIELD);
    }

    public static boolean isStaticField(AbstractInsnNode node) {
        var opcode = node.getOpcode();
        return (opcode >= GETSTATIC) && (opcode <= PUTSTATIC);
    }

    public boolean isJump(AbstractInsnNode node) {
        return node instanceof JumpInsnNode;
    }

    public boolean isOperator(AbstractInsnNode node) {
        var opcode = node.getOpcode();
        return (opcode >= IADD && opcode <= IINC);
    }

    public static boolean isNull(AbstractInsnNode insn) {
        return insn.getOpcode() == ACONST_NULL;
    }

    public InsnList asList(AbstractInsnNode... insns) {
        var insnList = new InsnList();
        Arrays.stream(insns).forEach(insnList::add);
        return insnList;
    }

    public static MethodInsnNode getInvoke(MethodWrapper wrapper) {
        int opcode = Opcodes.INVOKEVIRTUAL;

        if (Modifier.isInterface(wrapper.getOwner().getAccess()))
            opcode = Opcodes.INVOKEINTERFACE;
        else if (Modifier.isStatic(wrapper.getAccess()))
            opcode = Opcodes.INVOKESTATIC;
        else if (wrapper.isInitializer())
            opcode = Opcodes.INVOKESPECIAL;

        return new MethodInsnNode(opcode, wrapper.getOwnerName(), wrapper.getName(), wrapper.getDescriptor(), false);
    }

    public InsnList getJump(LabelNode label) {
        InsnList list = new InsnList();

        int firstInteger = RANDOM.nextInt(Integer.MIN_VALUE, -1);
        int secondInteger = RANDOM.nextInt(1, Integer.MAX_VALUE);

        long firstLong = RANDOM.nextLong(Long.MIN_VALUE, -1);
        long secondLong = RANDOM.nextLong(1, Long.MAX_VALUE);

        float firstFloat = -RANDOM.nextFloat();
        float secondFloat = RANDOM.nextFloat();

        double firstDouble = -RANDOM.nextDouble();
        double secondDouble = RANDOM.nextDouble();

        switch (RANDOM.nextInt(10)) {
            case 0 -> {
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(INEG));
                list.add(new InsnNode(ICONST_2));
                list.add(new InsnNode(IADD));
                list.add(new JumpInsnNode(IFNE, label));
            }
            case 1 -> {
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(INEG));
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(IADD));
                list.add(new JumpInsnNode(IFEQ, label));
            }
            case 2 -> {
                list.add(getOptimizedNumber(firstInteger));
                list.add(getOptimizedNumber(secondInteger));
                list.add(new JumpInsnNode(IF_ICMPLT, label));
            }
            case 3 -> {
                list.add(getOptimizedNumber(secondInteger));
                list.add(getOptimizedNumber(firstInteger));
                list.add(new JumpInsnNode(IF_ICMPGE, label));
            }
            case 4 -> {
                list.add(getOptimizedNumber(firstLong));
                list.add(getOptimizedNumber(secondLong));
                list.add(new InsnNode(LCMP));
                list.add(new JumpInsnNode(IFLE, label));
            }
            case 5 -> {
                list.add(getOptimizedNumber(secondLong));
                list.add(getOptimizedNumber(firstLong));
                list.add(new InsnNode(LCMP));
                list.add(new JumpInsnNode(IFGE, label));
            }
            case 6 -> {
                list.add(getOptimizedNumber(firstFloat));
                list.add(getOptimizedNumber(secondFloat));
                list.add(new InsnNode(FCMPG));
                list.add(new JumpInsnNode(IFLE, label));
            }
            case 7 -> {
                list.add(getOptimizedNumber(secondFloat));
                list.add(getOptimizedNumber(firstFloat));
                list.add(new InsnNode(FCMPL));
                list.add(new JumpInsnNode(IFGE, label));
            }
            case 8 -> {
                list.add(getOptimizedNumber(firstDouble));
                list.add(getOptimizedNumber(secondDouble));
                list.add(new InsnNode(DCMPG));
                list.add(new JumpInsnNode(IFLE, label));
            }
            default -> {
                list.add(getOptimizedNumber(secondDouble));
                list.add(getOptimizedNumber(firstDouble));
                list.add(new InsnNode(DCMPL));
                list.add(new JumpInsnNode(IFGE, label));
            }
        }

        return list;
    }

    public InstructionBuilder getExit(int exitCode) {
        switch (RANDOM.nextInt(1, 4)) {
            case 1 -> {
                return InstructionBuilder.allocate()
                        .put(getOptimizedInteger(exitCode))
                        .put(new MethodInsnNode(INVOKESTATIC, "java/lang/System", "exit", "(I)V", false));
            }
            case 2 -> {
                return InstructionBuilder.allocate()
                        .put(new MethodInsnNode(INVOKESTATIC, "java/lang/Runtime", "getRuntime()", "()Ljava/lang/Runtime;", false))
                        .put(getOptimizedInteger(exitCode))
                        .put(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Runtime", "exit", "(I)V", false));
            }
            default -> {
                return InstructionBuilder.allocate()
                        .put(new MethodInsnNode(INVOKESTATIC, "java/lang/Runtime", "getRuntime()", "()Ljava/lang/Runtime;", false))
                        .put(getOptimizedInteger(exitCode))
                        .put(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Runtime", "halt", "(I)V", false));
            }
        }
    }

    public boolean isLoad(int opcode) {
        return opcode >= ILOAD && opcode <= ALOAD;
    }

    public boolean isStore(int opcode) {
        return opcode >= ISTORE && opcode <= ASTORE;
    }

    public boolean isStoreNoDoubled(int opcode) {
        return isStore(opcode) && opcode != LSTORE && opcode != DSTORE;
    }

    public InsnList getNotJump(LabelNode label) {
        InsnList list = new InsnList();

        int firstInteger = RANDOM.nextInt(1, Integer.MAX_VALUE);
        int secondInteger = RANDOM.nextInt(Integer.MIN_VALUE, -1);

        long firstLong = RANDOM.nextLong(1, Long.MAX_VALUE);
        long secondLong = RANDOM.nextLong(Long.MIN_VALUE, -1);

        float firstFloat = RANDOM.nextFloat();
        float secondFloat = -RANDOM.nextFloat();

        double firstDouble = RANDOM.nextDouble();
        double secondDouble = -RANDOM.nextDouble();

        switch (RANDOM.nextInt(10)) {
            case 0 -> {
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(INEG));
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(IADD));
                list.add(new JumpInsnNode(IFNE, label));
            }
            case 1 -> {
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(INEG));
                list.add(new InsnNode(ICONST_0));
                list.add(new InsnNode(IADD));
                list.add(new JumpInsnNode(IFEQ, label));
            }
            case 2 -> {
                list.add(getOptimizedNumber(firstInteger));
                list.add(getOptimizedNumber(secondInteger));
                list.add(new JumpInsnNode(IF_ICMPLT, label));
            }
            case 3 -> {
                list.add(getOptimizedNumber(secondInteger));
                list.add(getOptimizedNumber(firstInteger));
                list.add(new JumpInsnNode(IF_ICMPGE, label));
            }
            case 4 -> {
                list.add(getOptimizedNumber(firstLong));
                list.add(getOptimizedNumber(secondLong));
                list.add(new InsnNode(LCMP));
                list.add(new JumpInsnNode(IFLE, label));
            }
            case 5 -> {
                list.add(getOptimizedNumber(secondLong));
                list.add(getOptimizedNumber(firstLong));
                list.add(new InsnNode(LCMP));
                list.add(new JumpInsnNode(IFGE, label));
            }
            case 6 -> {
                list.add(getOptimizedNumber(firstFloat));
                list.add(getOptimizedNumber(secondFloat));
                list.add(new InsnNode(FCMPG));
                list.add(new JumpInsnNode(IFLE, label));
            }
            case 7 -> {
                list.add(getOptimizedNumber(secondFloat));
                list.add(getOptimizedNumber(firstFloat));
                list.add(new InsnNode(FCMPL));
                list.add(new JumpInsnNode(IFGE, label));
            }
            case 8 -> {
                list.add(getOptimizedNumber(firstDouble));
                list.add(getOptimizedNumber(secondDouble));
                list.add(new InsnNode(DCMPG));
                list.add(new JumpInsnNode(IFLE, label));
            }
            default -> {
                list.add(getOptimizedNumber(secondDouble));
                list.add(getOptimizedNumber(firstDouble));
                list.add(new InsnNode(DCMPL));
                list.add(new JumpInsnNode(IFGE, label));
            }
        }

        return list;
    }


    public static int randomModifiers(int... modifiers) {
        return modifiers[RANDOM.nextInt(modifiers.length)];
    }

    public static int randomAccess() {
        return randomModifiers(ACC_PUBLIC, ACC_PROTECTED, ACC_PRIVATE, 0);
    }

    public InstructionBuilder getParameters(Type returnType, Type[] parameters) {
        InstructionBuilder builder = InstructionBuilder.allocate();
        builder.put(getTypeAsInstruction(returnType));
        for (Type parameter : parameters)
            builder.put(getTypeAsInstruction(parameter));
        return builder;
    }

    public AbstractInsnNode getTypeAsInstruction(Type type) {
        if (type.getSort() == Type.OBJECT)
            return new LdcInsnNode(Type.getType("L" + type.getClassName().replace('.', '/') + ";"));
        return new FieldInsnNode(GETSTATIC, BOXING.get(type.toString()), "TYPE", "Ljava/lang/Class;");
    }

    public InstructionBuilder getRandomObject() {
        InstructionBuilder instructionBuilder = InstructionBuilder.allocate();

        switch (RANDOM.nextInt(0, 3)) {
            case 0 -> instructionBuilder.put(getOptimizedInteger(RANDOM.nextInt()));
            case 1 -> instructionBuilder.put(getOptimizedFloat(RANDOM.nextFloat()));
            default -> instructionBuilder
                    .put(new TypeInsnNode(NEW, "java/lang/Object"))
                    .put(DUP)
                    .put(new MethodInsnNode(INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
        }

        return instructionBuilder;
    }

    public boolean containsAccess(int accessCode, int access) {
        return removeAccess(accessCode, access) != 0;
    }

    public int addAccess(int accessCode, int access) {
        return accessCode | access;
    }

    public int removeAccess(int accessCode, int access) {
        return accessCode & access;
    }

    public static String toSourceFile(String name) {
        var split = name.split("/");
        return split[split.length - 1] + ".java";
    }

    public static int getEmptyElementFromStore(int storeOpcode) {
        switch (storeOpcode) {
            case ASTORE -> {
                return ACONST_NULL;
            }
            case ISTORE -> {
                return ICONST_0;
            }
            case FSTORE -> {
                return FCONST_0;
            }
            case DSTORE -> {
                return DCONST_0;
            }
            case LSTORE -> {
                return LCONST_0;
            }
        }

        throw new IllegalArgumentException();
    }

    public static AbstractInsnNode pushInt(int value) {
        if (value >= -1 && value <= 5) {
            return new InsnNode(Opcodes.ICONST_0 + value);
        }
        if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            return new IntInsnNode(Opcodes.BIPUSH, value);
        }
        if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            return new IntInsnNode(Opcodes.SIPUSH, value);
        }
        return new LdcInsnNode(value);
    }


    public static long getAmountOf(int opcode, MethodNode mn) {
        return Arrays.stream(mn.instructions.toArray()).filter(insn -> insn.getOpcode() == opcode).count();
    }

    public static long getAmountOf(int opcode, ClassNode cn) {
        int amount = 0;
        for (MethodNode mn : cn.methods) {
            amount += Arrays.stream(mn.instructions.toArray()).filter(insn -> insn.getOpcode() == opcode).count();
        }
        return amount;
    }

    public List<ClassWrapper> getSuperHierarchy(ClassWrapper base) {
        var superList = new ArrayList<ClassWrapper>();
        return superList;
    }
}
