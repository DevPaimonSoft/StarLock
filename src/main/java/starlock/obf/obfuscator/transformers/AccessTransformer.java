package starlock.obf.obfuscator.transformers;

import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.access.ClassAccessTransformer;
import starlock.obf.obfuscator.transformers.impl.access.FieldAccessTransformer;
import starlock.obf.obfuscator.transformers.impl.access.MethodAccessTransformer;

public class AccessTransformer extends Transformer {
    @Override
    public void transform(Obfuscator obfuscator) {
        new MethodAccessTransformer().obfuscate(obfuscator);
        new FieldAccessTransformer().obfuscate(obfuscator);
        new ClassAccessTransformer().obfuscate(obfuscator);
    }
}