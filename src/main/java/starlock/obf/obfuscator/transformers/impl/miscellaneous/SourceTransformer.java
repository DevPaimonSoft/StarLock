package starlock.obf.obfuscator.transformers.impl.miscellaneous;

import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.MiscellaneousTransformer;

import java.util.Random;

public class SourceTransformer extends MiscellaneousTransformer {
    public void init(Obfuscator obfuscator){
        if(getConfig().getBoolean("Miscellaneous.Source.Remove")){
            obfuscator.getClasses().forEach(classNode -> {
                classNode.sourceDebug = null;
                classNode.sourceFile = null;
            });
        } else {
            obfuscator.getClasses().forEach(classNode -> {
                classNode.sourceDebug = getRandomString(new Random().nextInt(256),new Random().nextInt(3));
                classNode.sourceFile = getRandomString(new Random().nextInt(256),new Random().nextInt(3));
            });
        }
    }
}
