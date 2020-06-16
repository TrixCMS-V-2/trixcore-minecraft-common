package eu.trixcms.trixcore.common.request;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class DecryptRequest {

    private final String key;
    private final JsonEncryptedRequest request;

    public DecryptRequest(JsonEncryptedRequest request, String key) {
        this.key = key;
        this.request = request;
    }

    public String data() throws Exception {
        return decrypt(request.data(), request.iv());
    }

    private String decrypt(String value, String initVector) throws Exception {
        if (initVector == null || initVector.isEmpty())
            throw new Exception();

        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(value));

        return new String(original);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecryptRequest that = (DecryptRequest) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(request, that.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, request);
    }
}
