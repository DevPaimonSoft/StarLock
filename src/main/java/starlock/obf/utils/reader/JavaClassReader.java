package starlock.obf.utils.reader;

import org.objectweb.asm.ClassReader;

public final class JavaClassReader extends ClassReader {
    public JavaClassReader(byte[] classFile) {
        super(classFile);
    }
}