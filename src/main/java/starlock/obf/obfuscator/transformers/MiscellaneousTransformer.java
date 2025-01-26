package starlock.obf.obfuscator.transformers;

import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.Transformer;
import starlock.obf.obfuscator.transformers.impl.miscellaneous.*;

import java.util.ArrayList;
import java.util.List;

public class MiscellaneousTransformer extends Transformer {
    @Override
    public void transform(Obfuscator obfuscator) {
        List<MiscellaneousTransformer> transformers = new ArrayList<>();
        //if(getConfig().getInt("Miscellaneous.TrashClasses") != 0) transformers.add(new TrashClassesTransformer());
        //if(getConfig().getBoolean("Miscellaneous.Source.Enabled")) transformers.add(new SourceTransformer());
        //if(getConfig().getBoolean("Miscellaneous.Shuffler")) transformers.add(new ShufflerTransformer());

        transformers.forEach(transformer -> transformer.transform(obfuscator));
    }
}
