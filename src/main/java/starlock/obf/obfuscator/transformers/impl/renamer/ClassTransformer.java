package starlock.obf.obfuscator.transformers.impl.renamer;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;
import starlock.obf.obfuscator.Obfuscator;
import starlock.obf.obfuscator.transformers.RenamerTransformer;
import starlock.obf.utils.CustomRemapper;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClassTransformer extends RenamerTransformer {

    public void obfuscate(Obfuscator obfuscator) {
        List<String> names = getConfig().getStringList("Renamer.Path");
        AtomicInteger ipp = new AtomicInteger();
        CustomRemapper remapper = new CustomRemapper();

        Map<String, ClassNode> updated = new HashMap<>();
        Map<String, ClassNode> old = new HashMap<>();

        obfuscator.getClasses().forEach(remapper::loadClass);

        obfuscator.getClasses().forEach(classNode -> {
            classNode.sourceFile = getRandomString(new Random().nextInt(30, 100), new Random().nextInt(1, 3));
            classNode.sourceDebug = getRandomString(new Random().nextInt(30, 100), new Random().nextInt(1, 3));

            String newName = getRandomString(new Random().nextInt(30, 100), new Random().nextInt(1, 3));
            String newPackage = getConfig().getString("Renamer.Repackage");
            String oldPackage = getPackage(classNode.name);
            String fullNewName = oldPackage + "/" + newName;
            RENAMEDCLASSES.put(classNode.name + ".class", fullNewName);
            remapper.map(classNode.name, fullNewName);

            //System.out.println(classNode.name + "  ->  " + newPackage + fullNewName.replaceAll(getPackage(fullNewName), ""));

            //remapper.mapPackage(getPackage(classNode.name), newPackage);

            classNode.name = fullNewName;

            ipp.getAndIncrement();
        });
        LOGGER.info("Info->");
        LOGGER.info("  - Renamed: " + ipp.get());

        obfuscator.getClasses().forEach(ban -> {
            ClassNode newNode = new ClassNode();
            ClassRemapper classRemapper = new ClassRemapper(newNode, remapper);

            ban.accept(classRemapper);
            updated.put(newNode.name, newNode);
        });

        updated.forEach((name, data) -> {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            data.accept(writer);
            obfuscator.setClass(name, writer.toByteArray());
        });
        remapper.updateReferences();
    }

    public static String getPackage(String classPath) {
        int lastSlashIndex = classPath.lastIndexOf('/');
        if (lastSlashIndex == -1) {
            return "";
        }
        return classPath.substring(0, lastSlashIndex);
    }
}
