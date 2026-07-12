package ros.domain.model.Auxiliary;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordHasher implements HasherInterface {
    
    String encodeSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");            
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo SHA-256 não encontrado no sistema.", e);
        }
    }

    @Override
    public String encode(String password) {
        return encodeSha256(password);
    }

    @Override
    public boolean match(String hashedString, String unhashedString) {
        return (hashedString.equals(encode(unhashedString)));
    }
}
