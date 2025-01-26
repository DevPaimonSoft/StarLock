package starlock.obf.obfuscator.transformers;

import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.renamer.ClassTransformer;
import starlock.obf.obfuscator.transformers.impl.renamer.LocalVaribleTransformer;

public class RenamerTransformer extends Transformer {
    @Override
    public void transform(Obfuscator obfuscator) {
        new LocalVaribleTransformer().obfuscate(obfuscator);
        new ClassTransformer().obfuscate(obfuscator);
    }
}