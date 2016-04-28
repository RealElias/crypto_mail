package crypto_mail.service.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by elias on 28.4.16.
 */
public class MD5Util {
    public static String md5Hash(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(password.getBytes(), 0, password.length());
            return new BigInteger(1, messageDigest.digest()).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
