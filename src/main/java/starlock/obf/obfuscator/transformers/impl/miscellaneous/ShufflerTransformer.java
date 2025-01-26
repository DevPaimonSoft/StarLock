package starlock.obf.obfuscator.transformers.impl.miscellaneous;

import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.MiscellaneousTransformer;

import java.util.Collections;

public class ShufflerTransformer extends MiscellaneousTransformer {

    public void init(Obfuscator obfuscator){
        obfuscator.getClasses().forEach(classNode -> {
            Collections.shuffle(classNode.methods);
            Collections.shuffle(classNode.fields);
        });
    }
}