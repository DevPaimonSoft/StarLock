package starlock.obf.obfuscator.transformers.impl.miscellaneous;

import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.MiscellaneousTransformer;

import java.util.Random;

public class SourceTransformer extends MiscellaneousTransformer {
    public void obfuscate(ClassNode classNode){
        if(getConfig().getBoolean("Miscellaneous.Source.Remove")){
            classNode.sourceDebug = null;
            classNode.sourceFile = null;
        } else {
            classNode.sourceDebug = getRandomString(new Random().nextInt(256),new Random().nextInt(3));
            classNode.sourceFile = getRandomString(new Random().nextInt(256),new Random().nextInt(3));
        }
    }
}
