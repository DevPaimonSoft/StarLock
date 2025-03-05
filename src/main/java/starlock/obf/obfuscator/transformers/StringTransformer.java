package starlock.obf.obfuscator.transformers;

import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.string.HeavyStringTransformer;

import java.util.Random;

public class StringTransformer  extends Transformer {
    public static int key = (new Random()).nextInt();
    @Override
    public void transform(ClassNode classnode) {
        switch (getConfig().getString("StringEncryption.Mode")){
            case "Normal" -> new HeavyStringTransformer().obfuscate(classnode);
            case "Heavy" -> new HeavyStringTransformer().obfuscate(classnode);
            default -> throw new IllegalArgumentException();
        }
    }
}