package com.example.app.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/29/19, MarkHuang,new
 * </ul>
 * @since 10/29/19
 */
public final class EncryptUtil {
    private EncryptUtil() {
        throw new AssertionError();
    }

    public static String encryptMd5(String s) throws NoSuchAlgorithmException {
        return encryptMd5(s.getBytes());
    }

    public static String encryptMd5(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);
        return new BigInteger(1, md.digest()).toString(16);
    }
}
