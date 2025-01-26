package starlock.obf.obfuscator.transformers;

import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.natives.NativeAuthTransformer;

import java.util.ArrayList;
import java.util.List;

public abstract class NativeTransformer extends Transformer {
    @Override
    public void transform(Obfuscator obfuscator) {
        List<NativeTransformer> transformers = new ArrayList<>();
        if(getConfig().getBoolean("NativeObfuscation.Auth")) transformers.add(new NativeAuthTransformer());
        transformers.forEach(transformer -> transformer.init(obfuscator));
    }
    public abstract void init(Obfuscator obfuscator);
}
