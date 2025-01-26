package starlock.obf.obfuscator.transformers;

import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.string.HeavyStringTransformer;

public class StringTransformer  extends Transformer {
    @Override
    public void transform(Obfuscator obfuscator) {
        switch (getConfig().getString("StringEncryption.Mode")){
            case "Normal" -> new HeavyStringTransformer().obfuscate(obfuscator);
            case "Heavy" -> new HeavyStringTransformer().obfuscate(obfuscator);
            default -> throw new IllegalArgumentException();
        }
    }
}