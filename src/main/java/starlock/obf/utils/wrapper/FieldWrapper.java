package starlock.obf.utils.wrapper;

import lombok.Getter;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;
import java.util.List;


public final class FieldWrapper {
    @Getter
    private final FieldNode fieldNode;
    @Getter
    private final ClassWrapper owner;

    public FieldWrapper(ClassWrapper owner, FieldNode fieldNode) {
        this.owner = owner;
        this.fieldNode = fieldNode;
    }

    public int getAccess() {
        return fieldNode.access;
    }

    public void setAccess(int access) {
        fieldNode.access = access;
    }

    public String getSignature() {
        return fieldNode.signature;
    }

    public void setSignature(String signature) {
        fieldNode.signature = signature;
    }

    public void removeUnknownAttributes() {
        if (fieldNode.attrs != null)
            fieldNode.attrs.removeIf(Attribute::isUnknown);
    }

    public String getDescriptor() {
        return fieldNode.desc;
    }

    public String getName() {
        return fieldNode.name;
    }

    public String getOwnerName() {
        return getOwner().getName();
    }

    @Override
    public String toString() {
        return this.getOwnerName() + "." + this.getName() + " " + this.getDescriptor();
    }

    public List<Attribute> getAttributes() {
        if (fieldNode.attrs == null)
            fieldNode.attrs = new ArrayList<>();
        return fieldNode.attrs;
    }
}

