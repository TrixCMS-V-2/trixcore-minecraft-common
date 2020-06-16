package eu.trixcms.trixcore.common.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class JsonEncryptedRequest {

    private final String data;

    private final String iv;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonEncryptedRequest that = (JsonEncryptedRequest) o;
        return Objects.equals(data, that.data) &&
                Objects.equals(iv, that.iv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, iv);
    }
}
