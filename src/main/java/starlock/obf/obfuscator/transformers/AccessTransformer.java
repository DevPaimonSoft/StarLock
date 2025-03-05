package starlock.obf.obfuscator.transformers;

import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.access.ClassAccessTransformer;
import starlock.obf.obfuscator.transformers.impl.access.FieldAccessTransformer;
import starlock.obf.obfuscator.transformers.impl.access.MethodAccessTransformer;

public class AccessTransformer extends Transformer {
    @Override
    public void transform(ClassNode classnode) {
        new MethodAccessTransformer().obfuscate(classnode);
        new FieldAccessTransformer().obfuscate(classnode);
        new ClassAccessTransformer().obfuscate(classnode);
    }
}