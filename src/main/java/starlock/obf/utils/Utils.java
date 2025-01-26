package starlock.obf.utils;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import starlock.obf.Main;
import starlock.obf.manager.ConfigManager;
import starlock.obf.manager.DictonaryManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils extends ConfigManager {
    private static final String Dict;
    static {
        switch (getConfig().getString("Settings.Dictionary")){
            case "IiIiI" -> Dict = DictonaryManager.IiIiI;
            case "Invisible" -> Dict = DictonaryManager.IiIiI;
            case "Split" -> Dict = DictonaryManager.IiIiI;
            default -> Dict = DictonaryManager.Default;
        }
    }
    public static String randomString(int length) {
        String regex = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        char[] chars = regex.toCharArray();
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < length; ++i) {
            int c = (int)(Math.random() * (double)chars.length);
            builder.append(chars[c]);
        }

        return builder.toString();
    }
    public static String getRandomString(int length, int repeat) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            s.append(IntStream.range(0, length)
                    .mapToObj(ii -> Character.toString(Dict.charAt((new Random()).nextInt(1999999999) % Dict.length())))
                    .collect(Collectors.joining()));
        }
        return new String(s.toString().getBytes(StandardCharsets.UTF_8));
    }
    public static String getRandomString(int length) {
        StringBuilder s = new StringBuilder();
        s.append(IntStream.range(0, length)
                .mapToObj(ii -> Character.toString(Dict.charAt((new Random()).nextInt(1999999999) % Dict.length())))
                .collect(Collectors.joining()));
        return new String(s.toString().getBytes(StandardCharsets.UTF_8));
    }
    private static String getTimestamp(){
        ZoneId moscowZoneId = ZoneId.of("Europe/Moscow");
        ZonedDateTime moscowTime = ZonedDateTime.now(moscowZoneId);
        Instant moscowInstant = moscowTime.toInstant();
        long unixTimestamp = moscowInstant.getEpochSecond();
        return Long.toString(unixTimestamp);
    }

    public static void updateMainClass(String newMainClass) {
        byte[] manifestData = Main.FILES.get("META-INF/MANIFEST.MF");
        if (manifestData == null) {
            System.out.println("Manifest file not found.");
            return;
        }

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(manifestData);
            Manifest manifest = new Manifest(inputStream);
            Attributes mainAttributes = manifest.getMainAttributes();
            mainAttributes.putValue("Main-Class", newMainClass);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            manifest.write(outputStream);
            Main.FILES.put("META-INF/MANIFEST.MF", outputStream.toByteArray());
            //System.out.println("Main-Class updated to: " + newMainClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getMainManifest(){
        byte[] manifestData;
        manifestData = Main.FILES.get("META-INF/MANIFEST.MF");
        if (manifestData == null) return null;

        return Arrays.stream(new String(manifestData).split("\n"))
                .filter(s -> s.startsWith("Main-Class: "))
                .findFirst()
                .orElse("StarLockNotFoundMainInManifest")
                .trim()
                .split(": ")[1]
                .replaceAll("\\.", "/");
    }
    public static byte[] classToBytes(ClassNode classNode) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
    public static final String[] colors = new String[]{"31m", "32m", "33m", "34m", "35m", "36m", "37m", "38m", "39m"};
    @SneakyThrows
    public static boolean isClassFile(@NonNull String entryName, byte @NonNull [] buffer) {
        return (entryName.endsWith(".class") && buffer.length >= 4
                        && String.format("%02X%02X%02X%02X", buffer[0], buffer[1], buffer[2], buffer[3]
        ).equalsIgnoreCase("cafebabe"));
    }
}
