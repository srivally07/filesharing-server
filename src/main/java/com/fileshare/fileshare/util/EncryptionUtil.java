package com.fileshare.fileshare.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class EncryptionUtil {

    private static final String ALGO = "AES";

    private static SecretKey secretKey;

    static {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGO);
            keyGen.init(128);
            secretKey = keyGen.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }
}