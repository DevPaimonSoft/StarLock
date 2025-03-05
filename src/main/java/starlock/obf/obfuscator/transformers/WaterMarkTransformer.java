package starlock.obf.obfuscator.transformers;

import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.watermark.ConsoleTransformer;
import starlock.obf.obfuscator.transformers.impl.watermark.MetaInfTransformer;
import starlock.obf.obfuscator.transformers.impl.watermark.WClassTransformer;

public class WaterMarkTransformer extends Transformer {
    @Override
    public void transform(ClassNode classnode) {
        if(getConfig().getBoolean("Watermark.META-INF")){
            new MetaInfTransformer().obfuscate(classnode);
        }
        //if(getConfig().getBoolean("Watermark.META-INF")){
        //    new MetaInfTransformer().obfuscate(classnode);
        //}
        new ConsoleTransformer().obfuscate(classnode);
        //new WClassTransformer().obfuscate(classnode);
    }
}
