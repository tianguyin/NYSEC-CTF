import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class enCryption {
    public static String hash(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
    public static StringBuilder salt(String token, String salt) throws NoSuchAlgorithmException {
        int numParts = salt.length();
        int tokenLength = token.length();
        String[] parts = new String[numParts];
        int spliteParts = tokenLength / numParts;
        StringBuilder entoken = new StringBuilder();
        for (int i = 0; i < numParts; i++) {
            int startIndex = i * spliteParts;
            int endIndex = (i == numParts - 1) ? token.length() : (i + 1) * spliteParts;
            parts[i] = token.substring(startIndex, endIndex);
        }
        for (int i = 0; i < parts.length; i++) {
            entoken.append(parts[i]).append(salt);

        }
        // 打印切分后的部分
        return entoken;
    }
    public static StringBuilder unsalt(String token,String salt) throws NoSuchAlgorithmException {
        int numParts = salt.length();
        int tokenLength = token.length();
        String[] parts = new String[numParts];
        int spliteParts = tokenLength / numParts;
        StringBuilder untoken = new StringBuilder();
        for (int i = 0; i < numParts; i++) {
            int startIndex = i * spliteParts;
            int endIndex = (i == numParts - 1) ? token.length() : (i + 1) * spliteParts;
            parts[i] = token.substring(startIndex, endIndex);
        }
        for (int i = 0; i < parts.length; i++) {
            untoken.append(parts[i]);
            untoken.delete(untoken.length()-numParts, untoken.length());
        }

        return untoken;
    }
    public static String token(String[] Info) {
        StringBuilder tokenBuilder = new StringBuilder();
        for (int i = 0; i < Info.length; i++) {
            String part = Base64.getEncoder().encodeToString(Info[i].getBytes(StandardCharsets.UTF_8));
            tokenBuilder.append(part);
            if (i < Info.length - 1) {
                tokenBuilder.append(",");
            }
        }
        return tokenBuilder.toString();
    }
}
