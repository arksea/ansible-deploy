package net.arksea.ansible.deploy.api.auth.service;

import net.arksea.ansible.deploy.api.auth.entity.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

/**
 * 密码加密与比较
 * Create by xiaohaixing on 2020/4/30
 */
@Component("credentialsMatcher")
public class CredentialsMatcherImpl implements CredentialsMatcher {
    private static Logger logger = LogManager.getLogger(CredentialsMatcherImpl.class);

    private static final SecureRandom secureRandom;
    private static final SecretKeyFactory secretKeyFactory;
    private static final int KEY_ITERATION_COUNT = 10000;
    private static final int KEY_BITS = 256;

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("create SecretKeyFactory failed", e);
        }
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        UsernamePasswordToken t = (UsernamePasswordToken) token;
        SimpleAuthenticationInfo i = (SimpleAuthenticationInfo) info;
        User user = (User)i.getCredentials();
        String targetPwd = user.getPassword();
        return match(t.getPassword(), targetPwd.toCharArray(), user.getSalt());
    }

    public static boolean match(char[] pwd, char[] pwdHash, String salt) {
        try {
            String pwdhash = hashPassword(pwd, salt);
            return slowEquals(pwdhash.toCharArray(), pwdHash);
        } catch (Exception ex) {
            logger.warn("match password failed", ex);
            return false;
        }
    }

    public static String hashPassword(char[] pwd, String salt) throws Exception {
        final KeySpec keySpec = new PBEKeySpec(pwd, Base64.decodeBase64(salt), KEY_ITERATION_COUNT, KEY_BITS);
        final SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        byte[] bytes = secretKey.getEncoded();
        return Base64.encodeBase64String(bytes);
    }

    /**
     * 比较的时间与密码长度不为线性关系
     * @param a
     * @param b
     * @return
     */
    private static boolean slowEquals(char[] a, char[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) diff |= a[i] ^ b[i];
        return diff == 0;
    }

    public static String createSalt() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.encodeBase64String(bytes);
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

    //生成CookieRememberMeManager.cipherKey
    public static void main(String[] args) {
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        System.out.println((org.apache.shiro.codec.Base64.encodeToString(bytes)));

        String c = "4AvVhmFLUs0KTA3Kprsdag==";
        System.out.println(org.apache.shiro.codec.Base64.decode(c).length);
    }

}
