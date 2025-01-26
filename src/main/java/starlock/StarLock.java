package starlock;

import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class StarLock {
    public static String decryptPool(String var0, String varKeys, int var3, int key) {
        char[] var1 = var0.toCharArray();
        char[] banan = varKeys.toCharArray();
        for(int var11 = 0; var11 < var1.length; ++var11) {
            var1[var11] ^= banan[var11 % banan.length];
        }
        char[] var17 = new char[var1.length];
        for(int var18 = 0; var18 < var17.length; ++var18) {
            switch (var18 % 6) {
                case 0 -> var17[var18] = (char) (var1[var18] ^ var18);
                case 1 -> var17[var18] = (char) (var3 ^ key ^ var1[var18]);
                case 2 -> var17[var18] = (char) (var18 ^ key ^ var1[var18]);
                case 3 -> var17[var18] = (char) (key ^ var1[var18]);
                case 4 -> var17[var18] = (char) (var1[var18] ^ key);
                case 5 -> var17[var18] = (char) (var3 ^ var18 ^ var1[var18]);
            }
        }
        return new String(var17);
    }
    public static String decrypt(String var0, int var1, int var2){
        switch (var1) {
            case 0 -> {
                int key = 1122331334;
                char[] str = var0.toCharArray();
                StringBuilder decrypted = new StringBuilder();
                for (char c : str) {
                    decrypted.append((char) ((int) c ^ key));
                }
                return decrypted.toString();
            }
            case 1 -> {
                int key = 345345777;
                char[] var4 = var0.toCharArray();
                char[] var5 = var0.toCharArray();
                for(int var6 = 0; var6 < var4.length; ++var6) {
                    var4[var6] = (char)(var5[var6] ^ var2);
                }
                char[] var7 = new char[var4.length];
                for(int var8 = 0; var8 < var7.length; ++var8) {
                    switch (var8 % 3) {
                        case 0 -> var7[var8] = (char) (var2 ^ key ^ var4[var8]);
                        case 1 -> var7[var8] = (char) (key ^ var4[var8]);
                        case 2 -> var7[var8] = (char) (var4[var8] ^ key);
                    }
                }
                return new String(var7);
            }
            default -> {
                return null;
            }
        }
    }
    public static Object decrypt(Object var0,  // Lookup
                                 Object var1,  // String
                                 Object var2,  // MethodType
                                 Object var3,  // Key
                                 int ignored) {// String
        String var4 = (String)var3;
        char[] var5 = (new String(Base64.getDecoder().decode(((String)var1).getBytes(StandardCharsets.UTF_8)))).toCharArray();
        char[] var6 = new char[var4.length()];
        char[] var7 = var4.toCharArray();

        for(int var8 = 0; var8 < var4.length(); ++var8) {
            var6[var8] = (char)(var7[var8] ^ var5[var8 % 16]);
        }

        byte[] var22 = (new String(var6)).getBytes();
        String var9 = new String(var22);
        String[] var10 = var9.split("SPLITSTRING");
        String className = var10[0];
        String name = var10[1];
        String desc = var10[2];
        //String var14 = var10[3];
        int type = Integer.parseInt(var10[3]);

        MethodHandle var16;
        try {
            Class<?> var17 = Class.forName(className);
            //Class<?> var18 = Class.forName(var14);
            ClassLoader var19 = StarLock.class.getClassLoader();
            MethodType var20 = MethodType.fromMethodDescriptorString(desc, var19);
            var16 = switch (type) {
                case 11111 -> ((MethodHandles.Lookup) var0).findStatic(var17, name, var20);
                case 22222 -> ((MethodHandles.Lookup) var0).findVirtual(var17, name, var20);
                //case 33333 -> ((MethodHandles.Lookup) var0).findSpecial(var17, name, var20, var18);
                default -> throw new BootstrapMethodError();
            };

            var16 = var16.asType((MethodType)var2);
        } catch (Exception var21) {
            var21.printStackTrace();
            throw new BootstrapMethodError();
        }

        return new ConstantCallSite(var16);
    }
    public static native void registerNativesForClass(int index, Class<?> clazz);
    public static native String get(int index);

    static {
        //System.loadLibrary(StarLock.class.getResource("StarLock.dll").getPath());
    }
}
