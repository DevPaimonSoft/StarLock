package starlock.obf.obfuscator;

import org.objectweb.asm.Opcodes;
import starlock.obf.utils.ASMHelper;

public abstract class Transformer extends ASMHelper implements Opcodes {
    public abstract void transform(Obfuscator obfuscator);
    public String name() {
        return this.getClass().getSimpleName();
    }
}
