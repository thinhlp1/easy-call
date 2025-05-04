package thinhlp.easycall;


import java.security.MessageDigest;
import java.security.SecureRandom;
import android.util.Base64;
import java.nio.charset.StandardCharsets;


   
public class PKCEUtil {

    public static String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] code = new byte[32];
        secureRandom.nextBytes(code);
        return base64UrlEncode(code);
    }

    public static String generateCodeChallenge(String codeVerifier) {
        try {
            byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes, 0, bytes.length);
            byte[] digest = md.digest();
            return base64UrlEncode(digest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate code challenge", e);
        }
    }

    private static String base64UrlEncode(byte[] input) {
        return Base64.encodeToString(input, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
    }
}
