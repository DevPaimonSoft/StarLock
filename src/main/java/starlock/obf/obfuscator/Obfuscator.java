package starlock.obf.obfuscator;

import starlock.obf.obfuscator.transformers.*;
import starlock.obf.utils.ASMHelper;
import org.objectweb.asm.tree.*;
import starlock.obf.utils.wrapper.ClassWrapper;

import java.util.*;

public class Obfuscator extends ASMHelper {
    public void run() {
        List<Transformer> transformers = new ArrayList<>();
        transformers.add(new StringTransformer());
        transformers.add(new NumberTransformer());

        //transformers.add(new InvokeDynamicTransformer()); //TODO: In future update

        transformers.add(new ControlFlowTransformer());
        transformers.add(new RenamerTransformer());
        transformers.add(new AccessTransformer());
        transformers.add(new WaterMarkTransformer());
        transformers.add(new MiscellaneousTransformer());
        transformers.add(new PoolTransformer());

        transformers.forEach(transformer -> {
            LOGGER.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
            LOGGER.info("      {} running...", transformer.name());
            transformer.transform(this);
            LOGGER.info("      {} finished!", transformer.name());
            LOGGER.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =\n");
        });
    }

    public List<ClassNode> getClasses(){
        List<ClassNode> toReturn = new ArrayList<>();
        CLASSES.forEach((a, c) -> toReturn.add(c.getClassNode()));
        return toReturn;
    }
    public byte[] getFile(String forGet){
        final byte[][] toReturn = {null};
        FILES.forEach((name, data) -> {
            if(name.equals(forGet))
                toReturn[0] = data;
        });
        return toReturn[0];
    }
    public void setFile(String name, byte[] data){
        FILES.put(name, data);
    }
    public void setClass(String name, byte[] data){
        CLASSES.put(name, new ClassWrapper(data));
    }
    public void delFile(String name){
        FILES.remove(name);
    }
    public void delClass(String name){
        CLASSES.remove(name);
    }

}