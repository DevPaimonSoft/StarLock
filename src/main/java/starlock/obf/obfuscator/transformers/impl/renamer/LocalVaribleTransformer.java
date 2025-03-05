package starlock.obf.obfuscator.transformers.impl.renamer;

import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.RenamerTransformer;

import java.util.Random;

public class LocalVaribleTransformer extends RenamerTransformer {

    public void obfuscate(ClassNode classNode){
        if(getConfig().getBoolean("Renamer.LocalVariables.Remove")){
            classNode.methods.forEach(methodNode -> methodNode.localVariables = null);
        } else {
            classNode.methods.forEach(methodNode -> {
                if(methodNode.localVariables != null){
                    methodNode.localVariables.forEach(var ->
                            var.name = getRandomString(new Random().nextInt(50,300),new Random().nextInt(1,3)));
                }
            });
        }
    }
}
