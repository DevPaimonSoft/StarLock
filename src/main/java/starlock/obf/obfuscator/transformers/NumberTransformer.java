package starlock.obf.obfuscator.transformers;

import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.number.HeavyNumberTransformer;

import java.util.Random;

public class NumberTransformer extends Transformer {
    public static int key = (new Random()).nextInt();
    @Override
    public void transform(ClassNode classnode) {
        switch (getConfig().getString("NumberObfuscation.Mode")){
            case "Normal" -> new HeavyNumberTransformer().obfuscate(classnode);
            case "Heavy" -> new HeavyNumberTransformer().obfuscate(classnode);
            default -> throw new IllegalArgumentException();
        }
    }
}
