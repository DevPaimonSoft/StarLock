package starlock.obf.obfuscator;

import starlock.obf.obfuscator.transformers.*;
import starlock.obf.utils.ASMHelper;
import org.objectweb.asm.tree.*;
import starlock.obf.utils.wrapper.ClassWrapper;

import java.util.*;

public class Obfuscator extends ASMHelper {
    public void run() {
        List<Transformer> transformers = Arrays.asList(
                new StringTransformer(),
                new NumberTransformer(),
                ////new InvokeDynamicTransformer(), //TODO: In Future Update >:)
                new ControlFlowTransformer(),
                new RenamerTransformer(),
                new AccessTransformer(),
                new WaterMarkTransformer(),
                new MiscellaneousTransformer(),
                new PoolTransformer()
        );
        LOGGER.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        LOGGER.info("      Obfuscation starting...\n");
        getClasses().forEach(classNode -> {
            LOGGER.info("      Transforming {}", classNode.name);
            transformers.forEach(transformer -> transformer.transform(classNode));
        });
        LOGGER.info("\n      Obfuscation finished!");
        LOGGER.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =\n");
    }

    public List<ClassNode> getClasses(){
        List<ClassNode> toReturn = new ArrayList<>();
        CLASSES.forEach((name, c) -> toReturn.add(c.getClassNode()));
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