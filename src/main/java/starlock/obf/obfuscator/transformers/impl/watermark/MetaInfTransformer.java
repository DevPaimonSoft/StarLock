package starlock.obf.obfuscator.transformers.impl.watermark;

import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.WaterMarkTransformer;

public class MetaInfTransformer extends WaterMarkTransformer {
    public void obfuscate(ClassNode ignored){
        if(!watermarkInjection) {
            Obfuscator obfuscator = new Obfuscator();
            byte[] data = obfuscator.getFile("META-INF/MANIFEST.MF");
            if (data != null) {
                final String[] manifest = {new String(data)};
                manifest[0] = manifest[0].substring(0, manifest[0].length() - 2);
                manifest[0] += "Re-Build: starlock.StarLock Obfuscator";
                getConfig().getStringList("Watermark.MetaInfMsg").forEach(str -> manifest[0] += ("\n" + str));

                data = manifest[0].getBytes();
                obfuscator.setFile("META-INF/MANIFEST.MF", data);
            }
        }
    }
}
