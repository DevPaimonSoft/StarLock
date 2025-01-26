package starlock.obf.utils.reader;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.util.Objects;

public class JavaClassWriter extends ClassWriter {
    public JavaClassWriter(int flags) {
        super(flags);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        if (Objects.equals(type1, type2))
            return type1;

        try {
            return super.getCommonSuperClass(type1, type2);
        } catch (TypeNotPresentException e) {
            return Type.getInternalName(Object.class);
        }
    }
}
