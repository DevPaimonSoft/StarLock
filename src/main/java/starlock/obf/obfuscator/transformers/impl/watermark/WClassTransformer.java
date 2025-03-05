package starlock.obf.obfuscator.transformers.impl.watermark;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import starlock.obf.manager.StarLockManager;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.WaterMarkTransformer;

public class WClassTransformer extends WaterMarkTransformer {
    public void obfuscate(ClassNode classNode){
        Obfuscator obfuscator = new Obfuscator();
        ClassWriter cw = new ClassWriter(0);
        cw.newUTF8("StarLock_" + StarLockManager.VERSION);
        classNode.accept(cw);
        obfuscator.setClass(classNode.name+".class",cw.toByteArray());
    }
}
