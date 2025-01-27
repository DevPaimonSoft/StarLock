package starlock.obf.obfuscator.transformers;

import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.flow.LightControlFlowTransformer;
import starlock.obf.obfuscator.transformers.impl.flow.NormalControlFlowTransformer;

public class ControlFlowTransformer extends Transformer {
    @Override
    public void transform(Obfuscator obfuscator) {
        switch (getConfig().getString("FlowObfuscation.Mode")){
            case "Normal" -> new LightControlFlowTransformer().obfuscate(obfuscator);
            case "Medium" -> new NormalControlFlowTransformer().obfuscate(obfuscator);
            case "Heavy" -> new NormalControlFlowTransformer().obfuscate(obfuscator);
            default -> throw new IllegalArgumentException();
        }
    }
}
