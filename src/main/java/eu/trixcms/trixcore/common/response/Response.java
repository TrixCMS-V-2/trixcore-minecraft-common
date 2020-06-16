package eu.trixcms.trixcore.common.response;

import eu.trixcms.trixcore.api.response.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Objects;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class Response implements IResponse {

    private int statusCode;

    private Object data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return statusCode == response.statusCode &&
                Objects.equals(data, response.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, data);
    }
}
