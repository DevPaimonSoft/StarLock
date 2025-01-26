package starlock;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Base64;

public class StarUtils {
    public static Long getTimestamp(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 2) {
            StackTraceElement caller = stackTrace[2];
            if(!caller.getClassName().startsWith("starlock.Star") || !caller.getMethodName().equals("onEnable")
                    || !caller.isNativeMethod()
                    || !stackTrace[1].isNativeMethod())
                throw new RuntimeException("[X] Invalid reference address!");
            else {
                ZoneId moscowZoneId = ZoneId.of("Europe/Moscow");
                ZonedDateTime moscowTime = ZonedDateTime.now(moscowZoneId);
                Instant moscowInstant = moscowTime.toInstant();
                return moscowInstant.getEpochSecond();
            }
        }
        throw new RuntimeException("[X] Invalid reference address!");
    }
    public static byte[] encrypt(byte[] data) throws Exception {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 2) {
            StackTraceElement caller = stackTrace[2];
            if(!caller.getClassName().equals("starlock.Star") || !caller.getMethodName().equals("onEnable")
                    || !caller.isNativeMethod()
                    || !stackTrace[1].isNativeMethod())
                throw new RuntimeException("[X] Invalid reference address!");
            else {
                byte[] password = hash(data,"MD5").getBytes();
                rotateArray(data, 4, 1);

                data = encrypt(data, password, "RC4",16);

                rotateArray(data, 2, 2);
                rotateArray(password, 9, 1);

                int key = hashCode(new String(password));

                for(int i = 0; i < data.length; ++i)
                    data[i] = (byte) ((data[i] ^ password[i % password.length]) ^ key);

                data = encrypt(data, password, "RC4",16);
                rotateArray(password, 3, 2);
                rotateArray(data, 6, 2);

                byte[] rot = (Base64.getEncoder().encodeToString(data)+"PEnIs"+Base64.getEncoder().encodeToString(password)).getBytes();
                return rotl(rot, rot.length % 63);
            }
        }
        throw new RuntimeException("[X] Invalid reference address!");
    }

    public static byte[] decrypt(byte[] data) throws Exception {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 2) {
            StackTraceElement caller = stackTrace[2];
            if(!caller.getClassName().equals("starlock/Main") || !caller.getMethodName().equals("onEnable")
                    || !caller.isNativeMethod()
                    || !stackTrace[1].isNativeMethod())
                throw new RuntimeException("[X] Invalid reference address!");
            else {
                data = rotr(data, data.length % 63);

                String[] dataStr = new String(data).split("PEnIs");
                byte[] password = Base64.getDecoder().decode(dataStr[1].getBytes());
                data = Base64.getDecoder().decode(dataStr[0].getBytes());

                rotateArray(data, 6, 1);
                rotateArray(password, 3, 1);
                data = decrypt(data, password, "RC4",16);

                int key = hashCode(new String(password));

                for (int i = 0; i < data.length; ++i)
                    data[i] = (byte) ((data[i] ^ password[(i) % password.length]) ^ key);

                rotateArray(data, 2, 1);
                rotateArray(password, 9, 2);

                data = decrypt(data, password, "RC4",16);

                rotateArray(data, 4, 1);

                return data;
            }
        }
        throw new RuntimeException("[X] Invalid reference address!");
    }

    private static int hashCode(String str){
        byte[] value = str.getBytes();

        int h = 7;
        int prime = 37;
        int length = value.length >> 1;
        for (int i = 0; i < length; i++) {
            h = 31 * h + getChar(value, i) | i;
            h = prime * h + (value[i] ^ (i * prime));
        }

        return h;
    }
    private static char getChar(byte[] val, int index) {
        assert index >= 0 && index < val.length : "Trusted caller missed bounds check";
        index <<= 1;
        return (char)(((val[index++] & 0xff) << 8) |
                ((val[index] & 0xff)));
    }
    private static byte[] encrypt(byte[] data, byte[] password, String algorithm, int ivLen) throws Exception {
        byte[] iv = new byte[ivLen];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        SecretKey key = generateKey(new String(password), algorithm);

        Cipher cipher = Cipher.getInstance(algorithm.equals("RC4") ? algorithm : algorithm+"/CBC/PKCS5Padding");
        if(algorithm.equals("RC4"))
            cipher.init(Cipher.ENCRYPT_MODE, key);
        else
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        byte[] encryptedData = cipher.doFinal(data);

        byte[] result = new byte[ivLen + encryptedData.length];
        System.arraycopy(iv, 0, result, 0, ivLen);
        System.arraycopy(encryptedData, 0, result, ivLen, encryptedData.length);

        return Base64.getEncoder().encode(result);
    }
    private static byte[] decrypt(byte[] encryptedData, byte[] password, String algorithm, int ivLen) throws Exception {
        if (!algorithm.equals("RC4") && encryptedData.length < ivLen) {
            throw new IllegalArgumentException("Invalid encrypted data length: less than IV length");
        }
        encryptedData = Base64.getDecoder().decode(encryptedData);

        byte[] iv = Arrays.copyOfRange(encryptedData, 0, ivLen);
        byte[] actualData = Arrays.copyOfRange(encryptedData, ivLen, encryptedData.length);

        SecretKey key = generateKey(new String(password), algorithm);

        Cipher cipher = Cipher.getInstance(algorithm.equals("RC4") ? algorithm : algorithm+"/CBC/PKCS5Padding");
        if(algorithm.equals("RC4"))
            cipher.init(Cipher.DECRYPT_MODE, key);
        else
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(actualData);
    }
    private static SecretKey generateKey(String password, String algorithm) throws Exception {
        byte[] keyBytes = password.getBytes(StandardCharsets.UTF_8);
        int keySize = algorithm.equalsIgnoreCase("AES") ? 16 : Math.min(keyBytes.length, 56);
        byte[] key = Arrays.copyOf(keyBytes, keySize);
        return new SecretKeySpec(key, algorithm);
    }
    private static String hash(byte[] input, String type) {
        try {
            MessageDigest digest = MessageDigest.getInstance(type);
            byte[] encodedHash = digest.digest(input);

            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    private static byte[] rotl(byte[] array, int n) {
        int length = array.length;
        n %= length;
        byte[] shifted = new byte[length];
        System.arraycopy(array, n, shifted, 0, length - n);
        System.arraycopy(array, 0, shifted, length - n, n);

        return shifted;
    }
    private static byte[] rotr(byte[] array, int n) {
        int length = array.length;
        n %= length;
        byte[] shifted = new byte[length];
        System.arraycopy(array, length - n, shifted, 0, n);
        System.arraycopy(array, 0, shifted, n, length - n);

        return shifted;
    }
    private static byte rotl8(byte value, int shift) {
        shift &= 7; // Ограничиваем сдвиг в пределах от 0 до 7 бит
        return (byte) ((value << shift) | (value & 0xFF) >>> (8 - shift));
    }
    private static byte rotr8(byte value, int shift) {
        shift &= 7; // Ограничиваем сдвиг в пределах от 0 до 7 бит
        return (byte) ((value & 0xFF) >>> shift | (value << (8 - shift)));
    }
    private static void rotateArray(byte[] array, int shift, int type) {
        for (int i = 0; i < array.length; i++) {
            if(type == 1){
                array[i] = rotl8(array[i], shift);
            } else if(type == 2) {
                array[i] = rotr8(array[i], shift);
            }
        }
    }
}
