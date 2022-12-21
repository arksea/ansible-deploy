package net.arksea.ansible.deploy.api.system;

import org.apache.commons.codec.binary.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Create by xiaohaixing on 2020/4/30
 */
public class RandomUtils {

    private static final SecureRandom secureRandom;

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("create SecretKeyFactory failed", e);
        }
    }

    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        for (int i = 0; i < bArray.length; ++i) {
            String sTemp = Integer.toHexString(255 & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp);
        }
        return sb.toString();
    }

    public static String createRandom(int byteLen) {
        byte[] bytes = new byte[byteLen];
        secureRandom.nextBytes(bytes);
        return bytesToHexString(bytes);
    }
}
