package starlock.obf.obfuscator.transformers;

import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.watermark.ConsoleTransformer;
import starlock.obf.obfuscator.transformers.impl.watermark.InvokeDynamicWatermarkTransformer;
import starlock.obf.obfuscator.transformers.impl.watermark.MetaInfTransformer;
import starlock.obf.obfuscator.transformers.impl.watermark.WClassTransformer;

public class WaterMarkTransformer extends Transformer {
    @Override
    public void transform(Obfuscator obfuscator) {
        if(getConfig().getBoolean("Watermark.META-INF")){
            new MetaInfTransformer().obfuscate(obfuscator);
        }
        if (getConfig().getBoolean("Watermark.UnsafeInvokeDynamic")) {
            new InvokeDynamicWatermarkTransformer().obfuscate(obfuscator);
        }
        //if(getConfig().getBoolean("Watermark.META-INF")){
        //    new MetaInfTransformer().obfuscate(obfuscator);
        //}
        new ConsoleTransformer().obfuscate(obfuscator);
        //new WClassTransformer().obfuscate(obfuscator);
    }
}
