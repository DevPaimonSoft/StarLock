package starlock.obf.obfuscator.transformers;

import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.number.HeavyNumberTransformer;
import starlock.obf.obfuscator.transformers.impl.ref.HeavyInvokeDynamic;

public class NumberTransformer extends Transformer {
    @Override
    public void transform(Obfuscator obfuscator) {
        switch (getConfig().getString("NumberObfuscation.Mode")){
            case "Normal" -> new HeavyInvokeDynamic().init(obfuscator);
            case "Heavy" -> new HeavyNumberTransformer().obfuscate(obfuscator);
            default -> throw new IllegalArgumentException();
        }
    }
}
