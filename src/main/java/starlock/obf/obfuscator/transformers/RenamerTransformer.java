package starlock.obf.obfuscator.transformers;

import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.renamer.ClassTransformer;
import starlock.obf.obfuscator.transformers.impl.renamer.LocalVaribleTransformer;

public class RenamerTransformer extends Transformer {
    @Override
    public void transform(ClassNode classnode) {
        new LocalVaribleTransformer().obfuscate(classnode);
        //new ClassTransformer().obfuscate(classnode);
    }
}