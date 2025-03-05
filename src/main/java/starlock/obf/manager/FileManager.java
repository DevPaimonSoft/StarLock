package starlock.obf.manager;

import lombok.Setter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import starlock.obf.Main;
import starlock.obf.obfuscator.transformers.NumberTransformer;
import starlock.obf.obfuscator.transformers.StringTransformer;
import starlock.obf.obfuscator.transformers.impl.number.HeavyNumberTransformer;
import starlock.obf.obfuscator.transformers.impl.string.HeavyStringTransformer;
import starlock.obf.utils.ASMHelper;
import starlock.obf.utils.Utils;
import starlock.obf.utils.wrapper.ClassWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileManager extends Utils {
    public static List<ClassWrapper> LIBS = null;
    public static final Map<String,ClassWrapper> CLASSES = new ConcurrentHashMap<>();
    public static final Map<String, String> RENAMEDCLASSES = new ConcurrentHashMap<>();
    public static final Map<String, byte[]> FILES = new ConcurrentHashMap<>();
    @Setter
    private static File inputFile,outputFile,path;

    public static void loadLibs(){
        LIBS = new ArrayList<>();
        for (File file : path.listFiles()) {
            if (!file.isDirectory()) {
                try {
                    ZipFile zipFile = new ZipFile(file);
                    zipFile.entries().asIterator().forEachRemaining(zipEntry -> {
                                try {
                                    var is = zipFile.getInputStream(zipEntry);
                                    var name = zipEntry.getName();
                                    var buffer = is.readAllBytes();

                                    if (isClassFile(name, buffer)) LIBS.add(new ClassWrapper(buffer));

                                    //if(name.endsWith(".class")){
                                    //    ClassesBytes.put(name, buffer);
                                    //}

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                    );
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    public static void parseFile(){
        try {
            ZipFile zipFile = new ZipFile(inputFile);
            zipFile.entries().asIterator().forEachRemaining(zipEntry -> {
                        try {
                            var is = zipFile.getInputStream(zipEntry);
                            var name = zipEntry.getName();
                            var buffer = is.readAllBytes();

                            if (isClassFile(name, buffer)) {
                                ClassWrapper classWrapper = new ClassWrapper(buffer);
                                if(!ASMHelper.isAccess(classWrapper.getClassNode().access, Opcodes.ACC_INTERFACE))
                                    CLASSES.put(name, new ClassWrapper(buffer));
                                else FILES.put(name, buffer);
                            }
                            else FILES.put(name, buffer);

                            //if(name.endsWith(".class")){
                            //    ClassesBytes.put(name, buffer);
                            //}

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static void saveOutput() {
        try (ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(outputFile))) {
            System.gc();

            String mainClass = "starlock/Main";
            try {
                mainClass = getMainManifest();
            } catch (Exception e) {}

            String finalMainClass = mainClass;

            CLASSES.forEach((name, wrapper) -> {
                try {
                    String finalName = wrapper.getName();
                    if (RENAMEDCLASSES.containsKey(name)) {
                        if(finalMainClass.equals(name.replace(".class",""))){
                            updateMainClass(RENAMEDCLASSES.get(name).replaceAll("/", "\\."));
                        }
                        finalName = RENAMEDCLASSES.get(name);
                        //System.out.println("New Name: " + finalName);
                    }
                    zipFile.putNextEntry(new ZipEntry(finalName + ".class"));
                    byte[] toObf = classToBytes(wrapper.getClassNode());
                    zipFile.write(toObf);
                    zipFile.closeEntry();
                } catch (Throwable throwable) {
                    // Handle exceptions if necessary
                }
            });
            FILES.forEach((name, buffer) -> {
                if (name.endsWith("/"))
                    return;
                try {
                    zipFile.putNextEntry(new ZipEntry(name));
                    zipFile.write(buffer);
                    zipFile.closeEntry();
                } catch (Throwable throwable) {
                    //throwable.printStackTrace();
                }
            });



            //TODO: save lib and replace values
            ClassNode classNode = getSLClassNode();

            classNode.methods.forEach(methodNode -> {
                Arrays.stream(methodNode.instructions.toArray())
                        .forEach(insn -> {
                            if (ASMHelper.isInteger(insn) && ASMHelper.getInteger(insn) == 1122331334) {
                                methodNode.instructions.set(insn, new LdcInsnNode(NumberTransformer.key));
                            } else if (ASMHelper.isInteger(insn) && ASMHelper.getInteger(insn) == 345345777) {
                                methodNode.instructions.set(insn, new LdcInsnNode(StringTransformer.key));
                            } else if (ASMHelper.isString(insn) && ASMHelper.getString(insn).equals("SPLITSTRING")) {
                                methodNode.instructions.set(insn, new LdcInsnNode("nullptr"));
                            }
                        });
                if (methodNode.invisibleAnnotations == null) {
                    methodNode.invisibleAnnotations = new ArrayList<>();
                }
                methodNode.localVariables = null;
                //methodNode.invisibleAnnotations.add(new AnnotationNode("starlock.StarLock(NativeTransformer)"));
            });

            zipFile.putNextEntry(new ZipEntry(classNode.name + ".class"));
            zipFile.write(classToBytes(classNode));
            zipFile.closeEntry();


            System.gc();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }
    public static ClassNode getSLClassNode() {
        try {
            InputStream stream = Main.class.getResourceAsStream("../StarLock.class");
            ClassNode classNode = new ClassNode(Opcodes.ASM9);
            ClassReader reader = new ClassReader(stream);
            reader.accept(classNode, Opcodes.ASM9);
            return classNode;
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
