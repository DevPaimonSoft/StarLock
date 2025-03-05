package starlock.obf.obfuscator.transformers.impl.watermark;

import org.objectweb.asm.tree.*;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.WaterMarkTransformer;

public class ConsoleTransformer extends WaterMarkTransformer {
    public void obfuscate(ClassNode classNode){
        Obfuscator obfuscator = new Obfuscator();
        String mainClass = null;

        byte[] data = obfuscator.getFile("META-INF/MANIFEST.MF");
        if(data != null){
            String manifest = new String(data);
            manifest = manifest.substring(0, manifest.length() - 2);
            String[] list = manifest.split("\n");

            for(String str : list){
                if(str.startsWith("Main-Class")) mainClass = str.replace("Main-Class: ","");
            }
            if(mainClass != null){
                if(mainClass.contains(classNode.name.replace("/","."))){
                    classNode.methods.stream()
                            .filter(methodNode -> methodNode.name.equals("main"))
                            .filter(methodNode -> methodNode.desc.equals("([Ljava/lang/String;)V"))
                            .forEach(methodNode ->{
                                InsnList insnList = new InsnList();
                                getConfig().getStringList("Watermark.Messages").forEach(str -> {
                                    insnList.add(new FieldInsnNode(GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;"));
                                    insnList.add(new LdcInsnNode(str.replace("\\","\\\\")));
                                    insnList.add(new MethodInsnNode(INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V"));
                                });
                                AbstractInsnNode insn = methodNode.instructions.get(0);
                                methodNode.instructions.insertBefore(insn, insnList);
                            });
                }
            }
        }
    }
}
