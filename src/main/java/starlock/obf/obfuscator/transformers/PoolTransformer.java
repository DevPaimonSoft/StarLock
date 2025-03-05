package starlock.obf.obfuscator.transformers;

import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.pools.NumberPoolTransformer;
import starlock.obf.obfuscator.transformers.impl.pools.StringPoolTransformer;

public class PoolTransformer extends Transformer {
    @Override
    public void transform(ClassNode classnode) {
        if(!isAccess(classnode.access, ACC_ENUM)) {
            if (getConfig().getBoolean("PoolTransformer.String"))
                new StringPoolTransformer().obfuscate(classnode);
            if (getConfig().getBoolean("PoolTransformer.Number"))
                new NumberPoolTransformer().obfuscate(classnode);
        }
    }
}