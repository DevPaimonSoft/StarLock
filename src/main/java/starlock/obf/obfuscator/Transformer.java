package starlock.obf.obfuscator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import starlock.obf.utils.ASMHelper;

public abstract class Transformer extends ASMHelper implements Opcodes {
    protected boolean SLClass = true;//getConfig().getBoolean("Settings.StarLockClass");
    protected boolean watermarkInjection = false;
    protected String method4Decrypt = getRandomName(64,1);
    public abstract void transform(ClassNode classNode);
    public String name() {
        return this.getClass().getSimpleName();
    }
}
