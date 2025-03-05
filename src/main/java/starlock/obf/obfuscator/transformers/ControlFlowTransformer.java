package starlock.obf.obfuscator.transformers;

import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.flow.LightControlFlowTransformer;
import starlock.obf.obfuscator.transformers.impl.flow.NormalControlFlowTransformer;

public class ControlFlowTransformer extends Transformer {
    @Override
    public void transform(ClassNode classnode) {
        switch (getConfig().getString("FlowObfuscation.Mode")){
            case "Normal" -> new NormalControlFlowTransformer().obfuscate(classnode);
            case "Medium" -> new NormalControlFlowTransformer().obfuscate(classnode);
            case "Heavy" -> new NormalControlFlowTransformer().obfuscate(classnode);
            default -> throw new IllegalArgumentException();
        }
    }
}
