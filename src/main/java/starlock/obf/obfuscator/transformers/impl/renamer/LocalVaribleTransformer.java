package starlock.obf.obfuscator.transformers.impl.renamer;

import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.RenamerTransformer;

import java.util.Random;

public class LocalVaribleTransformer extends RenamerTransformer {

    public void obfuscate(Obfuscator obfuscator){
        if(getConfig().getBoolean("Renamer.LocalVariables.Remove")){
            obfuscator.getClasses().forEach(classNode ->
                        classNode.methods.forEach(methodNode -> methodNode.localVariables = null));
        } else {
            obfuscator.getClasses().forEach(classNode -> {
                        classNode.methods.forEach(methodNode -> {
                            if(methodNode.localVariables != null){
                                methodNode.localVariables.forEach(var
                                        -> var.name = getRandomString(new Random().nextInt(50,300),new Random().nextInt(1,3)));
                            }
                        });
                    });
        }
    }
}
