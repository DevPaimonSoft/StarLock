package starlock.obf.obfuscator.transformers;

import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.pools.NumberPoolTransformer;
import starlock.obf.obfuscator.transformers.impl.pools.StringPoolTransformer;

public class PoolTransformer extends Transformer {
    @Override
    public void transform(Obfuscator obfuscator) {
        if(getConfig().getBoolean("PoolTransformer.String")) new StringPoolTransformer().obfuscate(obfuscator);
        if(getConfig().getBoolean("PoolTransformer.Number")) new NumberPoolTransformer().obfuscate(obfuscator);
    }
}