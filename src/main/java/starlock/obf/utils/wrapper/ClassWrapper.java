package starlock.obf.utils.wrapper;

import starlock.obf.utils.reader.JavaClassReader;
import starlock.obf.utils.reader.JavaClassWriter;
import lombok.Getter;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;


public final class ClassWrapper {
    @Getter
    private final ClassNode classNode;

    public ClassWrapper(byte[] buffer) {
        var classNode = new ClassNode();
        var classReader = new JavaClassReader(buffer);
        classReader.accept(classNode, EXPAND_FRAMES);
        this.classNode = classNode;
    }

    public ClassWrapper(ClassNode classNode) {
        this.classNode = classNode;
    }

    public ClassWrapper(int version, int access, String name, String superName) {
        ClassNode node = new ClassNode();
        node.visit(version, access, name, null, superName, null);
        this.classNode = node;
    }

    public ClassWrapper(int version, int access, String name) {
        this(version, access, name, null);
    }

    public ClassWrapper(int access, String name) {
        this(V1_8, access, name, null);
    }

    public int getAccess() {
        return classNode.access;
    }

    public void setAccess(int access) {
        classNode.access = access;
    }

    public String getOuterClass() {
        return classNode.outerClass;
    }

    public void shuffleItems() {
        Collections.shuffle(classNode.fields);
        Collections.shuffle(classNode.methods);
    }

    public boolean isEnum() {
        return getSuperName() != null && getSuperName().equals("java/lang/Enum");
    }

    public void setOuterClass(String outerClass) {
        classNode.outerClass = outerClass;
    }

    public String getOuterMethod() {
        return classNode.outerMethod;
    }

    public void setOuterMethod(String outerMethod) {
        classNode.outerMethod = outerMethod;
    }

    public String getOuterMethodDescriptor() {
        return classNode.outerMethodDesc;
    }

    public void setOuterMethodDescriptor(String outerMethodDescriptor) {
        classNode.outerMethodDesc = outerMethodDescriptor;
    }

    public List<AnnotationNode> getInvisibleAnnotations() {
        if (classNode.invisibleAnnotations == null)
            classNode.invisibleAnnotations = new ArrayList<>();
        return classNode.invisibleAnnotations;
    }

    public List<AnnotationNode> getVisibleAnnotations() {
        if (classNode.visibleAnnotations == null)
            classNode.visibleAnnotations = new ArrayList<>();
        return classNode.visibleAnnotations;
    }

    public List<TypeAnnotationNode> getInvisibleTypeAnnotations() {
        if (classNode.invisibleTypeAnnotations == null)
            classNode.invisibleTypeAnnotations = new ArrayList<>();
        return classNode.invisibleTypeAnnotations;
    }

    public List<TypeAnnotationNode> getVisibleTypeAnnotations() {
        if (classNode.visibleTypeAnnotations == null)
            classNode.visibleTypeAnnotations = new ArrayList<>();
        return classNode.visibleTypeAnnotations;
    }

    public String getSourceFile() {
        return classNode.sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        classNode.sourceFile = sourceFile;
    }

    public String getSourceDebug() {
        return classNode.sourceDebug;
    }

    public void setSourceDebug(String sourceDebug) {
        classNode.sourceDebug = sourceDebug;
    }

    public String getName() {
        return classNode.name;
    }

    public String getSuperName() {
        return classNode.superName;
    }

    public int getVersion() {
        return classNode.version;
    }

    public void setVersion(int version) {
        classNode.version = version;
    }

    public boolean isModifiable() {
        return (classNode.access & ACC_INTERFACE) == 0;
    }

    public MethodWrapper getClinit() {
        var clinit = findMethod("<clinit>", "()V");
        if (clinit == null) {
            clinit = new MethodWrapper(this, new MethodNode(ACC_STATIC, "<clinit>", "()V", null, null));
            clinit.getInstructions().add(new InsnNode(RETURN));
            classNode.methods.add(clinit.getMethodNode());
        } return clinit;
    }

    public FieldNode addField(FieldNode fieldNode) {
        classNode.fields.add(fieldNode);
        return fieldNode;
    }

    public FieldWrapper addField(FieldWrapper fieldWrapper) {
        classNode.fields.add(fieldWrapper.getFieldNode());
        return fieldWrapper;
    }

    public FieldWrapper addField(int access, String name, String descriptor) {
        return addField(new FieldWrapper(this, new FieldNode(access, name, descriptor, null, null)));
    }

    public MethodWrapper addMethod(int access, String name, String descriptor) {
        return addMethod(new MethodWrapper(this, new MethodNode(access, name, descriptor, null, null)));
    }

    public MethodWrapper addMethod(int access, String name, String descriptor, String... exceptions) {
        return addMethod(new MethodWrapper(this, new MethodNode(access, name, descriptor, null, exceptions)));
    }

    public MethodNode addMethod(MethodNode methodNode) {
        classNode.methods.add(methodNode);
        return methodNode;
    }

    public MethodWrapper addMethod(MethodWrapper methodWrapper) {
        classNode.methods.add(methodWrapper.getMethodNode());
        return methodWrapper;
    }

    public MethodWrapper findMethod(String name, String desc) {
        return this.getMethods().stream().filter(method -> method.getName().equals(name) && method.getDescriptor().equals(desc)).findFirst().orElse(null);
    }

    public List<MethodNode> getMethodsAsNodes() {
        return classNode.methods;
    }

    public List<MethodWrapper> getMethods() {
        var methods = new ArrayList<MethodWrapper>();
        for (var method : classNode.methods)
            methods.add(new MethodWrapper(this, method));
        return methods;
    }

    public List<FieldWrapper> getFields() {
        var fields = new ArrayList<FieldWrapper>();
        for (var field : classNode.fields)
            fields.add(new FieldWrapper(this, field));
        return fields;
    }

    public FieldWrapper getFieldOrNull(String name, String desc) {
        return getFields().stream().filter(field -> field.getName().equals(name) && field.getDescriptor().equals(desc)).findAny().orElse(null);
    }

    public FieldWrapper getFieldOrNull(FieldInsnNode call) {
        return getFieldOrNull(call.name, call.desc);
    }

    public MethodWrapper getMethodOrNull(String name, String desc) {
        return getMethods().stream().filter(method -> method.getName().equals(name) && method.getDescriptor().equals(desc)).findAny().orElse(null);
    }

    public MethodWrapper getMethodOrNull(MethodInsnNode call) {
        return getMethodOrNull(call.name, call.desc);
    }

    public void removeUnknownAttributes() {
        if (classNode.attrs != null)
            classNode.attrs.removeIf(Attribute::isUnknown);
    }

    public void removeInnerClasses() {
        if (classNode.innerClasses != null)
            classNode.innerClasses.clear();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public void setSignature(String signature) {
        classNode.signature = signature;
    }

    public void setNestHost(String nestHost) {
        classNode.nestHostClass = nestHost;
    }

    public String getNestHost() {
        return classNode.nestHostClass;
    }

    public List<String> getNestMembers() {
        return classNode.nestMembers;
    }

    public byte[] write() {
        if (classNode.version > 49) {
            return getBaseWriter(COMPUTE_MAXS | COMPUTE_FRAMES).toByteArray();
        } else {
            return getBaseWriter(COMPUTE_MAXS).toByteArray();
        }
    }

    public JavaClassWriter getBaseWriter(int flag) {

        var classWriter = new JavaClassWriter(flag);
        classNode.accept(classWriter);
        return classWriter;
    }


    public List<FieldNode> getFieldsAsNodes() {
        return classNode.fields;
    }

    public void setSuperName(String superName) {
        classNode.superName = superName;
    }

    public List<InnerClassNode> getInnerClasses() {
        return classNode.innerClasses;
    }

    public List<Attribute> getAttributes() {
        if (classNode.attrs == null)
            classNode.attrs = new ArrayList<>();
        return classNode.attrs;
    }
}
