package eu.trixcms.trixcore.common.request;

import eu.trixcms.trixcore.api.request.IRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class JsonRequest implements IRequest {

    private final String method;

    private final String[] args;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonRequest that = (JsonRequest) o;
        return Objects.equals(method, that.method) &&
                Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(method);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }
}
