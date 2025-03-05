package starlock.obf.obfuscator.transformers.impl.miscellaneous;

import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.MiscellaneousTransformer;

import java.util.Collections;

public class ShufflerTransformer extends MiscellaneousTransformer {

    public void obfuscate(ClassNode classNode) {
        Collections.shuffle(classNode.methods);
        Collections.shuffle(classNode.fields);
    }
}