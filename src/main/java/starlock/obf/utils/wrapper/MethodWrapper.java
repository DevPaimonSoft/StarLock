package starlock.obf.utils.wrapper;

import lombok.Getter;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_STATIC;


public final class MethodWrapper {
    @Getter
    private final MethodNode methodNode;
    @Getter
    private final ClassWrapper owner;

    public MethodWrapper(ClassWrapper owner, MethodNode methodNode) {
        this.owner = owner;
        this.methodNode = methodNode;
    }

    public int getAccess() {
        return methodNode.access;
    }

    public void setAccess(int access) {
        methodNode.access = access;
    }

    public int getMaxLocals() {
        return methodNode.maxLocals;
    }

    public void setMaxLocals(int maxLocals) {
        methodNode.maxLocals = maxLocals;
    }

    public int incrementAndGetMaxLocals() {
        return ++methodNode.maxLocals;
    }

    public int getAndIncrementMaxLocals() {
        return methodNode.maxLocals++;
    }

    public int addAndGetMaxLocals(int add) {
        return methodNode.maxLocals += add;
    }

    public int subAndGetMaxLocals(int sub) {
        return methodNode.maxLocals -= sub;
    }

    public void removeVariables() {
        if (methodNode.localVariables != null)
            methodNode.localVariables.clear();
    }

    public String getSignature() {
        return methodNode.signature;
    }

    public void setSignature(String signature) {
        methodNode.signature = signature;
    }

    public void removeUnknownAttributes() {
        if (methodNode.attrs != null)
            methodNode.attrs.removeIf(Attribute::isUnknown);
    }

    public String getDescriptor() {
        return methodNode.desc;
    }

    public String getName() {
        return methodNode.name;
    }

    public List<TryCatchBlockNode> getTryCatchBlocks() {
        return methodNode.tryCatchBlocks;
    }

    public String getReturnType() {
        return Type.getReturnType(methodNode.desc).getDescriptor();
    }

    public String getArgumentsType() {
        var builder = new StringBuilder();
        var args = Type.getArgumentTypes(methodNode.desc);
        for (Type arg : args)
            builder.append(arg.getDescriptor());
        return builder.toString();
    }

    public Stream<AbstractInsnNode> getInstructionStream() {
        return Arrays.stream(methodNode.instructions.toArray());
    }

    public InsnList getInstructions() {
        return methodNode.instructions;
    }

    public boolean isModifiable() {
        return owner.isModifiable() && (methodNode.access & ACC_ABSTRACT) == 0;
    }

    public boolean isClinit() {
        return getName().equals("<clinit>") && getDescriptor().equals("()V");
    }

    public boolean isInitializer() {
        return this.getName().startsWith("<");
    }

    public List<ParameterNode> getParameters() {
        return methodNode.parameters;
    }

    public String getOwnerName() {
        return getOwner().getName();
    }

    @Override
    public String toString() {
        return this.getOwnerName() + "." + this.getName() + " " + this.getDescriptor();
    }

    public boolean equals(MethodWrapper obj) {
        return this.getMethodNode() == obj.getMethodNode();
    }

    public List<String> getExceptions() {
        return methodNode.exceptions;
    }

    public String[] getExceptionsAsArray() {
        return methodNode.exceptions.toArray(new String[0]);
    }

    public List<Attribute> getAttributes() {
        if (methodNode.attrs == null)
            methodNode.attrs = new ArrayList<>();
        return methodNode.attrs;
    }

    public Object getAnnotationDefault() {
        return methodNode.annotationDefault;
    }

    public boolean isStatic() {
        return (getAccess() & ACC_STATIC) != 0;
    }
}
